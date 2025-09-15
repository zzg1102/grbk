<template>
  <div class="category-detail-layout">
    <!-- 简约导航栏 -->
    <header class="category-header">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/categories" class="back-link">
            <el-icon><ArrowLeft /></el-icon>
            <span>返回分类</span>
          </router-link>
        </div>

        <div class="header-actions">
          <button class="action-btn" @click="toggleTheme" title="切换主题">
            <el-icon><Sunny v-if="isDark" /><Moon v-else /></el-icon>
          </button>

          <!-- 未登录状态 -->
          <template v-if="!isLoggedIn">
            <router-link to="/auth/login" class="action-btn">
              登录
            </router-link>
          </template>

          <!-- 已登录状态 -->
          <template v-else>
            <div class="user-menu">
              <el-dropdown @command="handleUserAction">
                <div class="user-avatar">
                  <el-icon><User /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                    <el-dropdown-item v-if="isAdmin" command="admin">管理后台</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="category-main">
      <div class="category-container">
        <!-- 分类信息头部 -->
        <header class="category-hero" v-if="category">
          <div class="category-icon">
            {{ getCategoryIcon(category.name) }}
          </div>
          <div class="category-info">
            <h1 class="category-title">{{ category.name }}</h1>
            <p class="category-description" v-if="category.description">
              {{ category.description }}
            </p>
            <div class="category-stats">
              <div class="stat-item">
                <el-icon><Document /></el-icon>
                <span>{{ category.articleCount || 0 }} 篇文章</span>
              </div>
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ formatNumber(totalViews) }} 总浏览</span>
              </div>
            </div>
          </div>
        </header>

        <!-- 搜索和筛选 -->
        <div class="search-section">
          <div class="search-container">
            <div class="search-box">
              <el-input
                v-model="searchQuery"
                placeholder="在该分类中搜索文章..."
                size="large"
                clearable
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
            
            <div class="filter-controls">
              <el-select v-model="sortOrder" placeholder="排序方式" @change="handleSort">
                <el-option label="最新发布" value="newest" />
                <el-option label="最多浏览" value="views" />
                <el-option label="最多点赞" value="likes" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- 文章列表 -->
        <div class="articles-section">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-section">
            <el-icon class="rotating"><Loading /></el-icon>
            <span>正在加载文章...</span>
          </div>

          <!-- 文章网格 -->
          <div v-else-if="articles.length > 0" class="articles-grid">
            <article
              v-for="article in articles"
              :key="article.id"
              class="article-card"
              @click="viewArticle(article.id)"
            >
              <!-- 文章封面 -->
              <div class="article-cover" v-if="article.coverImage">
                <img :src="article.coverImage" :alt="article.title" />
              </div>

              <div class="article-content">
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-summary" v-if="article.summary">{{ article.summary }}</p>

                <div class="article-meta">
                  <div class="meta-left">
                    <span class="author">{{ article.authorName || '匿名用户' }}</span>
                    <span class="date">{{ formatDate(article.createTime) }}</span>
                  </div>
                  <div class="meta-right">
                    <span class="stat-item">
                      <el-icon><View /></el-icon>
                      {{ article.viewCount || 0 }}
                    </span>
                    <span class="stat-item">
                      <el-icon><Star /></el-icon>
                      {{ article.likeCount || 0 }}
                    </span>
                  </div>
                </div>

                <!-- 文章标签 -->
                <div class="article-tags" v-if="article.tags && article.tags.length">
                  <span
                    v-for="tag in article.tags.slice(0, 3)"
                    :key="tag.id"
                    class="tag-item"
                  >
                    {{ tag.name }}
                  </span>
                </div>
              </div>
            </article>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-section">
            <div class="empty-icon">{{ searchQuery ? '🔍' : '📝' }}</div>
            <h3 v-if="searchQuery">没有找到相关文章</h3>
            <h3 v-else>{{ category.name }} 分类等你来填充</h3>
            <p v-if="searchQuery">
              没有找到包含 "{{ searchQuery }}" 的文章，试试其他关键词或清除搜索查看所有文章
            </p>
            <p v-else>
              这是一个全新的分类，正等待着精彩内容的填充。成为第一个在这里分享想法的人吧！
            </p>
            <div class="empty-actions">
              <button v-if="searchQuery" class="btn btn-secondary" @click="clearSearch">
                <el-icon><Refresh /></el-icon>
                清除搜索
              </button>
              <router-link v-if="isLoggedIn" :to="`/write?categoryId=${route.params.id}`" class="btn btn-primary">
                <el-icon><EditPen /></el-icon>
                写第一篇文章
              </router-link>
              <router-link v-else to="/auth/login" class="btn btn-primary">
                <el-icon><User /></el-icon>
                登录后开始创作
              </router-link>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-section" v-if="articles.length > 0 && pagination.totalPages > 1">
          <div class="pagination-info">
            显示第 {{ (pagination.current - 1) * pagination.size + 1 }} - 
            {{ Math.min(pagination.current * pagination.size, pagination.total) }} 条，
            共 {{ pagination.total }} 条记录
          </div>
          <div class="pagination-controls">
            <button 
              class="btn btn-ghost" 
              :disabled="pagination.current <= 1"
              @click="goToPage(pagination.current - 1)"
            >
              <el-icon><ArrowLeft /></el-icon>
              上一页
            </button>
            
            <div class="page-numbers">
              <button
                v-for="page in getPageNumbers()"
                :key="page"
                class="btn"
                :class="{ 'btn-primary': page === pagination.current, 'btn-ghost': page !== pagination.current }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
            </div>
            
            <button 
              class="btn btn-ghost" 
              :disabled="pagination.current >= pagination.totalPages"
              @click="goToPage(pagination.current + 1)"
            >
              下一页
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Sunny, Moon, User, Document, View, Search, Loading,
  Star, ArrowRight, Refresh, EditPen
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const isDark = ref(false)
const isLoggedIn = ref(false)
const userInfo = ref(null)
const category = ref({})
const articles = ref([])
const searchQuery = ref('')
const sortOrder = ref('newest')
const totalViews = ref(0)

// 分页数据
const pagination = reactive({
  current: 1,
  size: 12,
  total: 0,
  totalPages: 0
})

// 计算属性
const isAdmin = computed(() => {
  return userInfo.value?.userType === 1
})

// 方法
const loadCategory = async () => {
  try {
    const response = await axios.get(`/api/v1/categories/${route.params.id}`)
    if (response.data.code === 200) {
      category.value = response.data.data
    }
  } catch (error) {
    console.error('加载分类信息失败:', error)
    ElMessage.error('加载分类信息失败')
  }
}

const loadArticles = async () => {
  loading.value = true
  
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      categoryId: route.params.id,
      title: searchQuery.value || null,
      sortField: sortOrder.value === 'views' ? 'view_count' : 
                 sortOrder.value === 'likes' ? 'like_count' : 'create_time',
      sortOrder: sortOrder.value === 'newest' ? 'desc' : 'desc'
    }

    // 过滤空值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined || params[key] === '') {
        delete params[key]
      }
    })

    const response = await axios.get('/api/v1/articles', { params })
    
    if (response.data.code === 200) {
      const result = response.data.data
      articles.value = result.records || []
      pagination.total = result.total || 0
      pagination.totalPages = Math.ceil(pagination.total / pagination.size)
      
      // 计算总浏览量
      totalViews.value = articles.value.reduce((sum, article) => sum + (article.viewCount || 0), 0)
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadArticles()
}

const handleSort = () => {
  pagination.current = 1
  loadArticles()
}

const clearSearch = () => {
  searchQuery.value = ''
  handleSearch()
}

const goToPage = (page) => {
  if (page >= 1 && page <= pagination.totalPages) {
    pagination.current = page
    loadArticles()
  }
}

const getPageNumbers = () => {
  const pages = []
  const total = pagination.totalPages
  const current = pagination.current
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }
  
  return pages
}

const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '技术': '💻',
    '生活': '🏠',
    '旅行': '✈️',
    '摄影': '📸',
    '读书': '📚',
    '音乐': '🎵',
    '电影': '🎬',
    '美食': '🍽️',
    '运动': '⚽',
    '游戏': '🎮'
  }
  return iconMap[categoryName] || '📂'
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num?.toString() || '0'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
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
    }
  }
}

const handleUserAction = (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人资料页面
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
      await axios.post('/api/v1/auth/logout')
    }
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_info')
    isLoggedIn.value = false
    userInfo.value = null
    ElMessage.success('退出登录成功')
    router.push('/')
  }
}

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadCategory()
    loadArticles()
  }
})

// 增加分类浏览量
const incrementCategoryView = async () => {
  try {
    await axios.post(`/api/v1/categories/${route.params.id}/view`)
  } catch (error) {
    console.error('增加分类浏览量失败:', error)
  }
}

// 生命周期
onMounted(() => {
  checkLoginStatus()
  
  // 初始化主题
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = savedTheme === 'dark' || (!savedTheme && prefersDark)
  document.documentElement.classList.toggle('dark', isDark.value)
  
  loadCategory()
  loadArticles()
  
  // 增加分类浏览量
  incrementCategoryView()
})
</script>

<style scoped>
.category-detail-layout {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
}

/* 简约导航栏 */
.category-header {
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-secondary);
  padding: var(--space-4) var(--space-6);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-secondary);
  text-decoration: none;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  transition: all var(--duration-fast) var(--ease-out);
}

.back-link:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.action-btn {
  display: flex;
  align-items: center;
  padding: var(--space-2) var(--space-3);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.action-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary-100);
  color: var(--primary-600);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* 主要内容 */
.category-main {
  flex: 1;
  overflow: auto;
}

.category-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-6);
}

/* 分类信息头部 */
.category-hero {
  display: flex;
  align-items: center;
  gap: var(--space-6);
  margin-bottom: var(--space-8);
  padding: var(--space-8);
  background: linear-gradient(135deg, var(--primary-50), var(--success-50));
  border-radius: var(--radius-2xl);
}

.category-icon {
  font-size: 4rem;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
  box-shadow: var(--shadow-lg);
}

.category-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.category-description {
  font-size: var(--text-lg);
  color: var(--text-secondary);
  margin: 0 0 var(--space-4) 0;
  line-height: 1.6;
}

.category-stats {
  display: flex;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* 搜索区域 */
.search-section {
  margin-bottom: var(--space-8);
}

.search-container {
  display: flex;
  gap: var(--space-4);
  align-items: center;
  padding: var(--space-4);
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.search-box {
  flex: 1;
}

.filter-controls {
  display: flex;
  gap: var(--space-3);
}

/* 文章列表 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-12) 0;
  color: var(--text-secondary);
}

.articles-grid {
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
  transition: all var(--duration-normal) var(--ease-out);
  cursor: pointer;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.article-cover {
  width: 100%;
  height: 200px;
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

.article-content {
  padding: var(--space-5);
}

.article-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.article-summary {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-4) 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.6;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.meta-left {
  display: flex;
  gap: var(--space-2);
}

.meta-right {
  display: flex;
  gap: var(--space-3);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.article-tags {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
}

.tag-item {
  background: var(--primary-100);
  color: var(--primary-700);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: var(--space-12) 0;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--space-4);
}

.empty-section h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.empty-section p {
  font-size: var(--text-lg);
  margin: 0 0 var(--space-6) 0;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.empty-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: center;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6) 0;
  border-top: 1px solid var(--border-secondary);
}

.pagination-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.page-numbers {
  display: flex;
  gap: var(--space-1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-container {
    padding: var(--space-6) var(--space-4);
  }
  
  .category-hero {
    flex-direction: column;
    text-align: center;
    gap: var(--space-4);
  }
  
  .category-icon {
    width: 80px;
    height: 80px;
    font-size: 3rem;
  }
  
  .category-title {
    font-size: var(--text-3xl);
  }
  
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .pagination-section {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
