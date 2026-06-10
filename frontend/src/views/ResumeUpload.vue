<template>
  <div class="upload-page">
    <el-card>
      <template #header>
        <span>简历上传</span>
      </template>

      <el-upload
        ref="uploadRef"
        class="resume-uploader"
        drag
        action=""
        :auto-upload="false"
        :limit="1"
        accept=".pdf"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将PDF简历拖拽到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">仅支持PDF格式文件</div>
        </template>
      </el-upload>

      <div class="upload-actions">
        <el-button type="primary" :loading="uploading" @click="handleUpload">
          {{ uploading ? 'AI分析中...' : '上传并分析' }}
        </el-button>
      </div>
    </el-card>

    <!-- AI Analysis Result Preview -->
    <el-card v-if="analysisResult" class="result-card" style="margin-top: 20px">
      <template #header>
        <div class="result-header">
          <span>AI分析结果</span>
          <el-tag type="success">分析完成</el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ analysisResult.name }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ analysisResult.contact }}</el-descriptions-item>
        <el-descriptions-item label="期望工作地点">{{ analysisResult.expectedLocations }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ analysisResult.workYears }}年</el-descriptions-item>
        <el-descriptions-item label="学历">{{ analysisResult.education }}</el-descriptions-item>
        <el-descriptions-item label="期望薪资">
          {{ analysisResult.salaryMin }}K - {{ analysisResult.salaryMax }}K
        </el-descriptions-item>
        <el-descriptions-item label="求职状态">{{ analysisResult.jobStatus }}</el-descriptions-item>
        <el-descriptions-item label="技能" :span="2">{{ analysisResult.skills }}</el-descriptions-item>
        <el-descriptions-item label="项目经验" :span="2">
          <div style="white-space: pre-wrap">{{ analysisResult.projectExperience }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <div class="result-actions">
        <el-button @click="resetUpload">继续上传</el-button>
      </div>
    </el-card>

    <!-- All Resumes Table -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>所有简历</span>
      </template>
      <el-table :data="resumeList" v-loading="listLoading" stripe style="width: 100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="contact" label="联系方式" width="180" show-overflow-tooltip />
        <el-table-column prop="expectedLocations" label="期望地点" width="160" show-overflow-tooltip />
        <el-table-column prop="workYears" label="工作年限" width="100" align="center">
          <template #default="{ row }">
            {{ row.workYears != null ? row.workYears + '年' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="education" label="学历" width="100" align="center" />
        <el-table-column label="期望薪资" width="140" align="center">
          <template #default="{ row }">
            <span v-if="row.salaryMin != null || row.salaryMax != null">
              {{ row.salaryMin ?? '-' }}K - {{ row.salaryMax ?? '-' }}K
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="skills" label="技能" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectExperience" label="项目经验" min-width="250" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link size="small" @click="editResume(row.id)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除该简历？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <!-- Edit Dialog -->
    <el-dialog v-model="editVisible" title="编辑简历" width="700px" @closed="onEditClosed">
      <el-form v-if="editForm" ref="editFormRef" :model="editForm" label-width="120px">
        <el-form-item label="姓名" required>
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="editForm.contact" placeholder="多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="期望工作地点">
          <el-input v-model="editForm.expectedLocations" placeholder="多个用逗号分隔，如：上海,北京" />
        </el-form-item>
        <el-form-item label="工作年限">
          <el-input-number v-model="editForm.workYears" :min="0" :max="50" />
          <span style="margin-left: 8px">年</span>
        </el-form-item>
        <el-form-item label="学历">
          <el-select v-model="editForm.education" placeholder="选择学历" style="width: 100%">
            <el-option v-for="edu in educationOptions" :key="edu" :label="edu" :value="edu" />
          </el-select>
        </el-form-item>
        <el-form-item label="期望薪资(K)">
          <el-col :span="11">
            <el-input-number v-model="editForm.salaryMin" :min="0" placeholder="最低" style="width: 100%" />
          </el-col>
          <el-col :span="2" style="text-align: center; line-height: 32px">-</el-col>
          <el-col :span="11">
            <el-input-number v-model="editForm.salaryMax" :min="0" placeholder="最高" style="width: 100%" />
          </el-col>
        </el-form-item>
        <el-form-item label="求职状态">
          <el-radio-group v-model="editForm.jobStatus">
            <el-radio label="在职">在职</el-radio>
            <el-radio label="离职">离职</el-radio>
            <el-radio label="随时到岗">随时到岗</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="技能">
          <el-input v-model="editForm.skills" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="项目经验">
          <el-input v-model="editForm.projectExperience" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSaving" @click="handleEditSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadResume, listResumes, getResume, updateResume, deleteResume } from '../api/resume'

const uploadRef = ref(null)
const uploading = ref(false)
const analysisResult = ref(null)
const selectedFile = ref(null)

// Resume list state
const listLoading = ref(false)
const resumeList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// Edit dialog state
const editVisible = ref(false)
const editForm = ref(null)
const editFormRef = ref(null)
const editSaving = ref(false)
const educationOptions = ['博士', '硕士', '本科', '大专', '高中及以下']

onMounted(() => {
  fetchList()
})

function handleFileChange(file) {
  selectedFile.value = file.raw
}

function handleExceed() {
  ElMessage.warning('只能上传一个文件，请先移除已选文件')
}

async function handleUpload() {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择PDF文件')
    return
  }

  uploading.value = true
  analysisResult.value = null

  try {
    const res = await uploadResume(selectedFile.value)
    if (res.code === 200) {
      analysisResult.value = res.data
      ElMessage.success('简历分析完成')
      fetchList()
    } else {
      ElMessage.error(res.message || '分析失败')
    }
  } catch (err) {
    ElMessage.error('上传失败，请检查后端服务是否正常')
  } finally {
    uploading.value = false
  }
}

function resetUpload() {
  analysisResult.value = null
  selectedFile.value = null
  uploadRef.value?.clearFiles()
}

async function fetchList() {
  listLoading.value = true
  try {
    const res = await listResumes({ page: currentPage.value, size: pageSize.value })
    if (res.code === 200) {
      resumeList.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) {
    ElMessage.error('获取简历列表失败')
  } finally {
    listLoading.value = false
  }
}

async function editResume(id) {
  try {
    const res = await getResume(id)
    if (res.code === 200) {
      editForm.value = { ...res.data }
      editVisible.value = true
    }
  } catch (err) {
    ElMessage.error('获取简历失败')
  }
}

async function handleEditSave() {
  if (!editForm.value.name) {
    ElMessage.warning('请填写姓名')
    return
  }
  editSaving.value = true
  try {
    const res = await updateResume(editForm.value.id, editForm.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      editVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    editSaving.value = false
  }
}

function onEditClosed() {
  editForm.value = null
}

async function handleDelete(id) {
  try {
    const res = await deleteResume(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

function formatTime(time) {
  if (!time) return '-'
  if (Array.isArray(time)) {
    const [y, m, d, h = 0, min = 0, s = 0] = time
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  return time
}
</script>

<style scoped>
.upload-page {
  max-width: 1400px;
  margin: 0 auto;
}

.upload-actions {
  margin-top: 16px;
  text-align: center;
}

.result-card .result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-actions {
  margin-top: 16px;
  text-align: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
