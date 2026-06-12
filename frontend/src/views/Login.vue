<template>
  <div class="login-page">
    <div class="bg-blobs" aria-hidden="true">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="2" width="18" height="24" rx="3" stroke="currentColor" stroke-width="2" fill="rgba(52,211,153,0.1)"/>
            <rect x="10" y="6" width="18" height="24" rx="3" stroke="currentColor" stroke-width="2" fill="rgba(52,211,153,0.15)"/>
            <line x1="14" y1="12" x2="22" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="14" y1="16" x2="20" y2="16" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="14" y1="20" x2="18" y2="20" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="login-title">简历筛选平台</h1>
        <p class="login-subtitle">{{ isRegister ? '注册新账号' : '请选择身份登录' }}</p>
      </div>

      <!-- Login -->
      <template v-if="!isRegister">
        <el-tabs v-model="loginRole" class="login-tabs" stretch>
          <el-tab-pane label="管理员" name="admin">
            <el-form @submit.prevent="handleLogin" class="login-form">
              <el-form-item>
                <el-input v-model="form.username" placeholder="管理员账户" size="large" prefix-icon="User" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">登 录</el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="用户" name="user">
            <el-form @submit.prevent="handleLogin" class="login-form">
              <el-form-item>
                <el-input v-model="form.username" placeholder="用户账户" size="large" prefix-icon="User" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">登 录</el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
        <div class="switch-link">
          还没有账号？<a @click="switchToRegister">去注册</a>
        </div>
      </template>

      <!-- Register -->
      <template v-else>
        <el-form @submit.prevent="handleRegister" class="login-form">
          <el-form-item>
            <el-input v-model="regForm.username" placeholder="账号（1-10个字符）" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="regForm.password" type="password" placeholder="密码（6-12位）" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-input v-model="regForm.confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="login-btn">注 册</el-button>
        </el-form>
        <div class="switch-link">
          已有账号？<a @click="switchToLogin">去登录</a>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loginRole = ref('admin')
const loading = ref(false)
const isRegister = ref(false)

const form = ref({ username: '', password: '' })
const regForm = ref({ username: '', password: '', confirmPassword: '' })

// 切换 Tab 时清空表单
watch(loginRole, () => {
  form.value.username = ''
  form.value.password = ''
})

function switchToRegister() {
  isRegister.value = true
  regForm.value = { username: '', password: '', confirmPassword: '' }
}

function switchToLogin() {
  isRegister.value = false
  form.value.password = ''
}

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await axios.post('/api/login', {
      username: form.value.username,
      password: form.value.password
    }, { withCredentials: true })
    if (res.data.code === 200) {
      const { role, username } = res.data.data
      localStorage.setItem('role', role)
      localStorage.setItem('username', username)
      ElMessage.success('登录成功')
      router.push(role === 'admin' ? '/upload' : '/list')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (err) {
    ElMessage.error('登录失败，请检查服务')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const { username, password, confirmPassword } = regForm.value

  if (!username || username.length > 10) {
    ElMessage.warning('账号需要1-10个字符')
    return
  }
  if (!password || password.length < 6 || password.length > 12) {
    ElMessage.warning('密码需要6-12位')
    return
  }
  if (password !== confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/register', {
      username, password
    }, { withCredentials: true })
    if (res.data.code === 200) {
      ElMessage.success('注册成功，请登录')
      switchToLogin()
      form.value.username = username
    } else {
      ElMessage.error(res.data.message || '注册失败')
    }
  } catch (err) {
    ElMessage.error('注册失败，请检查服务')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.bg-blobs {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: blobFloat 20s ease-in-out infinite;
}

.blob-1 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, #a7f3d0 0%, transparent 70%);
  top: -10%; left: -5%;
  animation-duration: 22s;
}

.blob-2 {
  width: 350px; height: 350px;
  background: radial-gradient(circle, #93c5fd 0%, transparent 70%);
  top: 50%; right: -8%;
  animation-delay: -5s;
  animation-duration: 18s;
}

.blob-3 {
  width: 300px; height: 300px;
  background: radial-gradient(circle, #c4b5fd 0%, transparent 70%);
  bottom: -5%; left: 30%;
  animation-delay: -10s;
  animation-duration: 25s;
}

@keyframes blobFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, 30px) scale(1.02); }
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(24px) saturate(1.8);
  -webkit-backdrop-filter: blur(24px) saturate(1.8);
  border: 1px solid rgba(167, 243, 208, 0.4);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(167, 243, 208, 0.2);
  padding: 40px 36px 32px;
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.login-icon {
  width: 48px;
  height: 48px;
  color: #34d399;
  margin: 0 auto 16px;
}

.login-icon svg {
  width: 100%;
  height: 100%;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
  letter-spacing: 2px;
}

.login-subtitle {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
  transition: all 0.3s;
}

/* Tabs */
.login-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background: #e5e7eb;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  color: #9ca3af;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #34d399;
  font-weight: 600;
}

.login-tabs :deep(.el-tabs__active-bar) {
  background-color: #34d399;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 44px !important;
  border-radius: 20px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  background: linear-gradient(135deg, #34d399 0%, #6ee7b7 100%) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(52, 211, 153, 0.3) !important;
  transition: all 0.3s !important;
  margin-top: 8px;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 24px rgba(52, 211, 153, 0.4) !important;
}

.switch-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #9ca3af;
}

.switch-link a {
  color: #34d399;
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
}

.switch-link a:hover {
  color: #059669;
  text-decoration: underline;
}

</style>
