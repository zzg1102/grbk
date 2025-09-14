<template>
  <div class="layout category-layout">
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
          <h1 class="page-title">文章分类</h1>
          <p class="page-description">{{ categoryStats.total }} 个分类 • {{ categoryStats.articles }} 篇文章</p>
        </div>

        <!-- 分类统计卡片 -->
        <div class="stats-section">
          <div class="stat-card">
            <div class="stat-icon">📚</div>
            <div class="stat-content">
              <div class="stat-number">{{ categoryStats.total }}</div>
              <div class="stat-label">分类总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-content">
              <div class="stat-number">{{ categoryStats.articles }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">👀</div>
            <div class="stat-content">
              <div class="stat-number">{{ formatNumber(categoryStats.views) }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🔥</div>
            <div class="stat-content">
              <div class="stat-number">{{ topCategory?.name || '-' }}</div>
              <div class="stat-label">热门分类</div>
            </div>
          </div>
        </div>

        <!-- 分类网格 -->
        <div class="categories-section">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-section">
            <div class="loading-spinner">
              <el-icon class="rotating"><Loading /></el-icon>
            </div>
            <p>正在加载分类...</p>
          </div>

          <!-- 分类列表 -->
          <div v-else-if="categories.length > 0" class="category-grid">
            <div
              v-for="category in categories"
              :key="category.id"
              class="category-card hover-lift"
              @click="viewCategory(category.id)"
            >
              <div class="category-icon">
                {{ getCategoryIcon(category.name) }}
              </div>
              <div class="category-content">
                <h3 class="category-name">{{ category.name }}</h3>
                <p class="category-description">{{ category.description || '暂无描述' }}</p>

                <div class="category-stats">
                  <div class="stat-item">
                    <el-icon><Document /></el-icon>
                    <span>{{ category.articleCount || 0 }} 篇文章</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><View /></el-icon>
                    <span>{{ formatNumber(category.viewCount || 0) }} 浏览</span>
                  </div>
                </div>

                <div class="category-tags" v-if="category.hotTags && category.hotTags.length">
                  <span class="hot-tag-label">热门标签：</span>
                  <span
                    v-for="tag in category.hotTags.slice(0, 3)"
                    :key="tag.id"
                    class="hot-tag"
                  >
                    {{ tag.name }}
                  </span>
                </div>
              </div>

              <div class="category-overlay">
                <button class="btn btn-primary btn-sm">
                  查看文章
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-section">
            <div class="empty-icon">📂</div>
            <h3>暂无分类</h3>
            <p>还没有创建任何分类</p>
          </div>
        </div>

        <!-- 最新文章预览 -->
        <div class="recent-articles-section" v-if="recentArticles.length > 0">
          <h2 class="section-title">最新文章</h2>
          <div class="article-preview-grid">
            <router-link
              v-for="article in recentArticles"
              :key="article.id"
              :to="`/article/${article.id}`"
              class="article-preview-card"
            >
              <div class="preview-cover">
                <img
                  :src="article.coverImage || '/api/placeholder/200/120'"
                  :alt="article.title"
                  loading="lazy"
                />
                <div class="preview-category">{{ article.categoryName }}</div>
              </div>
              <div class="preview-content">
                <h4 class="preview-title">{{ article.title }}</h4>
                <p class="preview-summary">{{ article.summary }}</p>
                <div class="preview-meta">
                  <span class="preview-date">{{ formatDate(article.createTime) }}</span>
                  <span class="preview-views">
                    <el-icon><View /></el-icon>
                    {{ article.viewCount || 0 }}
                  </span>
                </div>
              </div>
            </router-link>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Loading, Document, View, Sunny, Moon
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const categories = ref([])
const recentArticles = ref([])
const isDark = ref(false)

const categoryStats = reactive({
  total: 0,
  articles: 0,
  views: 0
})

// 计算属性
const topCategory = computed(() => {
  return categories.value.reduce((prev, current) => {
    return (current.articleCount || 0) > (prev?.articleCount || 0) ? current : prev
  }, null)
})

// 方法
const loadCategories = async () => {
  loading.value = true

  try {
    const response = await axios.get('/api/v1/categories')

    if (response.data.code === 200) {
      categories.value = response.data.data || []

      // 计算统计数据
      categoryStats.total = categories.value.length
      categoryStats.articles = categories.value.reduce((sum, cat) => sum + (cat.articleCount || 0), 0)
      categoryStats.views = categories.value.reduce((sum, cat) => sum + (cat.viewCount || 0), 0)
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

const loadRecentArticles = async () => {
  try {
    const response = await axios.get('/api/v1/articles/recent?limit=6')

    if (response.data.code === 200) {
      recentArticles.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载最新文章失败:', error)
  }
}

const viewCategory = (categoryId) => {
  router.push({
    path: '/articles',
    query: { categoryId }
  })
}

const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '技术': '💻',
    '生活': '🏠',
    '学习': '📚',
    '工作': '💼',
    '旅行': '✈️',
    '美食': '🍔',
    '音乐': '🎵',
    '电影': '🎬',
    '读书': '📖',
    '健康': '💪',
    'JavaScript': '🟨',
    'Vue': '💚',
    'React': '⚛️',
    'Node.js': '🟢',
    'Python': '🐍',
    'Java': '☕',
    '前端': '🎨',
    '后端': '⚙️',
    '数据库': '🗄️',
    '算法': '🧮'
  }

  return iconMap[categoryName] || '📁'
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
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
    month: 'long',
    day: 'numeric'
  })
}

// 生命周期
onMounted(() => {
  loadCategories()
  loadRecentArticles()

  // 检查主题偏好
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = prefersDark
  document.documentElement.classList.toggle('dark', isDark.value)
})
</script>

<style scoped>
.category-layout {
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

/* 统计卡片区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-10);
}

.stat-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  transition: all var(--duration-normal) var(--ease-out);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  font-size: var(--text-3xl);
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  background: var(--primary-50);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 分类区域 */
.categories-section {
  margin-bottom: var(--space-12);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-6);
}

.category-card {
  position: relative;
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-out);
  overflow: hidden;
}

.category-card:hover {
  box-shadow: var(--shadow-lg);
}

.category-card:hover .category-overlay {
  opacity: 1;
}

.category-icon {
  font-size: var(--text-4xl);
  width: 80px;
  height: 80px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--primary-500), var(--success-500));
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
  box-shadow: var(--shadow-md);
}

.category-content {
  position: relative;
  z-index: 1;
}

.category-name {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.category-description {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-4) 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.category-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.hot-tag-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

.hot-tag {
  font-size: var(--text-xs);
  color: var(--primary-600);
  background: var(--primary-50);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  border: 1px solid var(--primary-200);
}

.category-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.9), rgba(16, 185, 129, 0.9));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--duration-normal) var(--ease-out);
}

/* 最新文章预览 */
.recent-articles-section {
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-6);
  text-align: center;
}

.article-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-5);
}

.article-preview-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  text-decoration: none;
  transition: all var(--duration-normal) var(--ease-out);
}

.article-preview-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.preview-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.preview-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-normal) var(--ease-out);
}

.article-preview-card:hover .preview-cover img {
  transform: scale(1.05);
}

.preview-category {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  background: var(--primary-500);
  color: white;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.preview-content {
  padding: var(--space-4);
}

.preview-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.preview-summary {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin: 0 0 var(--space-3) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.preview-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.preview-views {
  display: flex;
  align-items: center;
  gap: var(--space-1);
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
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }

  .stat-card {
    padding: var(--space-4);
    flex-direction: column;
    text-align: center;
    gap: var(--space-2);
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: var(--text-2xl);
  }

  .category-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .category-card {
    padding: var(--space-4);
  }

  .category-icon {
    width: 60px;
    height: 60px;
    font-size: var(--text-3xl);
  }

  .category-stats {
    flex-direction: column;
    gap: var(--space-2);
  }

  .article-preview-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: var(--text-3xl);
  }

  .stats-section {
    grid-template-columns: 1fr;
  }

  .stat-number {
    font-size: var(--text-xl);
  }

  .category-card {
    margin: 0 calc(-1 * var(--space-2));
    border-radius: 0;
  }

  .article-preview-card {
    margin: 0 calc(-1 * var(--space-2));
    border-radius: 0;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>