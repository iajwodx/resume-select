<template>
  <div class="favorites-page">
    <!-- Filter Section -->
    <div class="filter-bar glass-card">
      <div class="filter-inner">
        <span class="filter-label">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
          按岗位筛选
        </span>
        <el-select
          v-model="selectedPosition"
          placeholder="全部岗位"
          clearable
          style="width: 220px"
          @change="handleFilterChange"
        >
          <el-option v-for="pos in positionOptions" :key="pos" :label="pos" :value="pos" />
        </el-select>
      </div>
    </div>

    <!-- Card View -->
    <div class="card-grid">
      <transition-group name="card-appear">
        <div
          v-for="resume in resumeList"
          :key="resume.id"
          class="resume-card glass-card"
        >
          <!-- Card Header -->
          <div class="card-header">
            <div class="card-name-row">
              <h3 class="card-name">{{ resume.name }}</h3>
              <el-tag
                v-if="resume.jobStatus"
                :type="statusTagType(resume.jobStatus)"
                size="small"
                round
                class="status-badge"
              >
                {{ resume.jobStatus }}
              </el-tag>
              <el-tag
                v-if="resume.fittedPosition"
                type="success"
                size="small"
                round
                effect="plain"
                class="position-badge"
              >
                {{ resume.fittedPosition }}
              </el-tag>
            </div>
          </div>

          <!-- Card Body -->
          <div class="card-body">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                  地点
                </span>
                <span class="info-value">{{ resume.expectedLocations || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  年限
                </span>
                <span class="info-value">{{ resume.workYears != null ? resume.workYears + '年' : '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 1.66 2.69 3 6 3s6-1.34 6-3v-5"/></svg>
                  学历
                </span>
                <span class="info-value">{{ resume.education || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                  薪资
                </span>
                <span class="info-value">
                  <template v-if="resume.salaryMin != null || resume.salaryMax != null">
                    {{ resume.salaryMin ?? '-' }}K-{{ resume.salaryMax ?? '-' }}K
                  </template>
                  <template v-else>-</template>
                </span>
              </div>
            </div>

            <div class="contact-line" v-if="resume.contact">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="inline-icon"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
              {{ resume.contact }}
            </div>

            <div class="skills-section" v-if="resume.skills">
              <div class="section-label">技能</div>
              <div class="skills-text">{{ resume.skills }}</div>
            </div>

            <div class="project-section" v-if="resume.projectExperience">
              <div class="section-label">项目经验</div>
              <div class="project-text">{{ resume.projectExperience }}</div>
            </div>
          </div>

          <!-- Card Footer -->
          <div class="card-footer">
            <span class="update-time">{{ formatTime(resume.updateTime) }}</span>
            <div class="card-actions">
              <el-button link size="small" @click="editFittedPosition(resume)" class="action-btn edit-btn">
                编辑岗位
              </el-button>
              <el-popconfirm title="确定取消收藏？" @confirm="handleUnfavorite(resume.id)">
                <template #reference>
                  <el-button type="danger" link size="small" class="action-btn">取消收藏</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </transition-group>

      <!-- Empty state -->
      <div v-if="!loading && resumeList.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M40 62L18.5 51.5C14.5 49.5 12 45.5 12 41V22L40 10L68 22V41C68 45.5 65.5 49.5 61.5 51.5L40 62Z" stroke="currentColor" stroke-width="2" fill="rgba(196,181,253,0.1)"/>
            <path d="M40 62V36" stroke="currentColor" stroke-width="1.5" opacity="0.5"/>
            <path d="M40 36L14 22" stroke="currentColor" stroke-width="1.5" opacity="0.5"/>
            <path d="M40 36L66 22" stroke="currentColor" stroke-width="1.5" opacity="0.5"/>
            <circle cx="40" cy="10" r="3" fill="currentColor" opacity="0.3"/>
          </svg>
        </div>
        <p>暂无收藏的简历</p>
      </div>
    </div>

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
      positionOptions.value = res.data.fittedPositions || []
    }
  } catch (err) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
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

/* ── Filter Bar ── */
.filter-bar {
  padding: 16px 24px;
  margin-bottom: 20px;
}

.filter-inner {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--gd-text-secondary);
  display: flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
}

.label-icon {
  width: 16px;
  height: 16px;
  opacity: 0.6;
}

/* ── Card Grid ── */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
  min-height: 200px;
}

/* ── Resume Card ── */
.resume-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  position: relative;
  overflow: hidden;
  transition: all var(--gd-transition);
}

.resume-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--gd-mint), var(--gd-lavender), var(--gd-rose));
  opacity: 0;
  transition: opacity var(--gd-transition);
}

.resume-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--gd-shadow-lg), var(--gd-shadow-glow);
}

.resume-card:hover::before {
  opacity: 1;
}

/* Card Header */
.card-header {
  margin-bottom: 14px;
}

.card-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.card-name {
  font-family: var(--gd-font-body);
  font-size: 18px;
  font-weight: 700;
  color: var(--gd-text);
  margin: 0;
}

.status-badge,
.position-badge {
  font-size: 12px !important;
}

/* Card Body */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.info-label {
  color: var(--gd-text-muted);
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 3px;
  min-width: 50px;
}

.info-label svg {
  width: 13px;
  height: 13px;
  opacity: 0.5;
}

.info-value {
  color: var(--gd-text);
  font-weight: 500;
}

.contact-line {
  font-size: 12px;
  color: var(--gd-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.inline-icon {
  width: 13px;
  height: 13px;
  flex-shrink: 0;
  opacity: 0.5;
}

.skills-section,
.project-section {
  font-size: 13px;
  line-height: 1.6;
}

.section-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--gd-text-muted);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 4px;
}

.skills-text,
.project-text {
  color: var(--gd-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Card Footer */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--gd-border);
}

.update-time {
  font-size: 12px;
  color: var(--gd-text-muted);
}

.card-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  font-size: 13px !important;
}

.edit-btn {
  color: #fff !important;
  background: linear-gradient(135deg, var(--gd-primary), var(--gd-primary-dark)) !important;
  padding: 4px 12px !important;
  border-radius: 14px !important;
  font-size: 12px !important;
  line-height: 18px !important;
  transition: all var(--gd-transition) !important;
}

.edit-btn:hover {
  opacity: 0.85 !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(124, 92, 191, 0.35) !important;
}

/* ── Empty State ── */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--gd-text-muted);
}

.empty-icon {
  width: 80px;
  height: 80px;
  color: var(--gd-lavender);
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 15px;
  margin: 0;
}

/* ── Pagination ── */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 28px;
  padding: 12px 0;
}

/* ── Card Animation ── */
.card-appear-enter-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.card-appear-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.97);
}

.card-appear-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.card-appear-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.97);
}

.card-appear-move {
  transition: transform 0.4s ease;
}
</style>
