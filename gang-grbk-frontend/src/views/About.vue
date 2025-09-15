<template>
  <div class="layout about-layout">
    <!-- 导航栏 -->
    <header class="navbar layout-header">
      <div class="navbar-container">
        <router-link to="/" class="navbar-brand gradient-text">Gang's Blog</router-link>

        <nav class="navbar-nav">
          <router-link to="/" exact-active-class="active">首页</router-link>
          <router-link to="/articles" exact-active-class="active">文章</router-link>
          <router-link to="/categories" exact-active-class="active">分类</router-link>
          <router-link to="/tags" exact-active-class="active">标签</router-link>
          <router-link to="/about" exact-active-class="active">关于</router-link>
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
      <div class="container py-8">
        <!-- 关于我 -->
        <div class="about-hero">
          <div class="hero-content">
            <div class="avatar-section">
              <img src="/api/placeholder/200/200" alt="Gang" class="author-avatar" />
              <div class="avatar-badge">
                <el-icon><User /></el-icon>
              </div>
            </div>
            <div class="intro-text">
              <h1 class="author-name">Gang</h1>
              <p class="author-title">全栈开发工程师 & 技术博客作者</p>
              <div class="social-links">
                <a href="#" class="social-link">
                  <el-icon><Link /></el-icon>
                  <span>GitHub</span>
                </a>
                <a href="#" class="social-link">
                  <el-icon><Message /></el-icon>
                  <span>邮箱联系</span>
                </a>
                <a href="#" class="social-link">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>微信</span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- 关于博客 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">关于这个博客</h2>
            <div class="section-decoration"></div>
          </div>
          <div class="content-grid">
            <div class="content-card">
              <div class="card-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="card-content">
                <h3>技术分享</h3>
                <p>专注于现代Web开发技术，分享Spring Boot、Vue.js、数据库设计等实用技术经验。</p>
              </div>
            </div>
            <div class="content-card">
              <div class="card-icon">
                <el-icon><Lightbulb /></el-icon>
              </div>
              <div class="card-content">
                <h3>思考记录</h3>
                <p>记录在开发过程中的思考、踩坑经历和解决方案，希望能帮助到遇到相似问题的朋友。</p>
              </div>
            </div>
            <div class="content-card">
              <div class="card-icon">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="card-content">
                <h3>持续学习</h3>
                <p>技术日新月异，保持学习的热情，与时俱进，分享最新的技术动态和最佳实践。</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 技术栈 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">技术栈</h2>
            <div class="section-decoration"></div>
          </div>
          <div class="tech-stack">
            <div class="tech-category">
              <h3>后端技术</h3>
              <div class="tech-list">
                <span class="tech-item">Java</span>
                <span class="tech-item">Spring Boot</span>
                <span class="tech-item">Spring Security</span>
                <span class="tech-item">MyBatis Plus</span>
                <span class="tech-item">MySQL</span>
                <span class="tech-item">Redis</span>
                <span class="tech-item">RabbitMQ</span>
              </div>
            </div>
            <div class="tech-category">
              <h3>前端技术</h3>
              <div class="tech-list">
                <span class="tech-item">Vue 3</span>
                <span class="tech-item">Element Plus</span>
                <span class="tech-item">Vite</span>
                <span class="tech-item">JavaScript</span>
                <span class="tech-item">CSS3</span>
                <span class="tech-item">Sass</span>
                <span class="tech-item">Axios</span>
              </div>
            </div>
            <div class="tech-category">
              <h3>开发工具</h3>
              <div class="tech-list">
                <span class="tech-item">IntelliJ IDEA</span>
                <span class="tech-item">VS Code</span>
                <span class="tech-item">Git</span>
                <span class="tech-item">Docker</span>
                <span class="tech-item">Postman</span>
                <span class="tech-item">Navicat</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">联系我</h2>
            <div class="section-decoration"></div>
          </div>
          <div class="contact-info">
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="contact-details">
                <h3>邮箱</h3>
                <p>gang@example.com</p>
                <small>工作合作 & 技术交流</small>
              </div>
            </div>
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><Link /></el-icon>
              </div>
              <div class="contact-details">
                <h3>GitHub</h3>
                <p>github.com/gang</p>
                <small>开源项目 & 代码分享</small>
              </div>
            </div>
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="contact-details">
                <h3>微信</h3>
                <p>gang_developer</p>
                <small>技术讨论 & 朋友圈</small>
              </div>
            </div>
          </div>
        </div>

        <!-- 博客统计 -->
        <div class="content-section">
          <div class="section-header">
            <h2 class="section-title">博客数据</h2>
            <div class="section-decoration"></div>
          </div>
          <div class="blog-stats">
            <div class="stat-item">
              <div class="stat-number">{{ stats.articleCount }}</div>
              <div class="stat-label">原创文章</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ formatNumber(stats.totalViews) }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.commentCount }}</div>
              <div class="stat-label">读者评论</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.daysSinceStart }}</div>
              <div class="stat-label">运行天数</div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="layout-footer">
      <div class="container">
        <div class="footer-content">
          <p>&copy; 2024 Gang's Blog. All rights reserved.</p>
          <div class="footer-links">
            <a href="/privacy">隐私政策</a>
            <a href="/terms">服务条款</a>
            <a href="/contact">联系我们</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Sunny, Moon, User, EditPen, Setting, Link, Message, ChatDotRound,
  Document, Lightbulb, Trophy, SwitchButton
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const isDark = ref(false)
const isLoggedIn = ref(false)
const userInfo = ref(null)

const stats = reactive({
  articleCount: 0,
  totalViews: 0,
  commentCount: 0,
  daysSinceStart: 0
})

// 计算属性
const isAdmin = computed(() => {
  return userInfo.value?.userType === 1
})

// 方法
const loadStats = async () => {
  try {
    const response = await axios.get('/v1/about/stats')
    if (response.data.code === 200) {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 设置默认值
    stats.articleCount = 25
    stats.totalViews = 12580
    stats.commentCount = 156
    stats.daysSinceStart = 365
  }
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num?.toString() || '0'
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

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

const handleLogout = async () => {
  try {
    const token = localStorage.getItem('auth_token')
    if (token) {
      await axios.post('/v1/auth/logout', {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
    }
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_info')
    localStorage.removeItem('remember_username')

    isLoggedIn.value = false
    userInfo.value = null

    ElMessage.success('退出登录成功')
    router.push('/')
  }
}

// 生命周期
onMounted(() => {
  checkLoginStatus()
  loadStats()

  // 初始化主题
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

  isDark.value = savedTheme === 'dark' || (!savedTheme && prefersDark)
  document.documentElement.classList.toggle('dark', isDark.value)
})
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

.about-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-main {
  flex: 1;
}

/* 关于我英雄区 */
.about-hero {
  background: linear-gradient(135deg, var(--primary-50), var(--success-50));
  border-radius: var(--radius-2xl);
  padding: var(--space-12) var(--space-8);
  margin-bottom: var(--space-12);
  text-align: center;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.avatar-section {
  position: relative;
  display: inline-block;
  margin-bottom: var(--space-6);
}

.author-avatar {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 6px solid white;
  box-shadow: var(--shadow-lg);
  object-fit: cover;
}

.avatar-badge {
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 50px;
  height: 50px;
  background: var(--primary-500);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: var(--text-xl);
  box-shadow: var(--shadow-md);
}

.author-name {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.author-title {
  font-size: var(--text-xl);
  color: var(--text-secondary);
  margin: 0 0 var(--space-6) 0;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: var(--space-4);
}

.social-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: white;
  color: var(--text-primary);
  text-decoration: none;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-fast) var(--ease-out);
}

.social-link:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  color: var(--primary-500);
}

/* 内容区域 */
.content-section {
  margin-bottom: var(--space-12);
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
}

.section-decoration {
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-500), var(--success-500));
  border-radius: var(--radius-full);
  margin: 0 auto;
}

/* 内容卡片 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-6);
}

.content-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-sm);
  text-align: center;
  transition: all var(--duration-normal) var(--ease-out);
}

.content-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.card-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-4) auto;
  color: white;
  font-size: var(--text-3xl);
}

.card-content h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.card-content p {
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

/* 技术栈 */
.tech-stack {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-6);
}

.tech-category {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-sm);
}

.tech-category h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
  text-align: center;
}

.tech-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
  justify-content: center;
}

.tech-item {
  background: var(--primary-100);
  color: var(--primary-700);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: 1px solid var(--primary-200);
  transition: all var(--duration-fast) var(--ease-out);
}

.tech-item:hover {
  background: var(--primary-500);
  color: white;
  transform: translateY(-1px);
}

/* 联系信息 */
.contact-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.contact-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  transition: all var(--duration-normal) var(--ease-out);
}

.contact-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.contact-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--success-500), var(--success-600));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: var(--text-xl);
  flex-shrink: 0;
}

.contact-details h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.contact-details p {
  font-size: var(--text-base);
  color: var(--primary-500);
  margin: 0 0 var(--space-1) 0;
  font-weight: var(--font-medium);
}

.contact-details small {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 博客统计 */
.blog-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-6);
}

.stat-item {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-8) var(--space-6);
  box-shadow: var(--shadow-sm);
  text-align: center;
  transition: all var(--duration-normal) var(--ease-out);
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-number {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--primary-500);
  margin-bottom: var(--space-2);
}

.stat-label {
  font-size: var(--text-base);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .about-hero {
    padding: var(--space-8) var(--space-4);
  }
  
  .author-avatar {
    width: 150px;
    height: 150px;
  }
  
  .author-name {
    font-size: var(--text-3xl);
  }
  
  .social-links {
    flex-direction: column;
    align-items: center;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .tech-stack {
    grid-template-columns: 1fr;
  }
  
  .contact-info {
    grid-template-columns: 1fr;
  }
  
  .blog-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .blog-stats {
    grid-template-columns: 1fr;
  }
  
  .contact-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
