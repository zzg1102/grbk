<template>
  <div class="layout home-layout">
    <!-- 现代导航栏 -->
    <header class="navbar layout-header">
      <div class="navbar-container">
        <a href="/" class="navbar-brand gradient-text">Gang's Blog</a>

        <nav class="navbar-nav">
          <a href="/" class="active">首页</a>
          <a href="/articles">文章</a>
          <a href="/categories">分类</a>
          <a href="/tags">标签</a>
          <a href="/about">关于</a>
        </nav>

        <div class="navbar-actions">
          <button class="btn btn-ghost btn-sm" @click="toggleTheme">
            <el-icon><Sunny v-if="isDark" /><Moon v-else /></el-icon>
          </button>

          <!-- 未登录状态 -->
          <template v-if="!isLoggedIn">
            <router-link to="/auth/login" class="btn btn-ghost btn-sm">
              登录
            </router-link>
            <router-link to="/auth/register" class="btn btn-primary btn-sm">
              注册
            </router-link>
          </template>

          <!-- 已登录状态 -->
          <template v-else>
            <div class="user-menu">
              <el-dropdown @command="handleUserAction" placement="bottom-end" trigger="click">
                <div class="user-avatar">
                  <el-icon><User /></el-icon>
                </div>
                <template #dropdown>
                  <div class="custom-dropdown">
                    <div class="dropdown-item" @click="handleUserAction('profile')">
                      <el-icon><User /></el-icon>
                      <span>个人资料</span>
                    </div>
                    <div v-if="isAdmin" class="dropdown-item" @click="handleUserAction('admin')">
                      <el-icon><Setting /></el-icon>
                      <span>管理后台</span>
                    </div>
                    <div class="dropdown-divider"></div>
                    <div class="dropdown-item logout-item" @click="handleUserAction('logout')">
                      <el-icon><SwitchButton /></el-icon>
                      <span>退出登录</span>
                    </div>
                  </div>
                </template>
              </el-dropdown>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="layout-main">
      <!-- 创作引导区域 - 只对登录用户显示 -->
      <section class="create-inspiration" v-if="isLoggedIn">
        <div class="container">
          <div class="inspiration-content">
            <div class="inspiration-text">
              <h2>今天想分享什么呢？</h2>
              <p>记录生活的美好瞬间，分享你的独特见解</p>
            </div>
            <router-link to="/write" class="inspiration-btn">
              <el-icon><EditPen /></el-icon>
              <span>开始写作</span>
            </router-link>
          </div>
        </div>
      </section>

      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="container">
          <h1 class="hero-title fade-in">欢迎来到我的博客</h1>
          <p class="hero-description slide-up">
            分享技术见解，记录思考历程，探索代码之美。
            这里是关于Spring Boot、Vue.js和现代Web开发的技术博客。
          </p>
          <div class="flex justify-center gap-4 scale-in">
            <button class="btn btn-primary btn-lg" @click="viewArticles">
              <el-icon><Reading /></el-icon>
              开始阅读
            </button>
            <button class="btn btn-secondary btn-lg" @click="viewDocs">
              <el-icon><Document /></el-icon>
              API文档
            </button>
          </div>
        </div>
      </section>

      <!-- 特色内容 -->
      <section class="featured-section">
        <div class="container">
          <h2 class="section-title">技术栈特色</h2>
          <div class="card-grid">
            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #e0f2fe; color: #0369a1; border-color: #7dd3fc;">
                    🚀
                  </div>
                  <h3 class="h4 mb-0">现代后端</h3>
                </div>
                <p class="text-secondary">
                  Spring Boot 3 + MyBatis Plus + MySQL + Redis + RabbitMQ
                  构建高性能、可扩展的后端服务
                </p>
              </div>
            </div>

            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #f0fdf4; color: #166534; border-color: #86efac;">
                    🎨
                  </div>
                  <h3 class="h4 mb-0">现代前端</h3>
                </div>
                <p class="text-secondary">
                  Vue 3 + Element Plus + Vite
                  打造流畅的用户体验和现代化界面
                </p>
              </div>
            </div>

            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #fef3c7; color: #92400e; border-color: #fde68a;">
                    🔐
                  </div>
                  <h3 class="h4 mb-0">企业级安全</h3>
                </div>
                <p class="text-secondary">
                  JWT认证、权限控制、XSS防护
                  确保系统安全可靠
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 最新文章 -->
      <section class="latest-section bg-secondary" v-if="latestArticles.length > 0">
        <div class="container">
          <h2 class="section-title">最新文章</h2>
          <div class="card-grid-large">
            <article 
              v-for="article in latestArticles" 
              :key="article.id"
              class="card card-article hover-lift"
              @click="viewArticle(article.id)"
            >
              <div class="card-body">
                <div class="card-meta">
                  <span>{{ formatDate(article.createTime) }}</span>
                  <span>•</span>
                  <span>{{ article.categoryName || '未分类' }}</span>
                </div>
                <h3 class="card-title">
                  <span class="article-title">{{ article.title }}</span>
                </h3>
                <p class="text-secondary">
                  {{ article.summary || '暂无摘要' }}
                </p>
                <div class="card-tags" v-if="article.tags && article.tags.length">
                  <span 
                    v-for="tag in article.tags.slice(0, 3)" 
                    :key="tag.id"
                    class="tag"
                  >
                    {{ tag.name }}
                  </span>
                </div>
              </div>
            </article>
          </div>

          <div class="text-center mt-8">
            <button class="btn btn-ghost" @click="viewAllArticles">
              查看更多文章
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </section>

      <!-- API测试结果 -->
      <div v-if="apiResult" class="container mt-8">
        <div class="card glass-effect">
          <div class="card-header">
            <h3 class="card-title">API连接测试结果</h3>
            <span class="tag" :class="apiResult.success ? 'tag-success' : 'tag-error'">
              {{ apiResult.success ? '连接成功' : '连接失败' }}
            </span>
          </div>
          <div class="card-body">
            <pre><code>{{ apiResult.data }}</code></pre>
          </div>
        </div>
      </div>
    </main>

    <!-- 现代页脚 -->
    <footer class="layout-footer bg-secondary border-t">
      <div class="container py-8">
        <div class="text-center">
          <p class="text-secondary">
            &copy; 2024 Gang的个人博客 - 使用
            <span class="gradient-text font-semibold">Spring Boot 3</span>
            &
            <span class="gradient-text font-semibold">Vue 3</span>
            构建
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Connection, Document, Reading, Sunny, Moon, ArrowRight, User, EditPen, Setting, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const apiResult = ref(null)
const isDark = ref(false)
const latestArticles = ref([])

// 登录状态管理
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 检查是否为管理员
const isAdmin = computed(() => {
  return userInfo.value?.userType === 1
})

// 主题切换
const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('auth_token')
  const userInfoStr = localStorage.getItem('user_info')

  if (token && userInfoStr) {
    try {
      const userData = JSON.parse(userInfoStr)
      isLoggedIn.value = true
      userInfo.value = userData
    } catch (error) {
      console.error('解析用户信息失败:', error)
      isLoggedIn.value = false
      userInfo.value = null
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user_info')
    }
  } else {
    isLoggedIn.value = false
    userInfo.value = null
  }
}

const handleUserAction = (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人资料页面
      ElMessage.info('个人资料功能开发中')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录处理
const handleLogout = async () => {
  try {
    const token = localStorage.getItem('auth_token')
    if (token) {
      await axios.post('/api/v1/auth/logout', {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
    }
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    // 清除本地存储
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_info')
    localStorage.removeItem('remember_username')

    // 更新状态
    isLoggedIn.value = false
    userInfo.value = null

    ElMessage.success('退出登录成功')

    // 刷新页面或重定向到首页
    router.push('/')
  }
}

// 加载最新文章
const loadLatestArticles = async () => {
  try {
    const response = await axios.get('/api/v1/articles/latest?limit=3')
    if (response.data.code === 200) {
      latestArticles.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载最新文章失败:', error)
  }
}

// 查看文章详情
const viewArticle = (articleId) => {
  router.push(`/article/${articleId}`)
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    month: 'long',
    day: 'numeric'
  })
}

// 初始化
onMounted(() => {
  // 检查登录状态
  checkLoginStatus()

  // 加载最新文章
  loadLatestArticles()

  // 初始化主题
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

  isDark.value = savedTheme === 'dark' || (!savedTheme && prefersDark)
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
})

// 测试后端连接
const testConnection = async () => {
  loading.value = true
  apiResult.value = null

  try {
    const response = await axios.get('/api/v1/test/hello', { timeout: 5000 })
    apiResult.value = {
      success: true,
      data: JSON.stringify(response.data, null, 2)
    }
  } catch (error) {
    apiResult.value = {
      success: false,
      data: `连接失败: ${error.message}\n\n后端服务可能未启动，请确保:\n1. 数据库服务已启动\n2. 后端Spring Boot应用已启动\n3. 端口8080未被占用`
    }
  } finally {
    loading.value = false
  }
}

// 导航方法
const viewDocs = () => {
  window.open('/api/doc.html', '_blank')
}

const viewArticles = () => {
  router.push('/articles')
}

const viewAllArticles = () => {
  router.push('/articles')
}
</script>

<style scoped>
/* 导航栏样式覆盖 */
.navbar-container {
  max-width: none !important;
  margin: 0 !important;
  padding: 0 var(--space-4) !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

@media (max-width: 768px) {
  .navbar-container {
    padding: 0 var(--space-3) !important;
  }
}

.navbar-brand {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--primary-600) !important;
  text-decoration: none;
  transition: all var(--duration-fast) var(--ease-out);
  flex-shrink: 0;
}

.navbar-brand:hover {
  color: var(--primary-700) !important;
  transform: scale(1.02);
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-shrink: 0;
}

/* 用户菜单样式 */
.user-menu .user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary-100);
  color: var(--primary-600);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.user-menu .user-avatar:hover {
  background: var(--primary-200);
}

/* 自定义下拉菜单样式 */
.custom-dropdown {
  background: var(--bg-elevated);
  border-radius: var(--radius-md);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-primary);
  padding: var(--space-1);
  min-width: 120px;
  width: max-content;
  overflow: hidden;
}

[data-theme="dark"] .custom-dropdown {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border-color: var(--border-secondary);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-normal);
  margin: 2px;
  white-space: nowrap;
}

.dropdown-item:hover {
  background: var(--bg-secondary);
  color: var(--primary-600);
}

.dropdown-item .el-icon {
  font-size: 14px;
  color: var(--text-tertiary);
  transition: color var(--duration-fast) var(--ease-out);
}

.dropdown-item:hover .el-icon {
  color: var(--primary-600);
}

.dropdown-divider {
  height: 1px;
  background: var(--border-primary);
  margin: var(--space-1) var(--space-2);
}

.logout-item {
  color: var(--error);
}

.logout-item:hover {
  background: rgba(239, 68, 68, 0.1);
  color: var(--error);
}

.logout-item .el-icon {
  color: var(--error);
}

.logout-item:hover .el-icon {
  color: var(--error);
}

/* 创作引导区域 */
.create-inspiration {
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  color: white;
  padding: var(--space-8) 0;
  margin-bottom: var(--space-8);
}

.inspiration-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-6);
  max-width: 800px;
  margin: 0 auto;
}

.inspiration-text h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-2) 0;
}

.inspiration-text p {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
}

.inspiration-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-6);
  background: rgba(255, 255, 255, 0.2);
  color: white;
  text-decoration: none;
  border-radius: var(--radius-xl);
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  font-weight: var(--font-semibold);
  transition: all var(--duration-normal) var(--ease-out);
  flex-shrink: 0;
}

.inspiration-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.inspiration-btn .el-icon {
  font-size: var(--text-lg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .inspiration-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-4);
  }
  
  .inspiration-text h2 {
    font-size: var(--text-xl);
  }
  
  .inspiration-text p {
    font-size: var(--text-base);
  }
}
</style>

