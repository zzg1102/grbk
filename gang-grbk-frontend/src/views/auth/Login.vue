<template>
  <div class="auth-layout">
    <div class="auth-container">
      <!-- 左侧装饰区域 -->
      <div class="auth-decoration">
        <div class="decoration-content">
          <div class="logo-section">
            <h1 class="gradient-text">Gang's Blog</h1>
            <p class="subtitle">现代化个人博客系统</p>
          </div>

          <div class="feature-list">
            <div class="feature-item fade-in">
              <div class="feature-icon">🚀</div>
              <div class="feature-text">
                <h3>现代技术栈</h3>
                <p>Spring Boot 3 + Vue 3</p>
              </div>
            </div>
            <div class="feature-item fade-in">
              <div class="feature-icon">🎨</div>
              <div class="feature-text">
                <h3>简约设计</h3>
                <p>响应式现代化界面</p>
              </div>
            </div>
            <div class="feature-item fade-in">
              <div class="feature-icon">🔐</div>
              <div class="feature-text">
                <h3>安全可靠</h3>
                <p>JWT认证 + 权限控制</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="auth-form-wrapper">
        <div class="auth-form-container">
          <div class="auth-header">
            <h2>欢迎登录</h2>
            <p class="text-secondary">使用您的账号登录博客管理系统</p>
          </div>

          <form class="auth-form" @submit.prevent="handleLogin">
            <div class="form-group">
              <label for="username" class="form-label">
                <el-icon><User /></el-icon>
                用户名
              </label>
              <input
                id="username"
                v-model="loginForm.username"
                type="text"
                class="input"
                placeholder="请输入用户名"
                :class="{ 'input-error': errors.username }"
                @blur="validateUsername"
              />
              <span v-if="errors.username" class="error-text">{{ errors.username }}</span>
            </div>

            <div class="form-group">
              <label for="password" class="form-label">
                <el-icon><Lock /></el-icon>
                密码
              </label>
              <div class="password-input">
                <input
                  id="password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="input"
                  placeholder="请输入密码"
                  :class="{ 'input-error': errors.password }"
                  @blur="validatePassword"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <el-icon>
                    <View v-if="showPassword" />
                    <Hide v-else />
                  </el-icon>
                </button>
              </div>
              <span v-if="errors.password" class="error-text">{{ errors.password }}</span>
            </div>

            <div class="form-options">
              <label class="checkbox-wrapper">
                <input v-model="loginForm.rememberMe" type="checkbox" />
                <span class="checkmark"></span>
                记住我
              </label>
              <router-link to="/auth/forgot-password" class="link-subtle">
                忘记密码？
              </router-link>
            </div>

            <button
              type="submit"
              class="btn btn-primary btn-lg w-full"
              :class="{ 'btn-loading': loading }"
              :disabled="loading"
            >
              <span class="btn-text">登录</span>
            </button>
          </form>

          <div class="auth-footer">
            <p class="text-center text-secondary">
              还没有账号？
              <router-link to="/auth/register" class="link-accent">立即注册</router-link>
            </p>
          </div>

          <!-- 快速登录提示 -->
          <div class="quick-login-hint">
            <div class="hint-card">
              <div class="hint-header">
                <el-icon><InfoFilled /></el-icon>
                <span>快速登录</span>
              </div>
              <div class="hint-content">
                <p>测试账号：<code>admin</code></p>
                <p>测试密码：<code>123456</code></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const showPassword = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const errors = reactive({
  username: '',
  password: ''
})

// 表单验证
const validateUsername = () => {
  if (!loginForm.username) {
    errors.username = '请输入用户名'
    return false
  }
  if (loginForm.username.length < 3) {
    errors.username = '用户名至少3个字符'
    return false
  }
  errors.username = ''
  return true
}

const validatePassword = () => {
  if (!loginForm.password) {
    errors.password = '请输入密码'
    return false
  }
  if (loginForm.password.length < 6) {
    errors.password = '密码至少6个字符'
    return false
  }
  errors.password = ''
  return true
}

const validateForm = () => {
  const usernameValid = validateUsername()
  const passwordValid = validatePassword()
  return usernameValid && passwordValid
}

// 登录处理
const handleLogin = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post('/api/v1/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })

    if (response.data.code === 200) {
      const loginData = response.data.data

      // 存储token和用户信息
      localStorage.setItem('auth_token', loginData.token)
      localStorage.setItem('user_info', JSON.stringify(loginData))

      // 记住我功能
      if (loginForm.rememberMe) {
        localStorage.setItem('remember_username', loginForm.username)
      } else {
        localStorage.removeItem('remember_username')
      }

      ElMessage.success('登录成功！')

      // 根据用户类型进行重定向
      let redirectPath = '/'

      // 检查是否有预设的redirect参数
      const queryRedirect = router.currentRoute.value.query.redirect

      if (queryRedirect) {
        // 如果有预设redirect，使用它
        redirectPath = queryRedirect
      } else {
        // 根据用户角色决定默认跳转页面
        if (loginData.userType === 1) {
          // 管理员用户跳转到管理后台
          redirectPath = '/admin'
        } else {
          // 普通用户跳转到首页
          redirectPath = '/'
        }
      }

      router.push(redirectPath)
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查网络连接或联系管理员')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  // 检查记住的用户名
  const rememberedUsername = localStorage.getItem('remember_username')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.rememberMe = true
  }

  // 检查是否已经登录
  const token = localStorage.getItem('auth_token')
  if (token) {
    router.push('/')
  }
})
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--gray-50) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
}

.auth-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  background: var(--bg-elevated);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
  min-height: 600px;
}

/* 左侧装饰区域 */
.auth-decoration {
  background: linear-gradient(135deg, var(--primary-600) 0%, var(--primary-800) 100%);
  color: white;
  padding: var(--space-10);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo-section {
  margin-bottom: var(--space-10);
}

.logo-section h1 {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
  background: linear-gradient(135deg, #ffffff, #e2e8f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  opacity: 0.9;
}

.feature-icon {
  font-size: var(--text-3xl);
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(8px);
}

.feature-text h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0 0 var(--space-1) 0;
  color: white;
}

.feature-text p {
  font-size: var(--text-sm);
  margin: 0;
  opacity: 0.8;
}

/* 右侧表单区域 */
.auth-form-wrapper {
  padding: var(--space-10);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-form-container {
  max-width: 380px;
  margin: 0 auto;
  width: 100%;
}

.auth-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.auth-header h2 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.form-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.password-input {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: color var(--duration-fast) var(--ease-out);
}

.password-toggle:hover {
  color: var(--text-secondary);
}

.input-error {
  border-color: var(--error) !important;
}

.error-text {
  color: var(--error);
  font-size: var(--text-xs);
  margin-top: var(--space-1);
  display: block;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.checkbox-wrapper input {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-sm);
  display: inline-block;
  position: relative;
  transition: all var(--duration-fast) var(--ease-out);
}

.checkbox-wrapper input:checked + .checkmark {
  background: var(--primary-500);
  border-color: var(--primary-500);
}

.checkbox-wrapper input:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  top: -2px;
  left: 1px;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.auth-footer {
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-secondary);
}

.quick-login-hint {
  margin-top: var(--space-6);
}

.hint-card {
  background: var(--primary-50);
  border: 1px solid var(--primary-200);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.hint-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--primary-700);
  margin-bottom: var(--space-3);
}

.hint-content p {
  font-size: var(--text-sm);
  color: var(--primary-600);
  margin: var(--space-1) 0;
}

.hint-content code {
  background: var(--primary-100);
  color: var(--primary-800);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-family: var(--font-mono);
  font-size: 11px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 480px;
  }

  .auth-decoration {
    padding: var(--space-6);
    text-align: center;
  }

  .logo-section h1 {
    font-size: var(--text-3xl);
  }

  .feature-list {
    flex-direction: row;
    justify-content: space-around;
    gap: var(--space-4);
  }

  .feature-item {
    flex-direction: column;
    text-align: center;
  }

  .feature-text h3 {
    font-size: var(--text-base);
  }

  .feature-text p {
    font-size: var(--text-xs);
  }

  .auth-form-wrapper {
    padding: var(--space-6);
  }

  .form-options {
    flex-direction: column;
    gap: var(--space-3);
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .auth-layout {
    padding: var(--space-2);
  }

  .auth-container {
    min-height: auto;
  }

  .auth-decoration {
    padding: var(--space-4);
  }

  .logo-section {
    margin-bottom: var(--space-6);
  }

  .feature-item {
    gap: var(--space-2);
  }

  .feature-icon {
    width: 40px;
    height: 40px;
    font-size: var(--text-lg);
  }
}
</style>