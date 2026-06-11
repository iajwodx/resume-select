<template>
  <div class="edit-page">
    <div class="edit-card glass-card">
      <div class="edit-header">
        <div class="edit-title-row">
          <div class="edit-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </div>
          <h2 class="edit-title">编辑简历</h2>
        </div>
      </div>

      <el-form v-if="form" ref="formRef" :model="form" label-width="120px" class="edit-form">
        <div class="form-section">
          <div class="section-title">基本信息</div>
          <div class="form-grid">
            <el-form-item label="姓名" required>
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="form.contact" placeholder="多个用逗号分隔" />
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">求职意向</div>
          <div class="form-grid">
            <el-form-item label="期望工作地点">
              <el-input v-model="form.expectedLocations" placeholder="多个用逗号分隔，如：上海,北京" />
            </el-form-item>
            <el-form-item label="工作年限">
              <el-input-number v-model="form.workYears" :min="0" :max="50" />
              <span style="margin-left: 8px; color: var(--gd-text-muted)">年</span>
            </el-form-item>
            <el-form-item label="学历">
              <el-select v-model="form.education" placeholder="选择学历" style="width: 100%">
                <el-option v-for="edu in educationOptions" :key="edu" :label="edu" :value="edu" />
              </el-select>
            </el-form-item>
            <el-form-item label="期望薪资(K)">
              <div class="salary-range">
                <el-input-number v-model="form.salaryMin" :min="0" placeholder="最低" style="width: 100%" />
                <span class="range-sep">—</span>
                <el-input-number v-model="form.salaryMax" :min="0" placeholder="最高" style="width: 100%" />
              </div>
            </el-form-item>
            <el-form-item label="求职状态">
              <el-radio-group v-model="form.jobStatus">
                <el-radio label="在职">在职</el-radio>
                <el-radio label="离职">离职</el-radio>
                <el-radio label="随时到岗">随时到岗</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">能力与经验</div>
          <el-form-item label="技能">
            <el-input v-model="form.skills" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="项目经验">
            <el-input v-model="form.projectExperience" type="textarea" :rows="5" />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button @click="goBack" class="cancel-btn">取消</el-button>
          <el-button type="primary" :loading="saving" @click="handleSave" class="save-btn">保存</el-button>
        </div>
      </el-form>

      <div v-else v-loading="true" style="min-height: 200px" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResume, updateResume } from '../api/resume'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const form = ref(null)

const educationOptions = ['博士', '硕士', '本科', '大专', '高中及以下']

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getResume(id)
    if (res.code === 200) {
      form.value = res.data
    } else {
      ElMessage.error('简历不存在')
      router.push('/list')
    }
  } catch (err) {
    ElMessage.error('获取简历失败')
    router.push('/list')
  }
})

async function handleSave() {
  if (!form.value.name) {
    ElMessage.warning('请填写姓名')
    return
  }
  saving.value = true
  try {
    const res = await updateResume(form.value.id, form.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      router.push('/list')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push('/list')
}
</script>

<style scoped>
.edit-page {
  max-width: 860px;
  margin: 0 auto;
}

.edit-card {
  padding: 32px;
}

.edit-header {
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--gd-border);
}

.edit-title-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.edit-icon {
  width: 28px;
  height: 28px;
  color: var(--gd-primary);
  display: flex;
  align-items: center;
}

.edit-icon svg {
  width: 100%;
  height: 100%;
}

.edit-title {
  font-family: var(--gd-font-body);
  font-size: 22px;
  font-weight: 700;
  color: var(--gd-text);
  margin: 0;
  background: linear-gradient(135deg, var(--gd-primary) 0%, var(--gd-accent) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ── Form Sections ── */
.form-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--gd-primary);
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 3px solid var(--gd-primary-light);
  line-height: 1;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 24px;
}

.form-grid .el-form-item:last-child {
  grid-column: 1 / -1;
}

.salary-range {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.range-sep {
  color: var(--gd-text-muted);
  flex-shrink: 0;
}

/* ── Form Actions ── */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--gd-border);
}

.save-btn {
  min-width: 100px;
}

.cancel-btn {
  border-radius: var(--gd-radius-sm) !important;
}

/* ── Element Plus Form Overrides ── */
:deep(.el-form-item__label) {
  color: var(--gd-text-secondary);
  font-weight: 500;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
  background: linear-gradient(135deg, var(--gd-primary), var(--gd-accent));
  border-color: var(--gd-primary);
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: var(--gd-primary);
}
</style>
