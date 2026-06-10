<template>
  <div class="filter-section">
    <el-form :model="filterForm" inline label-width="auto" @submit.prevent>
      <el-form-item label="期望地点">
        <el-select
          v-model="filterForm.locations"
          multiple
          filterable
          allow-create
          placeholder="选择或输入地点"
          style="width: 240px"
        >
          <el-option v-for="loc in locationOptions" :key="loc" :label="loc" :value="loc" />
        </el-select>
      </el-form-item>

      <el-form-item label="最低年限">
        <el-input-number
          v-model="filterForm.minWorkYears"
          :min="0"
          :max="50"
          placeholder="年"
          style="width: 120px"
        />
      </el-form-item>

      <el-form-item label="学历">
        <el-select
          v-model="filterForm.educations"
          multiple
          placeholder="选择学历"
          style="width: 240px"
        >
          <el-option v-for="edu in educationOptions" :key="edu" :label="edu" :value="edu" />
        </el-select>
      </el-form-item>

      <el-form-item label="期望薪资(K)">
        <el-col :span="11">
          <el-input-number v-model="filterForm.salaryMin" :min="0" placeholder="最低" style="width: 100%" />
        </el-col>
        <el-col :span="2" style="text-align: center">-</el-col>
        <el-col :span="11">
          <el-input-number v-model="filterForm.salaryMax" :min="0" placeholder="最高" style="width: 100%" />
        </el-col>
      </el-form-item>

      <el-form-item label="求职状态">
        <el-radio-group v-model="filterForm.jobStatus">
          <el-radio label="">全部</el-radio>
          <el-radio label="在职">在职</el-radio>
          <el-radio label="离职">离职</el-radio>
          <el-radio label="随时到岗">随时到岗</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="$emit('search', getFilterParams())">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['search'])

const locationOptions = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '南京', '苏州', '西安']
const educationOptions = ['博士', '硕士', '本科', '大专', '高中及以下']

const filterForm = reactive({
  locations: [],
  minWorkYears: null,
  educations: [],
  salaryMin: null,
  salaryMax: null,
  jobStatus: ''
})

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
  return params
}

function resetFilter() {
  filterForm.locations = []
  filterForm.minWorkYears = null
  filterForm.educations = []
  filterForm.salaryMin = null
  filterForm.salaryMax = null
  filterForm.jobStatus = ''
  emit('search', {})
}

defineExpose({ getFilterParams })
</script>

<style scoped>
.filter-section {
  background: #fff;
  padding: 16px 20px 0;
  border-radius: 4px;
  margin-bottom: 16px;
}
</style>
