<template>
  <el-container class="app-shell">
    <!-- Animated background blobs -->
    <div class="bg-blobs" aria-hidden="true">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
      <div class="blob blob-4"></div>
    </div>

    <el-header class="app-header">
      <div class="header-content">
        <div class="brand">
          <div class="brand-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="4" y="2" width="18" height="24" rx="3" stroke="currentColor" stroke-width="2" fill="rgba(255,255,255,0.15)"/>
              <rect x="10" y="6" width="18" height="24" rx="3" stroke="currentColor" stroke-width="2" fill="rgba(255,255,255,0.25)"/>
              <line x1="14" y1="12" x2="22" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="14" y1="16" x2="20" y2="16" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="14" y1="20" x2="18" y2="20" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <h1 class="app-title">简历筛选平台</h1>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" :ellipsis="false" router class="nav-menu">
          <el-menu-item index="/upload">
            <el-icon><Upload /></el-icon>
            <span>简历上传</span>
          </el-menu-item>
          <el-menu-item index="/list">
            <el-icon><Filter /></el-icon>
            <span>简历筛选</span>
          </el-menu-item>
          <el-menu-item index="/favorites">
            <el-icon><Star /></el-icon>
            <span>收藏简历</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-header>
    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Upload, Filter, Star } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
/* ═══════════════════════════════════════════
   Global Styles & CSS Variables
   ═══════════════════════════════════════════ */
:root {
  /* Gradient diffused palette */
  --gd-lavender: #c4b5fd;
  --gd-rose: #fda4af;
  --gd-sky: #93c5fd;
  --gd-peach: #fed7aa;
  --gd-mint: #a7f3d0;

  --gd-bg-start: #f0eef5;
  --gd-bg-end: #fdf2f8;

  --gd-primary: #7c5cbf;
  --gd-primary-light: #a78bfa;
  --gd-primary-dark: #5b21b6;

  --gd-accent: #ec4899;
  --gd-accent-light: #f9a8d4;

  --gd-surface: rgba(255, 255, 255, 0.65);
  --gd-surface-hover: rgba(255, 255, 255, 0.8);
  --gd-surface-solid: #ffffff;

  --gd-text: #1e1b4b;
  --gd-text-secondary: #6b7280;
  --gd-text-muted: #9ca3af;

  --gd-border: rgba(196, 181, 253, 0.3);
  --gd-border-focus: rgba(124, 92, 191, 0.5);

  --gd-shadow-sm: 0 1px 3px rgba(124, 92, 191, 0.08), 0 1px 2px rgba(124, 92, 191, 0.04);
  --gd-shadow-md: 0 4px 12px rgba(124, 92, 191, 0.1), 0 2px 4px rgba(124, 92, 191, 0.06);
  --gd-shadow-lg: 0 10px 30px rgba(124, 92, 191, 0.12), 0 4px 8px rgba(124, 92, 191, 0.08);
  --gd-shadow-glow: 0 0 40px rgba(167, 139, 250, 0.15);

  --gd-radius-sm: 10px;
  --gd-radius-md: 16px;
  --gd-radius-lg: 24px;
  --gd-radius-xl: 32px;

  --gd-font-display: 'ZCOOL QingKe HuangYou', 'Noto Sans SC', sans-serif;
  --gd-font-body: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;

  --gd-transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: var(--gd-font-body);
  background: linear-gradient(135deg, var(--gd-bg-start) 0%, var(--gd-bg-end) 50%, #ede9fe 100%);
  color: var(--gd-text);
  min-height: 100vh;
  -webkit-font-smoothing: antialiased;
}

/* Override Element Plus variables */
:root {
  --el-color-primary: #7c5cbf;
  --el-color-primary-light-3: #a78bfa;
  --el-color-primary-light-5: #c4b5fd;
  --el-color-primary-light-7: #ddd6fe;
  --el-color-primary-light-9: #f5f3ff;
  --el-color-primary-dark-2: #5b21b6;
  --el-border-radius-base: 10px;
  --el-font-family: var(--gd-font-body);
}

/* ═══════════════════════════════════════════
   Animated Background Blobs
   ═══════════════════════════════════════════ */
.bg-blobs {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: blobFloat 20s ease-in-out infinite;
}

.blob-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, var(--gd-lavender) 0%, transparent 70%);
  top: -10%;
  left: -5%;
  animation-delay: 0s;
  animation-duration: 22s;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--gd-rose) 0%, transparent 70%);
  top: 50%;
  right: -8%;
  animation-delay: -5s;
  animation-duration: 18s;
}

.blob-3 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, var(--gd-sky) 0%, transparent 70%);
  bottom: -5%;
  left: 30%;
  animation-delay: -10s;
  animation-duration: 25s;
}

.blob-4 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--gd-peach) 0%, transparent 70%);
  top: 30%;
  left: 50%;
  animation-delay: -15s;
  animation-duration: 20s;
}

@keyframes blobFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, 30px) scale(1.02); }
}

/* ═══════════════════════════════════════════
   App Shell Layout
   ═══════════════════════════════════════════ */
.app-shell {
  position: relative;
  z-index: 1;
  min-height: 100vh;
}

.app-header {
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(20px) saturate(1.8);
  -webkit-backdrop-filter: blur(20px) saturate(1.8);
  border-bottom: 1px solid var(--gd-border);
  padding: 0 32px;
  height: 64px !important;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: background var(--gd-transition);
}

.header-content {
  display: flex;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.brand-icon {
  width: 36px;
  height: 36px;
  color: var(--gd-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.app-title {
  font-family: var(--gd-font-display);
  font-size: 22px;
  font-weight: 400;
  background: linear-gradient(135deg, var(--gd-primary) 0%, var(--gd-accent) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  white-space: nowrap;
  letter-spacing: 2px;
}

.nav-menu {
  border-bottom: none !important;
  background: transparent !important;
  flex: 1;
}

.nav-menu .el-menu-item {
  font-family: var(--gd-font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--gd-text-secondary) !important;
  border-bottom: none !important;
  background: transparent !important;
  transition: all var(--gd-transition);
  border-radius: var(--gd-radius-sm);
  margin: 8px 4px;
  height: calc(100% - 16px) !important;
  line-height: calc(100% - 16px) !important;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-menu .el-menu-item:hover {
  color: var(--gd-primary) !important;
  background: rgba(124, 92, 191, 0.06) !important;
}

.nav-menu .el-menu-item.is-active {
  color: var(--gd-primary) !important;
  background: rgba(124, 92, 191, 0.1) !important;
  font-weight: 600;
}

.app-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
  position: relative;
  z-index: 1;
}

/* ═══════════════════════════════════════════
   Page Transitions
   ═══════════════════════════════════════════ */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ═══════════════════════════════════════════
   Global Glass Surface
   ═══════════════════════════════════════════ */
.glass-card {
  background: var(--gd-surface);
  backdrop-filter: blur(16px) saturate(1.5);
  -webkit-backdrop-filter: blur(16px) saturate(1.5);
  border: 1px solid var(--gd-border);
  border-radius: var(--gd-radius-lg);
  box-shadow: var(--gd-shadow-md);
  transition: all var(--gd-transition);
}

.glass-card:hover {
  box-shadow: var(--gd-shadow-lg);
}

/* ═══════════════════════════════════════════
   Match Highlight Style
   ═══════════════════════════════════════════ */
.match-highlight {
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.35) 0%, rgba(252, 211, 77, 0.45) 100%);
  color: #92400e;
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 700;
  box-shadow: 0 0 8px rgba(251, 191, 36, 0.2);
}

/* ═══════════════════════════════════════════
   Element Plus Overrides for Diffused Style
   ═══════════════════════════════════════════ */
.el-card {
  background: var(--gd-surface) !important;
  backdrop-filter: blur(16px) saturate(1.5) !important;
  -webkit-backdrop-filter: blur(16px) saturate(1.5) !important;
  border: 1px solid var(--gd-border) !important;
  border-radius: var(--gd-radius-lg) !important;
  box-shadow: var(--gd-shadow-md) !important;
}

.el-dialog {
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(24px) saturate(1.8) !important;
  -webkit-backdrop-filter: blur(24px) saturate(1.8) !important;
  border: 1px solid var(--gd-border) !important;
  border-radius: var(--gd-radius-lg) !important;
  box-shadow: var(--gd-shadow-lg) !important;
}

.el-dialog__header {
  border-bottom: 1px solid var(--gd-border);
  padding-bottom: 16px;
}

.el-input__wrapper,
.el-textarea__inner,
.el-select__wrapper {
  border-radius: var(--gd-radius-sm) !important;
  transition: all var(--gd-transition) !important;
}

.el-input__wrapper:hover,
.el-textarea__inner:hover {
  box-shadow: 0 0 0 1px var(--gd-primary-light) inset !important;
}

.el-input__wrapper.is-focus,
.el-textarea__inner:focus {
  box-shadow: 0 0 0 1px var(--gd-primary) inset, 0 0 12px rgba(124, 92, 191, 0.1) !important;
}

.el-button--primary {
  background: linear-gradient(135deg, var(--gd-primary) 0%, var(--gd-accent) 100%) !important;
  border: none !important;
  border-radius: var(--gd-radius-sm) !important;
  font-weight: 500 !important;
  transition: all var(--gd-transition) !important;
  box-shadow: 0 2px 8px rgba(124, 92, 191, 0.25) !important;
}

.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(124, 92, 191, 0.35) !important;
  opacity: 0.92;
}

.el-button--default {
  border-radius: var(--gd-radius-sm) !important;
  border-color: var(--gd-border) !important;
  transition: all var(--gd-transition) !important;
}

.el-button--default:hover {
  color: var(--gd-primary) !important;
  border-color: var(--gd-primary-light) !important;
  background: rgba(124, 92, 191, 0.04) !important;
}

.el-pagination {
  --el-pagination-button-color: var(--gd-text-secondary);
  --el-pagination-hover-color: var(--gd-primary);
}

.el-tag {
  border-radius: 20px !important;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--gd-lavender);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--gd-primary-light);
}
</style>
