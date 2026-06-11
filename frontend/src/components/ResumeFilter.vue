<template>
  <div class="filter-glass glass-card">
    <!-- Keyword search — most prominent -->
    <div class="filter-keyword-row">
      <div class="keyword-wrapper">
        <div class="keyword-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
        </div>
        <el-input
          v-model="filterForm.keyword"
          placeholder="输入关键词搜索简历，如：Java 架构 分布式"
          clearable
          size="large"
          class="keyword-input"
          @keyup.enter="$emit('search', getFilterParams())"
        />
        <el-button type="primary" size="large" class="search-btn" @click="$emit('search', getFilterParams())">
          <el-icon style="margin-right: 4px"><Search /></el-icon>
          查询
        </el-button>
        <el-button size="large" class="reset-btn" @click="resetFilter">重置</el-button>
      </div>
    </div>

    <!-- Filter conditions — clearly organized -->
    <div class="filter-conditions">
      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            期望地点
          </span>
          <el-select
            v-model="filterForm.locations"
            multiple
            filterable
            allow-create
            placeholder="选择或输入"
            style="width: 260px"
            size="default"
          >
            <el-option v-for="loc in locationOptions" :key="loc" :label="loc" :value="loc" />
          </el-select>
        </div>

        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            最低年限
          </span>
          <el-input-number
            v-model="filterForm.minWorkYears"
            :min="0"
            :max="50"
            placeholder="年"
            style="width: 120px"
            size="default"
          />
          <span class="filter-unit">年</span>
        </div>

        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 1.66 2.69 3 6 3s6-1.34 6-3v-5"/></svg>
            学历要求
          </span>
          <el-select
            v-model="filterForm.educations"
            multiple
            placeholder="选择学历"
            style="width: 260px"
            size="default"
          >
            <el-option v-for="edu in educationOptions" :key="edu" :label="edu" :value="edu" />
          </el-select>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
            期望薪资
          </span>
          <el-input-number v-model="filterForm.salaryMin" :min="0" placeholder="最低" style="width: 100px" size="default" />
          <span class="filter-sep">—</span>
          <el-input-number v-model="filterForm.salaryMax" :min="0" placeholder="最高" style="width: 100px" size="default" />
          <span class="filter-unit">K</span>
        </div>

        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            求职状态
          </span>
          <el-radio-group v-model="filterForm.jobStatus" size="default" class="status-radio">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="在职">在职</el-radio-button>
            <el-radio-button label="离职">离职</el-radio-button>
            <el-radio-button label="随时到岗">随时到岗</el-radio-button>
          </el-radio-group>
        </div>

        <div class="filter-group">
          <span class="filter-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="label-icon"><line x1="4" y1="21" x2="4" y2="14"/><line x1="4" y1="10" x2="4" y2="3"/><line x1="12" y1="21" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="3"/><line x1="20" y1="21" x2="20" y2="16"/><line x1="20" y1="12" x2="20" y2="3"/></svg>
            排序方式
          </span>
          <el-radio-group v-model="filterForm.sortBy" size="default" class="sort-radio">
            <el-radio-button label="updateTime">更新时间</el-radio-button>
            <el-radio-button label="matchScore">匹配度</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <!-- Active filter tags -->
    <transition-group name="tag-fade" tag="div" class="active-filters" v-if="hasActiveFilters">
      <el-tag
        v-for="tag in activeFilterTags"
        :key="tag.key"
        closable
        :type="tag.type"
        effect="plain"
        class="active-tag"
        @close="removeFilter(tag.key)"
      >
        {{ tag.label }}：{{ tag.value }}
      </el-tag>
      <el-button link type="primary" size="small" @click="resetFilter" class="clear-all-btn">
        清空全部
      </el-button>
    </transition-group>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

const emit = defineEmits(['search'])

const locationOptions = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '南京', '苏州', '西安']
const educationOptions = ['博士', '硕士', '本科', '大专', '高中及以下']

const filterForm = reactive({
  locations: [],
  minWorkYears: null,
  educations: [],
  salaryMin: null,
  salaryMax: null,
  jobStatus: '',
  keyword: '',
  sortBy: 'updateTime'
})

const hasActiveFilters = computed(() => {
  return filterForm.locations.length > 0 ||
    filterForm.minWorkYears != null ||
    filterForm.educations.length > 0 ||
    filterForm.salaryMin != null ||
    filterForm.salaryMax != null ||
    filterForm.jobStatus !== ''
})

const activeFilterTags = computed(() => {
  const tags = []
  if (filterForm.locations.length > 0) {
    tags.push({ key: 'locations', label: '地点', value: filterForm.locations.join('、'), type: 'primary' })
  }
  if (filterForm.minWorkYears != null) {
    tags.push({ key: 'minWorkYears', label: '年限', value: `${filterForm.minWorkYears}年以上`, type: 'success' })
  }
  if (filterForm.educations.length > 0) {
    tags.push({ key: 'educations', label: '学历', value: filterForm.educations.join('、'), type: 'warning' })
  }
  if (filterForm.salaryMin != null || filterForm.salaryMax != null) {
    const min = filterForm.salaryMin ?? '—'
    const max = filterForm.salaryMax ?? '—'
    tags.push({ key: 'salary', label: '薪资', value: `${min}K-${max}K`, type: 'danger' })
  }
  if (filterForm.jobStatus) {
    tags.push({ key: 'jobStatus', label: '状态', value: filterForm.jobStatus, type: 'info' })
  }
  return tags
})

function removeFilter(key) {
  if (key === 'locations') filterForm.locations = []
  else if (key === 'minWorkYears') filterForm.minWorkYears = null
  else if (key === 'educations') filterForm.educations = []
  else if (key === 'salary') { filterForm.salaryMin = null; filterForm.salaryMax = null }
  else if (key === 'jobStatus') filterForm.jobStatus = ''
  emit('search', getFilterParams())
}

function getFilterParams() {
  const params = {}
  if (filterForm.locations.length > 0) {
    params.locations = filterForm.locations.join(',')
  }
  if (filterForm.minWorkYears != null) {
    params.minWorkYears = filterForm.minWorkYears
  }
  if (filterForm.educations.length > 0) {
    params.educations = filterForm.educations.join(',')
  }
  if (filterForm.salaryMin != null) {
    params.salaryMin = filterForm.salaryMin
  }
  if (filterForm.salaryMax != null) {
    params.salaryMax = filterForm.salaryMax
  }
  if (filterForm.jobStatus) {
    params.jobStatus = filterForm.jobStatus
  }
  if (filterForm.keyword && filterForm.keyword.trim()) {
    params.keyword = filterForm.keyword.trim()
  }
  if (filterForm.sortBy) {
    params.sortBy = filterForm.sortBy
  }
  return params
}

function resetFilter() {
  filterForm.locations = []
  filterForm.minWorkYears = null
  filterForm.educations = []
  filterForm.salaryMin = null
  filterForm.salaryMax = null
  filterForm.jobStatus = ''
  filterForm.keyword = ''
  filterForm.sortBy = 'updateTime'
  emit('search', {})
}

defineExpose({ getFilterParams })
</script>

<style scoped>
.filter-glass {
  padding: 20px 24px;
  margin-bottom: 20px;
}

/* ── Keyword Row — most prominent ── */
.filter-keyword-row {
  margin-bottom: 20px;
}

.keyword-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.keyword-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  color: var(--gd-primary);
  display: flex;
  align-items: center;
}

.keyword-icon svg {
  width: 100%;
  height: 100%;
}

.keyword-input {
  flex: 1;
}

.keyword-input :deep(.el-input__wrapper) {
  border-radius: 14px !important;
  padding: 4px 16px;
  box-shadow: 0 0 0 1px var(--gd-border) inset, var(--gd-shadow-sm) !important;
  font-size: 15px;
  transition: all var(--gd-transition) !important;
}

.keyword-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px var(--gd-primary-light) inset, var(--gd-shadow-md) !important;
}

.keyword-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--gd-primary) inset, 0 0 20px rgba(124, 92, 191, 0.12) !important;
}

.keyword-input :deep(.el-input__inner) {
  height: 36px;
}

.keyword-input :deep(.el-input__inner::placeholder) {
  color: var(--gd-text-muted);
}

.search-btn {
  min-width: 100px;
  border-radius: 14px !important;
  font-weight: 600 !important;
  letter-spacing: 1px;
}

.reset-btn {
  border-radius: 14px !important;
}

/* ── Filter Conditions ── */
.filter-conditions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--gd-text-secondary);
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 4px;
}

.label-icon {
  width: 14px;
  height: 14px;
  opacity: 0.6;
}

.filter-unit {
  font-size: 13px;
  color: var(--gd-text-muted);
}

.filter-sep {
  color: var(--gd-text-muted);
  font-size: 13px;
}

/* ── Radio Buttons ── */
.status-radio :deep(.el-radio-button__inner),
.sort-radio :deep(.el-radio-button__inner) {
  border-radius: 20px !important;
  border: 1px solid var(--gd-border) !important;
  box-shadow: none !important;
  font-size: 13px;
  padding: 6px 14px;
  transition: all var(--gd-transition) !important;
}

.status-radio :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner),
.sort-radio :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, var(--gd-primary), var(--gd-accent)) !important;
  border-color: var(--gd-primary) !important;
  box-shadow: 0 2px 8px rgba(124, 92, 191, 0.3) !important;
}

.status-radio :deep(.el-radio-button:first-child .el-radio-button__inner),
.sort-radio :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid var(--gd-border) !important;
}

/* ── Active Filter Tags ── */
.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--gd-border);
}

.active-tag {
  border-radius: 20px !important;
  animation: tagAppear 0.3s ease;
}

.clear-all-btn {
  margin-left: 4px;
  font-size: 12px;
}

@keyframes tagAppear {
  from {
    opacity: 0;
    transform: scale(0.85);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.tag-fade-enter-active {
  animation: tagAppear 0.3s ease;
}

.tag-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.tag-fade-leave-to {
  opacity: 0;
  transform: scale(0.85);
}

/* ── Select Overrides ── */
:deep(.el-select__wrapper) {
  border-radius: var(--gd-radius-sm) !important;
}

:deep(.el-input-number) {
  border-radius: var(--gd-radius-sm) !important;
}
</style>
