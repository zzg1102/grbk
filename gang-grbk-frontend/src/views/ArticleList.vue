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
          <router-link to="/auth/login" class="btn btn-ghost btn-sm">
            登录
          </router-link>
          <router-link to="/auth/register" class="btn btn-primary btn-sm">
            注册
          </router-link>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="layout-main">
      <div class="container py-8">
        <!-- 页面标题区域 -->
        <div class="page-header">
          <h1 class="page-title">全部文章</h1>
          <p class="page-description">{{ articleStats.total }} 篇文章 • {{ articleStats.categories }} 个分类 • {{ articleStats.tags }} 个标签</p>
        </div>

        <!-- 搜索和筛选区域 -->
        <div class="filter-section">
          <div class="filter-toolbar">
            <div class="search-box">
              <el-icon class="search-icon"><Search /></el-icon>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索文章标题、内容或标签..."
                class="input"
                @input="handleSearch"
              />
            </div>

            <div class="filter-controls">
              <select v-model="selectedCategory" class="select" @change="handleFilter">
                <option value="">所有分类</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }} ({{ category.articleCount }})
                </option>
              </select>

              <select v-model="selectedTag" class="select" @change="handleFilter">
                <option value="">所有标签</option>
                <option v-for="tag in tags" :key="tag.id" :value="tag.id">
                  {{ tag.name }} ({{ tag.articleCount }})
                </option>
              </select>

              <select v-model="sortOrder" class="select" @change="handleSort">
                <option value="newest">最新发布</option>
                <option value="oldest">最早发布</option>
                <option value="views">浏览量</option>
                <option value="likes">点赞数</option>
              </select>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, Calendar, User, View, Star, ChatDotRound, Loading,
  ArrowLeft, ArrowRight, Sunny, Moon
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const categories = ref([])
const tags = ref([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')
const sortOrder = ref('newest')

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

// 生命周期
onMounted(() => {
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
  text-align: center;
  margin-bottom: var(--space-8);
}

.page-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.page-description {
  font-size: var(--text-lg);
  color: var(--text-secondary);
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  margin-bottom: var(--space-8);
}

.filter-toolbar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.search-box {
  position: relative;
  max-width: 400px;
  margin: 0 auto;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
}

.search-box .input {
  padding-left: calc(var(--space-3) + 20px);
}

.filter-controls {
  display: flex;
  justify-content: center;
  gap: var(--space-3);
  flex-wrap: wrap;
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
  .filter-toolbar {
    align-items: center;
  }

  .filter-controls {
    flex-direction: column;
    width: 100%;
    max-width: 400px;
  }

  .filter-controls .select {
    width: 100%;
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
  .page-title {
    font-size: var(--text-3xl);
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
</style>