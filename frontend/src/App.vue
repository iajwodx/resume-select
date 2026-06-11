<template>
  <el-container>
    <el-header class="app-header">
      <div class="header-content">
        <h1 class="app-title">简历筛选平台</h1>
        <el-menu mode="horizontal" :default-active="activeMenu" :ellipsis="false" router>
          <el-menu-item index="/upload">简历上传</el-menu-item>
          <el-menu-item index="/list">简历筛选</el-menu-item>
          <el-menu-item index="/favorites">收藏简历</el-menu-item>
        </el-menu>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #f5f7fa;
}

.app-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.app-title {
  font-size: 20px;
  color: #409eff;
  margin: 0 20px 0 0;
  white-space: nowrap;
}

.el-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}
</style>
