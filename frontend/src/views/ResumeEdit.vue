<template>
  <div class="edit-page">
    <el-card>
      <template #header>
        <span>编辑简历</span>
      </template>

      <el-form v-if="form" ref="formRef" :model="form" label-width="120px" style="max-width: 700px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="联系方式">
          <el-input v-model="form.contact" placeholder="多个用逗号分隔" />
        </el-form-item>

        <el-form-item label="期望工作地点">
          <el-input v-model="form.expectedLocations" placeholder="多个用逗号分隔，如：上海,北京" />
        </el-form-item>

        <el-form-item label="工作年限">
          <el-input-number v-model="form.workYears" :min="0" :max="50" />
          <span style="margin-left: 8px">年</span>
        </el-form-item>

        <el-form-item label="学历">
          <el-select v-model="form.education" placeholder="选择学历" style="width: 100%">
            <el-option v-for="edu in educationOptions" :key="edu" :label="edu" :value="edu" />
          </el-select>
        </el-form-item>

        <el-form-item label="期望薪资(K)">
          <el-col :span="11">
            <el-input-number v-model="form.salaryMin" :min="0" placeholder="最低" style="width: 100%" />
          </el-col>
          <el-col :span="2" style="text-align: center; line-height: 32px">-</el-col>
          <el-col :span="11">
            <el-input-number v-model="form.salaryMax" :min="0" placeholder="最高" style="width: 100%" />
          </el-col>
        </el-form-item>

        <el-form-item label="求职状态">
          <el-radio-group v-model="form.jobStatus">
            <el-radio label="在职">在职</el-radio>
            <el-radio label="离职">离职</el-radio>
            <el-radio label="随时到岗">随时到岗</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="技能">
          <el-input v-model="form.skills" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="项目经验">
          <el-input v-model="form.projectExperience" type="textarea" :rows="5" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>

      <div v-else v-loading="true" style="min-height: 200px" />
    </el-card>
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
  max-width: 900px;
  margin: 0 auto;
}
</style>
