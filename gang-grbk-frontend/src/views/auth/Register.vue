<template>
  <div class="auth-layout">
    <div class="auth-container">
      <!-- 左侧装饰区域 -->
      <div class="auth-decoration">
        <div class="decoration-content">
          <div class="logo-section">
            <h1 class="gradient-text">加入我们</h1>
            <p class="subtitle">创建账号，开始您的博客之旅</p>
          </div>

          <div class="feature-list">
            <div class="feature-item fade-in">
              <div class="feature-icon">✨</div>
              <div class="feature-text">
                <h3>个人空间</h3>
                <p>专属个人博客空间</p>
              </div>
            </div>
            <div class="feature-item fade-in">
              <div class="feature-icon">💬</div>
              <div class="feature-text">
                <h3>互动交流</h3>
                <p>评论点赞分享观点</p>
              </div>
            </div>
            <div class="feature-item fade-in">
              <div class="feature-icon">📊</div>
              <div class="feature-text">
                <h3>数据统计</h3>
                <p>文章访问量统计分析</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="auth-form-wrapper">
        <div class="auth-form-container">
          <div class="auth-header">
            <h2>创建新账号</h2>
            <p class="text-secondary">填写下方信息完成账号注册</p>
          </div>

          <form class="auth-form" @submit.prevent="handleRegister">
            <div class="form-group">
              <label for="username" class="form-label">
                <el-icon><User /></el-icon>
                用户名
              </label>
              <input
                id="username"
                v-model="registerForm.username"
                type="text"
                class="input"
                placeholder="请输入用户名（3-20字符）"
                :class="{ 'input-error': errors.username }"
                @blur="validateUsername"
              />
              <span v-if="errors.username" class="error-text">{{ errors.username }}</span>
            </div>

            <div class="form-group">
              <label for="email" class="form-label">
                <el-icon><Message /></el-icon>
                邮箱地址
              </label>
              <input
                id="email"
                v-model="registerForm.email"
                type="email"
                class="input"
                placeholder="请输入邮箱地址"
                :class="{ 'input-error': errors.email }"
                @blur="validateEmail"
              />
              <span v-if="errors.email" class="error-text">{{ errors.email }}</span>
            </div>

            <div class="form-group">
              <label for="password" class="form-label">
                <el-icon><Lock /></el-icon>
                密码
              </label>
              <div class="password-input">
                <input
                  id="password"
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="input"
                  placeholder="请输入密码（至少6个字符）"
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

            <div class="form-group">
              <label for="confirmPassword" class="form-label">
                <el-icon><Lock /></el-icon>
                确认密码
              </label>
              <div class="password-input">
                <input
                  id="confirmPassword"
                  v-model="registerForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="input"
                  placeholder="请再次输入密码"
                  :class="{ 'input-error': errors.confirmPassword }"
                  @blur="validateConfirmPassword"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <el-icon>
                    <View v-if="showConfirmPassword" />
                    <Hide v-else />
                  </el-icon>
                </button>
              </div>
              <span v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</span>
            </div>

            <div class="form-group">
              <label for="nickname" class="form-label">
                <el-icon><Avatar /></el-icon>
                昵称
              </label>
              <input
                id="nickname"
                v-model="registerForm.nickname"
                type="text"
                class="input"
                placeholder="请输入昵称（选填）"
                :class="{ 'input-error': errors.nickname }"
                @blur="validateNickname"
              />
              <span v-if="errors.nickname" class="error-text">{{ errors.nickname }}</span>
            </div>

            <div class="form-options">
              <label class="checkbox-wrapper">
                <input v-model="registerForm.agreeTerms" type="checkbox" />
                <span class="checkmark"></span>
                我已阅读并同意
                <a href="#" class="link-accent">用户协议</a>
                和
                <a href="#" class="link-accent">隐私政策</a>
              </label>
            </div>

            <button
              type="submit"
              class="btn btn-primary btn-lg w-full"
              :class="{ 'btn-loading': loading }"
              :disabled="loading || !registerForm.agreeTerms"
            >
              <span class="btn-text">注册账号</span>
            </button>
          </form>

          <div class="auth-footer">
            <p class="text-center text-secondary">
              已有账号？
              <router-link to="/auth/login" class="link-accent">立即登录</router-link>
            </p>
          </div>

          <!-- 注册提示 -->
          <div class="register-hint">
            <div class="hint-card">
              <div class="hint-header">
                <el-icon><InfoFilled /></el-icon>
                <span>注册提示</span>
              </div>
              <div class="hint-content">
                <p>• 用户名一经创建不可修改</p>
                <p>• 邮箱用于找回密码和重要通知</p>
                <p>• 请使用强密码保护账号安全</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide, InfoFilled, Message, Avatar } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  agreeTerms: false
})

const errors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

// 表单验证
const validateUsername = () => {
  if (!registerForm.username) {
    errors.username = '请输入用户名'
    return false
  }
  if (registerForm.username.length < 3) {
    errors.username = '用户名至少3个字符'
    return false
  }
  if (registerForm.username.length > 20) {
    errors.username = '用户名不能超过20个字符'
    return false
  }
  if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(registerForm.username)) {
    errors.username = '用户名只能包含字母、数字、下划线和中文'
    return false
  }
  errors.username = ''
  return true
}

const validateEmail = () => {
  if (!registerForm.email) {
    errors.email = '请输入邮箱地址'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    errors.email = '请输入有效的邮箱地址'
    return false
  }
  errors.email = ''
  return true
}

const validatePassword = () => {
  if (!registerForm.password) {
    errors.password = '请输入密码'
    return false
  }
  if (registerForm.password.length < 6) {
    errors.password = '密码至少6个字符'
    return false
  }
  if (registerForm.password.length > 30) {
    errors.password = '密码不能超过30个字符'
    return false
  }
  errors.password = ''
  return true
}

const validateConfirmPassword = () => {
  if (!registerForm.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    return false
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    return false
  }
  errors.confirmPassword = ''
  return true
}

const validateNickname = () => {
  if (registerForm.nickname && registerForm.nickname.length > 50) {
    errors.nickname = '昵称不能超过50个字符'
    return false
  }
  errors.nickname = ''
  return true
}

const validateForm = () => {
  const usernameValid = validateUsername()
  const emailValid = validateEmail()
  const passwordValid = validatePassword()
  const confirmPasswordValid = validateConfirmPassword()
  const nicknameValid = validateNickname()

  return usernameValid && emailValid && passwordValid && confirmPasswordValid && nicknameValid && registerForm.agreeTerms
}

// 注册处理
const handleRegister = async () => {
  if (!validateForm()) {
    if (!registerForm.agreeTerms) {
      ElMessage.error('请先同意用户协议和隐私政策')
    }
    return
  }

  loading.value = true

  try {
    const response = await axios.post('/api/v1/auth/register', {
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      nickname: registerForm.nickname || registerForm.username
    })

    if (response.data.code === 200) {
      ElMessage.success('注册成功！请登录您的账号')
      // 跳转到登录页面
      router.push('/auth/login')
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '注册失败')
    } else {
      ElMessage.error('注册失败，请检查网络连接或联系管理员')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--success-50) 0%, var(--gray-50) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
}

.auth-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1100px;
  width: 100%;
  background: var(--bg-elevated);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
  min-height: 700px;
}

/* 左侧装饰区域 */
.auth-decoration {
  background: linear-gradient(135deg, var(--success-600) 0%, var(--success-800) 100%);
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
  padding: var(--space-8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}

.auth-form-container {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.auth-header {
  text-align: center;
  margin-bottom: var(--space-6);
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
  gap: var(--space-4);
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
  margin: var(--space-2) 0;
}

.checkbox-wrapper {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
  cursor: pointer;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.4;
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
  flex-shrink: 0;
  margin-top: 2px;
}

.checkbox-wrapper input:checked + .checkmark {
  background: var(--success-500);
  border-color: var(--success-500);
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
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-secondary);
}

.register-hint {
  margin-top: var(--space-4);
}

.hint-card {
  background: var(--success-50);
  border: 1px solid var(--success-200);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.hint-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--success-700);
  margin-bottom: var(--space-3);
}

.hint-content p {
  font-size: var(--text-xs);
  color: var(--success-600);
  margin: var(--space-1) 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 480px;
    min-height: auto;
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
}

@media (max-width: 480px) {
  .auth-layout {
    padding: var(--space-2);
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

  .auth-form {
    gap: var(--space-3);
  }
}
</style>