<template>
  <div class="favorites-page">
    <!-- Filter Section -->
    <el-card style="margin-bottom: 16px">
      <el-form inline>
        <el-form-item label="适配岗位">
          <el-select
            v-model="selectedPosition"
            placeholder="全部岗位"
            clearable
            style="width: 200px"
            @change="handleFilterChange"
          >
            <el-option
              v-for="pos in positionOptions"
              :key="pos"
              :label="pos"
              :value="pos"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Resume Table -->
    <el-card>
      <el-table :data="resumeList" v-loading="loading" stripe style="width: 100%">
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
        <el-table-column prop="jobStatus" label="求职状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.jobStatus" :type="statusTagType(row.jobStatus)" size="small">
              {{ row.jobStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="fittedPosition" label="适配岗位" width="140" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.fittedPosition" type="success" size="small">
              {{ row.fittedPosition }}
            </el-tag>
            <span v-else style="color: #999">未填写</span>
          </template>
        </el-table-column>
        <el-table-column label="技能" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.skills || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="项目经验" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.projectExperience || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editFittedPosition(row)">
              编辑岗位
            </el-button>
            <el-popconfirm title="确定取消收藏？" @confirm="handleUnfavorite(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">取消收藏</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
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

    <!-- Edit Fitted Position Dialog -->
    <el-dialog v-model="editPosVisible" title="编辑适配岗位" width="400px" @closed="onEditPosClosed">
      <el-form label-width="80px">
        <el-form-item label="适配岗位">
          <el-input v-model="editPosValue" placeholder="请输入适配岗位（可留空）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editPosVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFittedPosition">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listResumes, toggleFavorite } from '../api/resume'

const loading = ref(false)
const resumeList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedPosition = ref('')
const positionOptions = ref([])

const editPosVisible = ref(false)
const editPosResumeId = ref(null)
const editPosValue = ref('')

onMounted(() => {
  fetchList()
  fetchPositionOptions()
})

async function fetchList() {
  loading.value = true
  try {
    const params = {
      isFavorite: true,
      page: currentPage.value,
      size: pageSize.value
    }
    if (selectedPosition.value) {
      params.fittedPosition = selectedPosition.value
    }
    const res = await listResumes(params)
    if (res.code === 200) {
      resumeList.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

async function fetchPositionOptions() {
  try {
    // Fetch all favorite resumes to extract unique fitted positions
    const res = await listResumes({ isFavorite: true, size: 1000 })
    if (res.code === 200) {
      const positions = new Set()
      res.data.list.forEach(r => {
        if (r.fittedPosition) {
          positions.add(r.fittedPosition)
        }
      })
      positionOptions.value = Array.from(positions)
    }
  } catch (err) {
    // silently ignore
  }
}

function handleFilterChange() {
  currentPage.value = 1
  fetchList()
}

function editFittedPosition(row) {
  editPosResumeId.value = row.id
  editPosValue.value = row.fittedPosition || ''
  editPosVisible.value = true
}

async function saveFittedPosition() {
  try {
    const res = await toggleFavorite(editPosResumeId.value, {
      isFavorite: true,
      fittedPosition: editPosValue.value
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      editPosVisible.value = false
      fetchList()
      fetchPositionOptions()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

async function handleUnfavorite(id) {
  try {
    const res = await toggleFavorite(id, {
      isFavorite: false
    })
    if (res.code === 200) {
      ElMessage.success('已取消收藏')
      fetchList()
      fetchPositionOptions()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

function onEditPosClosed() {
  editPosResumeId.value = null
  editPosValue.value = ''
}

function statusTagType(status) {
  if (status === '离职' || status === '随时到岗') return 'success'
  if (status === '在职') return 'warning'
  return 'info'
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
.favorites-page {
  max-width: 1400px;
  margin: 0 auto;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
