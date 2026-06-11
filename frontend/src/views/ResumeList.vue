<template>
  <div class="list-page">
    <!-- Filter Section -->
    <resume-filter ref="filterRef" @search="handleSearch" />

    <!-- Results summary -->
    <div class="results-header">
      <div class="results-info">
        <span class="results-count">共 <strong>{{ total }}</strong> 份简历</span>
        <span v-if="currentFilter.keyword" class="results-keyword">
          关键词：<em>{{ currentFilter.keyword }}</em>
        </span>
      </div>
      <div class="view-toggle">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="card">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button label="table">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- Card View -->
    <div v-if="viewMode === 'card'" class="card-grid" v-loading="loading">
      <transition-group name="card-appear">
        <div
          v-for="resume in resumeList"
          :key="resume.id"
          class="resume-card glass-card"
        >
          <!-- Card Header: Name + Match Score -->
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
            </div>
            <!-- Match Score Ring -->
            <div v-if="resume.matchScore != null" class="match-ring" :class="matchScoreClass(resume.matchScore)">
              <svg viewBox="0 0 48 48" class="ring-svg">
                <circle cx="24" cy="24" r="20" fill="none" stroke="rgba(0,0,0,0.06)" stroke-width="4"/>
                <circle
                  cx="24" cy="24" r="20" fill="none"
                  :stroke="matchScoreColor(resume.matchScore)"
                  stroke-width="4"
                  stroke-linecap="round"
                  :stroke-dasharray="`${resume.matchScore * 1.256} 9999`"
                  transform="rotate(-90 24 24)"
                  class="ring-progress"
                />
              </svg>
              <span class="ring-value">{{ resume.matchScore }}</span>
            </div>
          </div>

          <!-- Card Body: Key Info -->
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

            <!-- Contact -->
            <div class="contact-line" v-if="resume.contact">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="inline-icon"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
              {{ resume.contact }}
            </div>

            <!-- Skills with highlight -->
            <div class="skills-section" v-if="resume.skills">
              <div class="section-label">技能</div>
              <div class="skills-text" v-html="highlightText(resume.skills, resume.matchedTexts)" />
            </div>

            <!-- Project experience with highlight -->
            <div class="project-section" v-if="resume.projectExperience">
              <div class="section-label">项目经验</div>
              <div class="project-text" v-html="highlightText(resume.projectExperience, resume.matchedTexts)" />
            </div>
          </div>

          <!-- Card Footer: Actions -->
          <div class="card-footer">
            <span class="update-time">{{ formatTime(resume.updateTime) }}</span>
            <div class="card-actions">
              <el-button
                :type="resume.isFavorite ? 'warning' : 'default'"
                link
                size="small"
                @click="handleFavorite(resume)"
                class="action-btn fav-btn"
              >
                <el-icon style="margin-right: 3px"><StarFilled v-if="resume.isFavorite" /><Star v-else /></el-icon>
                {{ resume.isFavorite ? '已收藏' : '收藏' }}
              </el-button>
              <el-button link size="small" @click="viewResume(resume.id)" class="action-btn edit-btn">
                详情
              </el-button>
              <el-popconfirm title="确定删除该简历？" @confirm="handleDelete(resume.id)">
                <template #reference>
                  <el-button type="danger" link size="small" class="action-btn">删除</el-button>
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
            <rect x="15" y="8" width="36" height="48" rx="4" stroke="currentColor" stroke-width="2" fill="rgba(196,181,253,0.1)"/>
            <rect x="29" y="24" width="36" height="48" rx="4" stroke="currentColor" stroke-width="2" fill="rgba(196,181,253,0.15)"/>
            <line x1="36" y1="34" x2="56" y2="34" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
            <line x1="36" y1="42" x2="52" y2="42" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
            <line x1="36" y1="50" x2="48" y2="50" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
          </svg>
        </div>
        <p>暂无符合条件的简历</p>
      </div>
    </div>

    <!-- Table View (fallback) -->
    <el-card v-if="viewMode === 'table'">
      <el-table :data="resumeList" v-loading="loading" style="width: 100%">
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
            <el-tag v-if="row.jobStatus" :type="statusTagType(row.jobStatus)" size="small" round>
              {{ row.jobStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="技能" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-html="highlightText(row.skills, row.matchedTexts)" />
          </template>
        </el-table-column>
        <el-table-column label="项目经验" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-html="highlightText(row.projectExperience, row.matchedTexts)" />
          </template>
        </el-table-column>
        <el-table-column label="匹配度" width="120" align="center">
          <template #default="{ row }">
            <template v-if="row.matchScore != null">
              <el-progress
                :percentage="row.matchScore"
                :color="matchScoreColor(row.matchScore)"
                :stroke-width="16"
                :text-inside="true"
                style="width: 100%"
              />
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.isFavorite ? 'warning' : 'default'"
              link
              size="small"
              @click="handleFavorite(row)"
            >
              <el-icon style="margin-right: 2px"><StarFilled v-if="row.isFavorite" /><Star v-else /></el-icon>
              {{ row.isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button type="primary" link size="small" @click="viewResume(row.id)">
              详情
            </el-button>
            <el-popconfirm title="确定删除该简历？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

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

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" width="720px" @closed="onDetailClosed" class="breeze-dialog" :show-close="false">
      <template v-if="detailData">
        <!-- Header: Name + Status -->
        <div class="breeze-header">
          <div class="breeze-name">{{ detailData.name }}</div>
          <div class="breeze-tags">
            <el-tag v-if="detailData.jobStatus" :type="statusTagType(detailData.jobStatus)" size="small" round effect="plain">{{ detailData.jobStatus }}</el-tag>
            <el-tag v-if="detailData.isFavorite" size="small" round effect="plain" type="warning">已收藏{{ detailData.fittedPosition ? ' · ' + detailData.fittedPosition : '' }}</el-tag>
          </div>
        </div>

        <!-- Info Grid -->
        <div class="breeze-grid">
          <div class="breeze-field">
            <span class="breeze-label">联系方式</span>
            <span class="breeze-value">{{ detailData.contact || '-' }}</span>
          </div>
          <div class="breeze-field">
            <span class="breeze-label">期望地点</span>
            <span class="breeze-value">{{ detailData.expectedLocations || '-' }}</span>
          </div>
          <div class="breeze-field">
            <span class="breeze-label">工作年限</span>
            <span class="breeze-value">{{ detailData.workYears != null ? detailData.workYears + '年' : '-' }}</span>
          </div>
          <div class="breeze-field">
            <span class="breeze-label">学历</span>
            <span class="breeze-value">{{ detailData.education || '-' }}</span>
          </div>
          <div class="breeze-field">
            <span class="breeze-label">期望薪资</span>
            <span class="breeze-value">
              <template v-if="detailData.salaryMin != null || detailData.salaryMax != null">
                {{ detailData.salaryMin ?? '-' }}K - {{ detailData.salaryMax ?? '-' }}K
              </template>
              <template v-else>-</template>
            </span>
          </div>
          <div class="breeze-field">
            <span class="breeze-label">更新时间</span>
            <span class="breeze-value breeze-value--sm">{{ formatTime(detailData.updateTime) }}</span>
          </div>
        </div>

        <!-- Skills -->
        <div class="breeze-block">
          <div class="breeze-block-title">技能</div>
          <div class="breeze-block-body">
            <span v-for="skill in (detailData.skills || '').split(',').map(s => s.trim()).filter(Boolean)" :key="skill" class="breeze-chip">{{ skill }}</span>
            <span v-if="!detailData.skills" class="breeze-empty">暂无</span>
          </div>
        </div>

        <!-- Project Experience -->
        <div class="breeze-block">
          <div class="breeze-block-title">项目经验</div>
          <div class="breeze-block-body breeze-block-body--text">{{ detailData.projectExperience || '暂无' }}</div>
        </div>
      </template>
      <template #footer>
        <el-button class="breeze-close-btn" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Favorite Dialog -->
    <el-dialog v-model="favDialogVisible" title="收藏简历" width="400px" @closed="onFavDialogClosed">
      <el-form label-width="80px">
        <el-form-item label="适配岗位">
          <el-input v-model="favFittedPosition" placeholder="请输入适配岗位（可留空）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="favDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFavorite">确认收藏</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, Grid, List } from '@element-plus/icons-vue'
import ResumeFilter from '../components/ResumeFilter.vue'
import { listResumes, getResume, updateResume, deleteResume, toggleFavorite } from '../api/resume'

const filterRef = ref(null)
const loading = ref(false)
const resumeList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const currentFilter = ref({})
const viewMode = ref('card')

const editVisible = ref(false)
const editForm = ref(null)
const editFormRef = ref(null)
const editSaving = ref(false)

const detailVisible = ref(false)
const detailData = ref(null)
const educationOptions = ['博士', '硕士', '本科', '大专', '高中及以下']

const favDialogVisible = ref(false)
const favResumeId = ref(null)
const favFittedPosition = ref('')

onMounted(() => {
  fetchList()
})

function handleSearch(params) {
  currentFilter.value = params
  currentPage.value = 1
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      ...currentFilter.value,
      page: currentPage.value,
      size: pageSize.value
    }
    const res = await listResumes(params)
    if (res.code === 200) {
      resumeList.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) {
    ElMessage.error('获取简历列表失败')
  } finally {
    loading.value = false
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

async function viewResume(id) {
  try {
    const res = await getResume(id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    }
  } catch (err) {
    ElMessage.error('获取简历失败')
  }
}

function onDetailClosed() {
  detailData.value = null
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

function handleFavorite(row) {
  if (row.isFavorite) {
    cancelFavorite(row.id)
  } else {
    favResumeId.value = row.id
    favFittedPosition.value = row.fittedPosition || ''
    favDialogVisible.value = true
  }
}

async function confirmFavorite() {
  try {
    const res = await toggleFavorite(favResumeId.value, {
      isFavorite: true,
      fittedPosition: favFittedPosition.value
    })
    if (res.code === 200) {
      ElMessage.success('收藏成功')
      favDialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || '收藏失败')
    }
  } catch (err) {
    ElMessage.error('收藏失败')
  }
}

async function cancelFavorite(id) {
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

function onFavDialogClosed() {
  favResumeId.value = null
  favFittedPosition.value = ''
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

function matchScoreColor(score) {
  if (score >= 80) return '#10b981'
  if (score >= 50) return '#f59e0b'
  if (score >= 20) return '#ef4444'
  return '#9ca3af'
}

function matchScoreClass(score) {
  if (score >= 80) return 'score-high'
  if (score >= 50) return 'score-mid'
  if (score >= 20) return 'score-low'
  return 'score-none'
}

function highlightText(text, matchedTexts) {
  if (!text) return ''
  if (!matchedTexts || matchedTexts.length === 0) return escapeHtml(text)

  let result = escapeHtml(text)
  const sorted = [...matchedTexts].sort((a, b) => b.length - a.length)
  for (const snippet of sorted) {
    const escapedSnippet = escapeHtml(snippet)
    const regex = new RegExp(escapeRegex(escapedSnippet), 'gi')
    result = result.replace(regex, '<mark class="match-highlight">$&</mark>')
  }
  return result
}

function escapeHtml(str) {
  if (!str) return ''
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function escapeRegex(str) {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}
</script>

<style scoped>
.list-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* ── Results Header ── */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.results-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.results-count {
  font-size: 14px;
  color: var(--gd-text-secondary);
}

.results-count strong {
  color: var(--gd-primary);
  font-size: 18px;
  font-weight: 700;
}

.results-keyword {
  font-size: 13px;
  color: var(--gd-text-secondary);
  background: rgba(124, 92, 191, 0.06);
  padding: 4px 12px;
  border-radius: 20px;
}

.results-keyword em {
  font-style: normal;
  font-weight: 600;
  color: var(--gd-primary);
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
  cursor: default;
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
  background: linear-gradient(90deg, var(--gd-lavender), var(--gd-rose), var(--gd-sky));
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-name {
  font-family: var(--gd-font-body);
  font-size: 18px;
  font-weight: 700;
  color: var(--gd-text);
  margin: 0;
}

.status-badge {
  font-size: 12px !important;
}

/* ── Match Score Ring ── */
.match-ring {
  position: relative;
  width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.ring-svg {
  width: 100%;
  height: 100%;
}

.ring-progress {
  transition: stroke-dasharray 1s ease;
}

.ring-value {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.score-high .ring-value { color: #10b981; }
.score-mid .ring-value { color: #f59e0b; }
.score-low .ring-value { color: #ef4444; }
.score-none .ring-value { color: #9ca3af; }

/* Card Body */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
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

.fav-btn :deep(.el-icon) {
  color: #f59e0b;
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

/* ── Card Appear Animation ── */
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

/* ── View Toggle ── */
.view-toggle :deep(.el-radio-button__inner) {
  border-radius: 10px !important;
  border: 1px solid var(--gd-border) !important;
  box-shadow: none !important;
  padding: 6px 12px;
}

.view-toggle :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, var(--gd-primary), var(--gd-accent)) !important;
  border-color: var(--gd-primary) !important;
}

/* ── Breeze Detail Dialog ── */
.breeze-dialog {
  --breeze-green: #34d399;
  --breeze-green-soft: #ecfdf5;
  --breeze-green-border: #a7f3d0;
  --breeze-green-text: #065f46;
  --breeze-bg: #f8fffe;
  --breeze-muted: #6b7280;
  --breeze-border: #e5e7eb;
}

.breeze-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  background: var(--breeze-bg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08), 0 0 0 1px var(--breeze-border);
}

.breeze-dialog :deep(.el-dialog__header) {
  display: none;
}

.breeze-dialog :deep(.el-dialog__body) {
  padding: 32px 36px 12px;
}

.breeze-dialog :deep(.el-dialog__footer) {
  padding: 12px 36px 28px;
  text-align: center;
}

/* Header */
.breeze-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.breeze-name {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.5px;
}

.breeze-tags {
  display: flex;
  gap: 8px;
}

/* Info Grid */
.breeze-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px 24px;
  padding: 20px 24px;
  background: white;
  border-radius: 14px;
  border: 1px solid var(--breeze-border);
}

.breeze-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.breeze-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--breeze-muted);
  letter-spacing: 0.5px;
}

.breeze-value {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.breeze-value--sm {
  font-weight: 400;
  font-size: 13px;
  color: var(--breeze-muted);
}

/* Blocks */
.breeze-block {
  margin-top: 20px;
}

.breeze-block-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--breeze-green-text);
  margin-bottom: 10px;
  padding-left: 10px;
  border-left: 3px solid var(--breeze-green);
  line-height: 1;
}

.breeze-block-body {
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid var(--breeze-border);
  line-height: 1.8;
  font-size: 14px;
  color: #374151;
}

.breeze-block-body--text {
  white-space: pre-wrap;
}

/* Skill Chips */
.breeze-chip {
  display: inline-block;
  padding: 4px 14px;
  margin: 3px 4px;
  background: var(--breeze-green-soft);
  color: var(--breeze-green-text);
  border: 1px solid var(--breeze-green-border);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.6;
}

.breeze-empty {
  color: var(--breeze-muted);
  font-size: 14px;
}

/* Close Button */
.breeze-close-btn {
  border-radius: 20px !important;
  padding: 8px 40px !important;
  font-weight: 500 !important;
  border: 1px solid var(--breeze-border) !important;
  color: #374151 !important;
  background: white !important;
  transition: all 0.2s !important;
}

.breeze-close-btn:hover {
  border-color: var(--breeze-green) !important;
  color: var(--breeze-green-text) !important;
  background: var(--breeze-green-soft) !important;
}
</style>
