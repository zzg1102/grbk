<template>
  <div class="layout article-list-layout">
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
        <!-- 页面标题区域 -->
        <div class="page-header">
          <div class="header-content">
            <div class="header-text">
              <h1 class="page-title">
                <span class="title-main">全部文章</span>
                <span class="title-accent">Articles</span>
              </h1>
              <div class="page-stats">
                <div class="stat-item">
                  <el-icon class="stat-icon"><Document /></el-icon>
                  <span class="stat-number">{{ articleStats.total }}</span>
                  <span class="stat-label">篇文章</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <el-icon class="stat-icon"><View /></el-icon>
                  <span class="stat-number">{{ articleStats.categories }}</span>
                  <span class="stat-label">个分类</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <el-icon class="stat-icon"><Star /></el-icon>
                  <span class="stat-number">{{ articleStats.tags }}</span>
                  <span class="stat-label">个标签</span>
                </div>
              </div>
            </div>
            
            <!-- 写文章按钮 - 只对登录用户显示 -->
            <div class="header-actions" v-if="isLoggedIn">
              <router-link to="/write" class="write-article-btn">
                <div class="btn-icon">
                  <el-icon><EditPen /></el-icon>
                </div>
                <div class="btn-text">
                  <span class="btn-title">写文章</span>
                  <span class="btn-subtitle">分享你的创作</span>
                </div>
              </router-link>
            </div>
          </div>
        </div>

        <!-- 搜索和筛选区域 -->
        <div class="filter-section">
          <div class="filter-container">
            <!-- 搜索框 -->
            <div class="search-wrapper">
              <div class="search-box">
                <div class="search-input-group">
                  <el-icon class="search-icon"><Search /></el-icon>
                  <input
                    v-model="searchQuery"
                    type="text"
                    placeholder="搜索文章标题、内容或标签..."
                    class="search-input"
                    @input="handleSearch"
                  />
                  <div v-if="searchQuery" class="search-clear" @click="clearSearch">
                    <el-icon><Close /></el-icon>
                  </div>
                </div>
              </div>
            </div>

            <!-- 筛选控件 -->
            <div class="filter-controls">
              <div class="filter-group">
                <div class="filter-item">
                  <label class="filter-label">分类</label>
                  <div class="custom-select" :class="{ active: selectedCategory }">
                    <select v-model="selectedCategory" @change="handleFilter">
                      <option value="">所有分类</option>
                      <option v-for="category in categories" :key="category.id" :value="category.id">
                        {{ category.name }} ({{ category.articleCount }})
                      </option>
                    </select>
                    <div class="select-icon">
                      <el-icon><ArrowDown /></el-icon>
                    </div>
                  </div>
                </div>

                <div class="filter-item">
                  <label class="filter-label">标签</label>
                  <div class="custom-select" :class="{ active: selectedTag }">
                    <select v-model="selectedTag" @change="handleFilter">
                      <option value="">所有标签</option>
                      <option v-for="tag in tags" :key="tag.id" :value="tag.id">
                        {{ tag.name }} ({{ tag.articleCount }})
                      </option>
                    </select>
                    <div class="select-icon">
                      <el-icon><ArrowDown /></el-icon>
                    </div>
                  </div>
                </div>

                <div class="filter-item">
                  <label class="filter-label">排序</label>
                  <div class="custom-select active">
                    <select v-model="sortOrder" @change="handleSort">
                      <option value="newest">最新发布</option>
                      <option value="oldest">最早发布</option>
                      <option value="views">浏览量</option>
                      <option value="likes">点赞数</option>
                    </select>
                    <div class="select-icon">
                      <el-icon><ArrowDown /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 筛选状态显示 -->
              <div v-if="hasActiveFilters" class="filter-status">
                <div class="active-filters">
                  <span v-if="selectedCategory" class="filter-tag" @click="clearCategory">
                    {{ getCategoryName(selectedCategory) }}
                    <el-icon><Close /></el-icon>
                  </span>
                  <span v-if="selectedTag" class="filter-tag" @click="clearTag">
                    {{ getTagName(selectedTag) }}
                    <el-icon><Close /></el-icon>
                  </span>
                  <span v-if="searchQuery" class="filter-tag" @click="clearSearch">
                    "{{ searchQuery }}"
                    <el-icon><Close /></el-icon>
                  </span>
                </div>
                <button class="clear-all-btn" @click="clearAllFilters">
                  <el-icon><Refresh /></el-icon>
                  清除所有筛选
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 文章列表 -->
        <div class="article-grid">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card hover-lift"
            @click="viewArticle(article.id)"
          >
            <!-- 文章封面 -->
            <div class="article-cover">
              <img
                :src="article.coverImage || '/api/placeholder/400/240'"
                :alt="article.title"
                loading="lazy"
              />
              <div class="article-overlay">
                <div class="article-category-badge">
                  {{ article.categoryName }}
                </div>
              </div>
            </div>

            <!-- 文章内容 -->
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary }}</p>

              <!-- 文章标签 -->
              <div class="article-tags">
                <span
                  v-for="tag in article.tags"
                  :key="tag.id"
                  class="tag"
                >
                  {{ tag.name }}
                </span>
              </div>

              <!-- 文章元信息 -->
              <div class="article-meta">
                <div class="article-author">
                  <el-icon><User /></el-icon>
                  <span>{{ article.authorName }}</span>
                </div>
                <div class="article-date">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(article.createTime) }}</span>
                </div>
                <div class="article-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ article.viewCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ article.likeCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ article.commentCount }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-section">
          <div class="loading-spinner">
            <el-icon class="rotating"><Loading /></el-icon>
          </div>
          <p>正在加载文章...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="articles.length === 0" class="empty-section">
          <div class="empty-icon">📝</div>
          <h3>暂无文章</h3>
          <p v-if="searchQuery || selectedCategory || selectedTag">
            没有找到匹配的文章，试试调整筛选条件吧
          </p>
          <p v-else>
            还没有发布任何文章，期待第一篇文章的诞生！
          </p>
          <button v-if="searchQuery || selectedCategory || selectedTag" class="btn btn-primary" @click="clearFilters">
            清除筛选条件
          </button>
        </div>

        <!-- 分页 -->
        <div v-if="pagination.total > pagination.size && !loading" class="pagination-section">
          <nav class="pagination">
            <button
              class="pagination-btn"
              :disabled="pagination.current === 1"
              @click="goToPage(pagination.current - 1)"
            >
              <el-icon><ArrowLeft /></el-icon>
              上一页
            </button>

            <div class="pagination-pages">
              <button
                v-for="page in visiblePages"
                :key="page"
                class="pagination-page"
                :class="{ active: page === pagination.current }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
            </div>

            <button
              class="pagination-btn"
              :disabled="pagination.current === pagination.totalPages"
              @click="goToPage(pagination.current + 1)"
            >
              下一页
              <el-icon><ArrowRight /></el-icon>
            </button>
          </nav>

          <div class="pagination-info">
            第 {{ pagination.current }} 页，共 {{ pagination.totalPages }} 页，{{ pagination.total }} 篇文章
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, Calendar, User, View, Star, ChatDotRound, Loading,
  ArrowLeft, ArrowRight, Sunny, Moon, EditPen, Setting, SwitchButton,
  Close, ArrowDown, Refresh, Document
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const categories = ref([])
const tags = ref([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')
const sortOrder = ref('newest')

// 登录状态管理
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 检查是否为管理员
const isAdmin = computed(() => {
  return userInfo.value?.userType === 1
})

// 检查是否有活动的筛选
const hasActiveFilters = computed(() => {
  return searchQuery.value || selectedCategory.value || selectedTag.value
})

// 分页信息
const pagination = reactive({
  current: 1,
  size: 12,
  total: 0,
  totalPages: 0
})

// 文章统计信息
const articleStats = reactive({
  total: 0,
  categories: 0,
  tags: 0
})

// 主题切换
const isDark = ref(false)

// 计算属性
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, pagination.current - 2)
  const end = Math.min(pagination.totalPages, pagination.current + 2)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// 方法
const loadArticles = async () => {
  loading.value = true

  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      title: searchQuery.value,
      categoryId: selectedCategory.value || null,
      tagId: selectedTag.value || null,
    }

    // 处理排序参数
    if (sortOrder.value) {
      switch (sortOrder.value) {
        case 'newest':
          params.sortField = 'create_time'
          params.sortOrder = 'desc'
          break
        case 'oldest':
          params.sortField = 'create_time'
          params.sortOrder = 'asc'
          break
        case 'views':
          params.sortField = 'view_count'
          params.sortOrder = 'desc'
          break
        case 'likes':
          params.sortField = 'like_count'
          params.sortOrder = 'desc'
          break
        default:
          params.sortField = 'create_time'
          params.sortOrder = 'desc'
      }
    }

    // 过滤掉空值参数
    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, value]) => value !== null && value !== '')
    )

    const response = await axios.get('/api/v1/articles', { params: filteredParams })

    if (response.data.code === 200) {
      const result = response.data.data
      articles.value = result.records || []
      pagination.total = result.total || 0
      pagination.totalPages = Math.ceil(pagination.total / pagination.size)
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await axios.get('/api/v1/categories')
    if (response.data.code === 200) {
      categories.value = response.data.data || []
      articleStats.categories = categories.value.length
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadTags = async () => {
  try {
    const response = await axios.get('/api/v1/tags')
    if (response.data.code === 200) {
      tags.value = response.data.data || []
      articleStats.tags = tags.value.length
    }
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadArticles()
}

const handleFilter = () => {
  pagination.current = 1
  loadArticles()
}

const handleSort = () => {
  pagination.current = 1
  loadArticles()
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedTag.value = ''
  sortOrder.value = 'newest'
  pagination.current = 1
  loadArticles()
}

const goToPage = (page) => {
  if (page >= 1 && page <= pagination.totalPages) {
    pagination.current = page
    loadArticles()
  }
}

const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 监听器
watch([searchQuery, selectedCategory, selectedTag, sortOrder], () => {
  articleStats.total = pagination.total
})

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

// 退出登录处理
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

// 筛选相关方法
const clearSearch = () => {
  searchQuery.value = ''
  handleSearch()
}

const clearCategory = () => {
  selectedCategory.value = ''
  handleFilter()
}

const clearTag = () => {
  selectedTag.value = ''
  handleFilter()
}

const clearAllFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedTag.value = ''
  handleFilter()
}

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id == categoryId)
  return category ? category.name : ''
}

const getTagName = (tagId) => {
  const tag = tags.value.find(t => t.id == tagId)
  return tag ? tag.name : ''
}

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

// 生命周期
// 处理URL查询参数
const initializeFromQuery = () => {
  const query = route.query
  console.log('文章列表页面接收到查询参数:', query)
  
  if (query.categoryId) {
    selectedCategory.value = query.categoryId
    console.log('设置分类筛选:', query.categoryId)
  }
  
  if (query.tagId) {
    selectedTag.value = query.tagId
  }
  
  if (query.search) {
    searchQuery.value = query.search
  }
  
  if (query.sort) {
    sortOrder.value = query.sort
  }
}

// 监听路由变化
watch(() => route.query, (newQuery) => {
  initializeFromQuery()
  loadArticles()
}, { deep: true })

onMounted(() => {
  // 首先检查登录状态
  checkLoginStatus()

  // 初始化查询参数
  initializeFromQuery()

  loadArticles()
  loadCategories()
  loadTags()

  // 检查主题偏好
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = prefersDark
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

.article-list-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-main {
  flex: 1;
}

/* 页面标题区域 */
.page-header {
  margin-bottom: var(--space-8);
  position: relative;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-8);
}

.header-text {
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: flex-start;
  flex-shrink: 0;
}

.write-article-btn {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  background: var(--bg-elevated);
  border: 2px solid var(--primary-200);
  color: var(--text-primary);
  text-decoration: none;
  border-radius: var(--radius-xl);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all var(--duration-normal) var(--ease-out);
  position: relative;
  overflow: hidden;
}

.write-article-btn:hover {
  background: var(--primary-50);
  border-color: var(--primary-300);
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.write-article-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.write-article-btn:hover::before {
  left: 100%;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--primary-100);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast) var(--ease-out);
  z-index: 1;
}

.write-article-btn:hover .btn-icon {
  background: var(--primary-200);
  transform: scale(1.05);
}

.btn-icon .el-icon {
  font-size: var(--text-lg);
  color: var(--primary-600);
}

.btn-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--space-1);
  z-index: 1;
}

.btn-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  line-height: 1;
}

.btn-subtitle {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
  line-height: 1;
}

.page-title {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: var(--space-6);
  line-height: 1;
}

.title-main {
  font-size: var(--text-5xl);
  font-weight: var(--font-black);
  color: var(--text-primary);
  letter-spacing: -0.025em;
  margin-bottom: var(--space-2);
}

.title-accent {
  font-size: var(--text-lg);
  font-weight: var(--font-medium);
  color: var(--primary-500);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  opacity: 0.8;
}

.page-stats {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.stat-icon {
  font-size: var(--text-base);
  color: var(--primary-500);
}

.stat-number {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
}

.stat-divider {
  width: 1px;
  height: 20px;
  background: var(--border-secondary);
}

/* 筛选区域 */
.filter-section {
  margin-bottom: var(--space-8);
}

.filter-container {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--border-secondary);
}

/* 搜索框样式 */
.search-wrapper {
  margin-bottom: var(--space-6);
}

.search-box {
  max-width: 500px;
  margin: 0 auto;
}

.search-input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  color: var(--text-tertiary);
  font-size: var(--text-lg);
  z-index: 2;
}

.search-input {
  width: 100%;
  padding: var(--space-4) var(--space-6);
  padding-left: calc(var(--space-4) + var(--text-lg) + var(--space-2));
  padding-right: calc(var(--space-4) + 30px);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-xl);
  background: var(--bg-primary);
  font-size: var(--text-base);
  color: var(--text-primary);
  transition: all var(--duration-fast) var(--ease-out);
  outline: none;
}

.search-input:focus {
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(var(--primary-500-rgb), 0.1);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.search-clear {
  position: absolute;
  right: var(--space-3);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--text-tertiary);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: var(--text-sm);
  transition: all var(--duration-fast) var(--ease-out);
}

.search-clear:hover {
  background: var(--text-secondary);
  transform: scale(1.1);
}

/* 筛选控件样式 */
.filter-controls {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  justify-content: center;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  min-width: 140px;
}

.filter-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  text-align: center;
}

.custom-select {
  position: relative;
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast) var(--ease-out);
}

.custom-select.active {
  border-color: var(--primary-300);
  background: var(--primary-50);
}

.custom-select:hover {
  border-color: var(--primary-200);
}

.custom-select select {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  padding-right: calc(var(--space-4) + 20px);
  border: none;
  background: transparent;
  font-size: var(--text-sm);
  color: var(--text-primary);
  cursor: pointer;
  outline: none;
  appearance: none;
}

.select-icon {
  position: absolute;
  right: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  pointer-events: none;
  font-size: var(--text-sm);
  transition: transform var(--duration-fast) var(--ease-out);
}

.custom-select:hover .select-icon {
  color: var(--primary-500);
}

/* 筛选状态显示 */
.filter-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-3);
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-secondary);
}

.active-filters {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  background: var(--primary-100);
  color: var(--primary-700);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.filter-tag:hover {
  background: var(--primary-200);
  transform: scale(1.05);
}

.filter-tag .el-icon {
  font-size: var(--text-xs);
}

.clear-all-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: transparent;
  color: var(--text-tertiary);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.clear-all-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border-color: var(--border-primary);
}

/* 文章网格 */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.article-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-out);
}

.article-card:hover {
  box-shadow: var(--shadow-lg);
}

/* 文章封面 */
.article-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-normal) var(--ease-out);
}

.article-card:hover .article-cover img {
  transform: scale(1.05);
}

.article-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), transparent);
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  padding: var(--space-4);
}

.article-category-badge {
  background: var(--primary-500);
  color: white;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

/* 文章内容 */
.article-content {
  padding: var(--space-5);
}

.article-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 var(--space-4) 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.article-author,
.article-date {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.article-stats {
  display: flex;
  gap: var(--space-3);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* 加载状态 */
.loading-section {
  text-align: center;
  padding: var(--space-12) 0;
}

.loading-spinner {
  font-size: var(--text-4xl);
  color: var(--primary-500);
  margin-bottom: var(--space-4);
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: var(--space-12) 0;
}

.empty-icon {
  font-size: var(--text-6xl);
  margin-bottom: var(--space-4);
}

.empty-section h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-section p {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin: 0 0 var(--space-6) 0;
}

/* 分页 */
.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.pagination {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-4);
  background: var(--bg-elevated);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.pagination-btn:hover:not(:disabled) {
  background: var(--primary-50);
  border-color: var(--primary-500);
  color: var(--primary-500);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-pages {
  display: flex;
  gap: var(--space-1);
  margin: 0 var(--space-4);
}

.pagination-page {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-elevated);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.pagination-page:hover,
.pagination-page.active {
  background: var(--primary-500);
  border-color: var(--primary-500);
  color: white;
}

.pagination-info {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-container {
    padding: var(--space-4);
  }

  .search-wrapper {
    margin-bottom: var(--space-4);
  }

  .filter-group {
    flex-direction: column;
    align-items: center;
  }

  .filter-item {
    width: 100%;
    max-width: 300px;
  }

  .filter-status {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-3);
  }

  .active-filters {
    justify-content: center;
  }

  .clear-all-btn {
    align-self: center;
  }

  .article-grid {
    grid-template-columns: 1fr;
  }

  .article-meta {
    flex-direction: column;
    gap: var(--space-2);
    align-items: flex-start;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .header-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: var(--space-6);
  }

  .title-main {
    font-size: var(--text-4xl);
  }

  .page-stats {
    justify-content: center;
  }

  .stat-divider {
    display: none;
  }

  .write-article-btn {
    width: 100%;
    justify-content: center;
  }

  .article-card {
    margin: 0 calc(-1 * var(--space-4));
    border-radius: 0;
  }

  .pagination-pages {
    margin: 0 var(--space-2);
  }

  .pagination-btn {
    padding: var(--space-2) var(--space-3);
  }
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
</style>