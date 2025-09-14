<template>
  <div class="layout tags-layout">
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
          <h1 class="page-title">文章标签</h1>
          <p class="page-description">{{ tagStats.total }} 个标签 • {{ tagStats.articles }} 篇文章</p>
        </div>

        <!-- 搜索和筛选区域 -->
        <div class="filter-section">
          <div class="search-box">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索标签..."
              class="input"
              @input="handleSearch"
            />
          </div>

          <div class="sort-controls">
            <select v-model="sortBy" class="select" @change="handleSort">
              <option value="name">按名称排序</option>
              <option value="articleCount">按文章数排序</option>
              <option value="createTime">按创建时间排序</option>
            </select>
          </div>
        </div>

        <!-- 热门标签云 -->
        <div class="tag-cloud-section" v-if="hotTags.length > 0">
          <h2 class="section-title">热门标签</h2>
          <div class="tag-cloud">
            <button
              v-for="tag in hotTags"
              :key="tag.id"
              class="cloud-tag"
              :class="getTagSizeClass(tag.articleCount)"
              @click="viewTag(tag.id)"
            >
              {{ tag.name }}
              <span class="tag-count">({{ tag.articleCount }})</span>
            </button>
          </div>
        </div>

        <!-- 标签列表 -->
        <div class="tags-section">
          <div class="tags-header">
            <h2 class="section-title">全部标签</h2>
            <div class="view-toggle">
              <button
                class="toggle-btn"
                :class="{ active: viewMode === 'grid' }"
                @click="viewMode = 'grid'"
              >
                <el-icon><Grid /></el-icon>
                网格
              </button>
              <button
                class="toggle-btn"
                :class="{ active: viewMode === 'list' }"
                @click="viewMode = 'list'"
              >
                <el-icon><List /></el-icon>
                列表
              </button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-section">
            <div class="loading-spinner">
              <el-icon class="rotating"><Loading /></el-icon>
            </div>
            <p>正在加载标签...</p>
          </div>

          <!-- 网格视图 -->
          <div v-else-if="filteredTags.length > 0 && viewMode === 'grid'" class="tags-grid">
            <div
              v-for="tag in paginatedTags"
              :key="tag.id"
              class="tag-card hover-lift"
              @click="viewTag(tag.id)"
            >
              <div class="tag-card-header">
                <div class="tag-icon">{{ getTagIcon(tag.name) }}</div>
                <h3 class="tag-name">{{ tag.name }}</h3>
              </div>

              <div class="tag-card-body">
                <p class="tag-description">{{ tag.description || '暂无描述' }}</p>

                <div class="tag-stats">
                  <div class="stat-item">
                    <el-icon><Document /></el-icon>
                    <span>{{ tag.articleCount || 0 }} 篇文章</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><Calendar /></el-icon>
                    <span>{{ formatDate(tag.createTime) }}</span>
                  </div>
                </div>
              </div>

              <div class="tag-card-footer">
                <div class="tag-popularity">
                  <div class="popularity-bar">
                    <div
                      class="popularity-fill"
                      :style="{ width: getPopularityPercentage(tag.articleCount) + '%' }"
                    ></div>
                  </div>
                  <span class="popularity-text">热度 {{ getPopularityLevel(tag.articleCount) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 列表视图 -->
          <div v-else-if="filteredTags.length > 0 && viewMode === 'list'" class="tags-list">
            <div
              v-for="tag in paginatedTags"
              :key="tag.id"
              class="tag-list-item"
              @click="viewTag(tag.id)"
            >
              <div class="tag-list-icon">{{ getTagIcon(tag.name) }}</div>
              <div class="tag-list-content">
                <h4 class="tag-list-name">{{ tag.name }}</h4>
                <p class="tag-list-description">{{ tag.description || '暂无描述' }}</p>
              </div>
              <div class="tag-list-meta">
                <div class="tag-list-stats">
                  <span class="article-count">{{ tag.articleCount || 0 }} 篇文章</span>
                  <span class="create-date">{{ formatDate(tag.createTime) }}</span>
                </div>
                <button class="btn btn-ghost btn-sm">
                  查看文章
                  <el-icon><ArrowRight /></el-icon>
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="!loading" class="empty-section">
            <div class="empty-icon">🏷️</div>
            <h3>{{ searchQuery ? '未找到匹配的标签' : '暂无标签' }}</h3>
            <p>{{ searchQuery ? '试试调整搜索关键词' : '还没有创建任何标签' }}</p>
            <button v-if="searchQuery" class="btn btn-primary" @click="clearSearch">
              清除搜索
            </button>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="totalPages > 1 && !loading" class="pagination-section">
          <nav class="pagination">
            <button
              class="pagination-btn"
              :disabled="currentPage === 1"
              @click="goToPage(currentPage - 1)"
            >
              <el-icon><ArrowLeft /></el-icon>
              上一页
            </button>

            <div class="pagination-pages">
              <button
                v-for="page in visiblePages"
                :key="page"
                class="pagination-page"
                :class="{ active: page === currentPage }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
            </div>

            <button
              class="pagination-btn"
              :disabled="currentPage === totalPages"
              @click="goToPage(currentPage + 1)"
            >
              下一页
              <el-icon><ArrowRight /></el-icon>
            </button>
          </nav>

          <div class="pagination-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页，{{ filteredTags.length }} 个标签
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
  Search, Loading, Document, Calendar, Grid, List,
  ArrowLeft, ArrowRight, Sunny, Moon
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const tags = ref([])
const hotTags = ref([])
const searchQuery = ref('')
const sortBy = ref('name')
const viewMode = ref('grid')
const currentPage = ref(1)
const pageSize = ref(12)
const isDark = ref(false)

const tagStats = reactive({
  total: 0,
  articles: 0
})

// 计算属性
const filteredTags = computed(() => {
  let filtered = tags.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(tag =>
      tag.name.toLowerCase().includes(query) ||
      (tag.description && tag.description.toLowerCase().includes(query))
    )
  }

  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'articleCount':
        return (b.articleCount || 0) - (a.articleCount || 0)
      case 'createTime':
        return new Date(b.createTime) - new Date(a.createTime)
      case 'name':
      default:
        return a.name.localeCompare(b.name)
    }
  })

  return filtered
})

const totalPages = computed(() => {
  return Math.ceil(filteredTags.value.length / pageSize.value)
})

const paginatedTags = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredTags.value.slice(start, end)
})

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const maxArticleCount = computed(() => {
  return Math.max(...tags.value.map(tag => tag.articleCount || 0))
})

// 方法
const loadTags = async () => {
  loading.value = true

  try {
    const response = await axios.get('/api/v1/tags')

    if (response.data.code === 200) {
      tags.value = response.data.data || []

      // 计算统计数据
      tagStats.total = tags.value.length
      tagStats.articles = tags.value.reduce((sum, tag) => sum + (tag.articleCount || 0), 0)

      // 获取热门标签（文章数量前10）
      hotTags.value = [...tags.value]
        .sort((a, b) => (b.articleCount || 0) - (a.articleCount || 0))
        .slice(0, 10)
    }
  } catch (error) {
    console.error('加载标签失败:', error)
    ElMessage.error('加载标签失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const clearSearch = () => {
  searchQuery.value = ''
  currentPage.value = 1
}

const viewTag = (tagId) => {
  router.push({
    path: '/articles',
    query: { tagId }
  })
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const getTagIcon = (tagName) => {
  const iconMap = {
    'JavaScript': '🟨',
    'Vue': '💚',
    'React': '⚛️',
    'Node.js': '🟢',
    'Python': '🐍',
    'Java': '☕',
    'HTML': '🌐',
    'CSS': '🎨',
    'TypeScript': '🔷',
    'PHP': '🐘',
    'MySQL': '🗄️',
    'Redis': '🔴',
    'Docker': '🐳',
    'Git': '📝',
    'Linux': '🐧',
    '前端': '🖥️',
    '后端': '⚙️',
    '移动端': '📱',
    '设计': '🎨',
    '算法': '🧮',
    '数据结构': '📊',
    '架构': '🏗️',
    '性能优化': '⚡',
    '测试': '🧪',
    '部署': '🚀'
  }

  return iconMap[tagName] || '🏷️'
}

const getTagSizeClass = (articleCount) => {
  const count = articleCount || 0
  const max = Math.max(...hotTags.value.map(tag => tag.articleCount || 0))

  if (count >= max * 0.8) return 'size-xl'
  if (count >= max * 0.6) return 'size-lg'
  if (count >= max * 0.4) return 'size-md'
  return 'size-sm'
}

const getPopularityPercentage = (articleCount) => {
  if (maxArticleCount.value === 0) return 0
  return Math.min(100, ((articleCount || 0) / maxArticleCount.value) * 100)
}

const getPopularityLevel = (articleCount) => {
  const percentage = getPopularityPercentage(articleCount)
  if (percentage >= 80) return '🔥'
  if (percentage >= 60) return '⭐'
  if (percentage >= 40) return '👍'
  return '💡'
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short'
  })
}

// 监听器
watch([searchQuery, sortBy], () => {
  currentPage.value = 1
})

// 生命周期
onMounted(() => {
  loadTags()

  // 检查主题偏好
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = prefersDark
  document.documentElement.classList.toggle('dark', isDark.value)
})
</script>

<style scoped>
.tags-layout {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
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

.sort-controls {
  flex-shrink: 0;
}

/* 标签云区域 */
.tag-cloud-section {
  margin-bottom: var(--space-10);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-6);
  text-align: center;
}

.tag-cloud {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--space-3);
}

.cloud-tag {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-4);
  background: var(--primary-50);
  color: var(--primary-700);
  border: 1px solid var(--primary-200);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
  font-weight: var(--font-medium);
}

.cloud-tag:hover {
  background: var(--primary-500);
  color: white;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.cloud-tag.size-sm { font-size: var(--text-sm); }
.cloud-tag.size-md { font-size: var(--text-base); }
.cloud-tag.size-lg { font-size: var(--text-lg); }
.cloud-tag.size-xl { font-size: var(--text-xl); }

.tag-count {
  font-size: 0.8em;
  opacity: 0.8;
}

/* 标签区域 */
.tags-section {
  margin-bottom: var(--space-8);
}

.tags-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.view-toggle {
  display: flex;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.toggle-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  background: var(--bg-primary);
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
  font-size: var(--text-sm);
}

.toggle-btn:hover,
.toggle-btn.active {
  background: var(--primary-500);
  color: white;
}

.toggle-btn + .toggle-btn {
  border-left: 1px solid var(--border-primary);
}

/* 网格视图 */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-5);
}

.tag-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-out);
}

.tag-card:hover {
  box-shadow: var(--shadow-lg);
}

.tag-card-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.tag-icon {
  font-size: var(--text-2xl);
  width: 50px;
  height: 50px;
  border-radius: var(--radius-lg);
  background: var(--primary-50);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tag-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.tag-card-body {
  margin-bottom: var(--space-4);
}

.tag-description {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin: 0 0 var(--space-3) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tag-stats {
  display: flex;
  justify-content: space-between;
  gap: var(--space-3);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.tag-card-footer {
  border-top: 1px solid var(--border-secondary);
  padding-top: var(--space-3);
}

.tag-popularity {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.popularity-bar {
  flex: 1;
  height: 4px;
  background: var(--gray-200);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.popularity-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--success-500), var(--primary-500));
  transition: width var(--duration-normal) var(--ease-out);
}

.popularity-text {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  white-space: nowrap;
}

/* 列表视图 */
.tags-list {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.tag-list-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-secondary);
  cursor: pointer;
  transition: background-color var(--duration-fast) var(--ease-out);
}

.tag-list-item:hover {
  background: var(--bg-secondary);
}

.tag-list-item:last-child {
  border-bottom: none;
}

.tag-list-icon {
  font-size: var(--text-xl);
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  background: var(--primary-50);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.tag-list-content {
  flex: 1;
  min-width: 0;
}

.tag-list-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.tag-list-description {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tag-list-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-2);
}

.tag-list-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.article-count {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

/* 加载和空状态 */
.loading-section,
.empty-section {
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
  color: var(--text-secondary);
  margin: 0 0 var(--space-6) 0;
}

/* 分页 */
.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-8);
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
  .filter-section {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-3);
  }

  .search-box {
    max-width: none;
  }

  .tags-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-3);
  }

  .view-toggle {
    align-self: center;
  }

  .tags-grid {
    grid-template-columns: 1fr;
  }

  .tag-list-meta {
    align-items: flex-start;
  }

  .tag-list-stats {
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

  .tag-cloud {
    padding: var(--space-4);
    gap: var(--space-2);
  }

  .cloud-tag {
    padding: var(--space-1) var(--space-3);
  }

  .tag-card,
  .tag-list-item {
    margin: 0 calc(-1 * var(--space-2));
    border-radius: 0;
  }

  .tags-list {
    margin: 0 calc(-1 * var(--space-2));
    border-radius: 0;
  }

  .tag-list-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }

  .tag-list-meta {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .pagination-pages {
    margin: 0 var(--space-2);
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>