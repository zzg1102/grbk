<template>
  <div class="layout article-detail-layout">
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
      <div class="container-wide">
        <!-- 面包屑导航 -->
        <nav class="breadcrumb" v-if="article.title">
          <router-link to="/" class="breadcrumb-item">首页</router-link>
          <span class="breadcrumb-separator">/</span>
          <router-link to="/articles" class="breadcrumb-item">文章</router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-item current">{{ article.title }}</span>
        </nav>

        <div class="article-layout">
          <!-- 文章主体 -->
          <article class="article-main">
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-section">
              <div class="loading-spinner">
                <el-icon class="rotating"><Loading /></el-icon>
              </div>
              <p>正在加载文章...</p>
            </div>

            <!-- 文章内容 -->
            <div v-else-if="article.id" class="article-container">
              <!-- 文章头部 -->
              <header class="article-header">
                <h1 class="article-title">{{ article.title }}</h1>

                <div class="article-meta">
                  <div class="meta-primary">
                    <div class="author-info">
                      <img
                        :src="article.authorAvatar || '/api/placeholder/32/32'"
                        :alt="article.authorName"
                        class="author-avatar"
                      />
                      <div class="author-details">
                        <span class="author-name">{{ article.authorName }}</span>
                        <span class="publish-date">{{ formatDate(article.createTime) }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="meta-secondary">
                    <div class="article-stats">
                      <span class="stat-item">
                        <el-icon><View /></el-icon>
                        {{ article.viewCount || 0 }} 浏览
                      </span>
                      <span class="stat-item">
                        <el-icon><Star /></el-icon>
                        {{ article.likeCount || 0 }} 点赞
                      </span>
                      <span class="stat-item">
                        <el-icon><ChatDotRound /></el-icon>
                        {{ article.commentCount || 0 }} 评论
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 文章分类和标签 -->
                <div class="article-taxonomy">
                  <div class="taxonomy-item" v-if="article.categoryName">
                    <span class="taxonomy-label">分类：</span>
                    <span class="category-tag">{{ article.categoryName }}</span>
                  </div>
                  <div class="taxonomy-item" v-if="article.tags && article.tags.length">
                    <span class="taxonomy-label">标签：</span>
                    <div class="tag-list">
                      <span
                        v-for="tag in article.tags"
                        :key="tag.id"
                        class="tag"
                      >
                        {{ tag.name }}
                      </span>
                    </div>
                  </div>
                </div>
              </header>

              <!-- 文章封面 -->
              <div class="article-cover" v-if="article.coverImage">
                <img
                  :src="article.coverImage"
                  :alt="article.title"
                  loading="lazy"
                />
              </div>

              <!-- 文章内容 -->
              <div class="article-content">
                <div class="prose" v-html="article.content"></div>
              </div>

              <!-- 文章操作 -->
              <div class="article-actions">
                <button
                  class="action-btn like-btn"
                  :class="{ active: isLiked }"
                  @click="toggleLike"
                >
                  <el-icon><Star /></el-icon>
                  <span>{{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount || 0 }})</span>
                </button>

                <button class="action-btn share-btn" @click="shareArticle">
                  <el-icon><Share /></el-icon>
                  <span>分享</span>
                </button>

                <button class="action-btn collect-btn" @click="collectArticle">
                  <el-icon><Collection /></el-icon>
                  <span>收藏</span>
                </button>
              </div>

              <!-- 上一篇/下一篇导航 -->
              <nav class="article-navigation" v-if="prevArticle || nextArticle">
                <div class="nav-item prev-article" v-if="prevArticle">
                  <span class="nav-label">上一篇</span>
                  <router-link :to="`/article/${prevArticle.id}`" class="nav-title">
                    {{ prevArticle.title }}
                  </router-link>
                </div>
                <div class="nav-item next-article" v-if="nextArticle">
                  <span class="nav-label">下一篇</span>
                  <router-link :to="`/article/${nextArticle.id}`" class="nav-title">
                    {{ nextArticle.title }}
                  </router-link>
                </div>
              </nav>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-section">
              <div class="error-icon">❌</div>
              <h3>文章加载失败</h3>
              <p>{{ error }}</p>
              <button class="btn btn-primary" @click="loadArticle">重新加载</button>
            </div>
          </article>

          <!-- 侧边栏 -->
          <aside class="article-sidebar">
            <!-- 作者信息卡片 -->
            <div class="sidebar-card" v-if="article.authorName">
              <div class="card-header">
                <h3>作者介绍</h3>
              </div>
              <div class="card-body">
                <div class="author-profile">
                  <img
                    :src="article.authorAvatar || '/api/placeholder/80/80'"
                    :alt="article.authorName"
                    class="author-profile-avatar"
                  />
                  <h4 class="author-profile-name">{{ article.authorName }}</h4>
                  <p class="author-profile-bio">{{ article.authorBio || '暂无个人介绍' }}</p>
                  <div class="author-stats">
                    <div class="stat">
                      <span class="stat-value">{{ authorStats.articleCount || 0 }}</span>
                      <span class="stat-label">文章</span>
                    </div>
                    <div class="stat">
                      <span class="stat-value">{{ authorStats.viewCount || 0 }}</span>
                      <span class="stat-label">浏览</span>
                    </div>
                    <div class="stat">
                      <span class="stat-value">{{ authorStats.likeCount || 0 }}</span>
                      <span class="stat-label">获赞</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 相关文章 -->
            <div class="sidebar-card" v-if="relatedArticles.length">
              <div class="card-header">
                <h3>相关文章</h3>
              </div>
              <div class="card-body">
                <div class="related-articles">
                  <router-link
                    v-for="relatedArticle in relatedArticles"
                    :key="relatedArticle.id"
                    :to="`/article/${relatedArticle.id}`"
                    class="related-article-item"
                  >
                    <img
                      :src="relatedArticle.coverImage || '/api/placeholder/60/60'"
                      :alt="relatedArticle.title"
                      class="related-article-image"
                    />
                    <div class="related-article-content">
                      <h4 class="related-article-title">{{ relatedArticle.title }}</h4>
                      <div class="related-article-meta">
                        <span class="view-count">
                          <el-icon><View /></el-icon>
                          {{ relatedArticle.viewCount || 0 }}
                        </span>
                        <span class="publish-date">{{ formatDate(relatedArticle.createTime) }}</span>
                      </div>
                    </div>
                  </router-link>
                </div>
              </div>
            </div>

            <!-- 目录 -->
            <div class="sidebar-card toc-card" v-if="tocItems.length">
              <div class="card-header">
                <h3>目录</h3>
              </div>
              <div class="card-body">
                <nav class="table-of-contents">
                  <a
                    v-for="item in tocItems"
                    :key="item.id"
                    :href="`#${item.id}`"
                    :class="['toc-item', `toc-level-${item.level}`]"
                    @click="scrollToHeading(item.id)"
                  >
                    {{ item.text }}
                  </a>
                </nav>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>

    <!-- 评论区域 -->
    <section class="comments-section">
      <div class="container-wide">
        <div class="comments-container">
          <CommentList :article-id="articleId" v-if="article.id" />
        </div>
      </div>
    </section>

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
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  View, Star, ChatDotRound, Loading, Share, Collection,
  Sunny, Moon
} from '@element-plus/icons-vue'
import axios from 'axios'
import CommentList from '@/components/CommentList.vue'

const router = useRouter()
const route = useRoute()

// Props
const props = defineProps({
  id: {
    type: [String, Number],
    default: null
  }
})

// 响应式数据
const loading = ref(false)
const error = ref('')
const isLiked = ref(false)
const isDark = ref(false)

const article = reactive({
  id: null,
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryName: '',
  tags: [],
  authorName: '',
  authorAvatar: '',
  authorBio: '',
  createTime: '',
  updateTime: '',
  viewCount: 0,
  likeCount: 0,
  commentCount: 0
})

const authorStats = reactive({
  articleCount: 0,
  viewCount: 0,
  likeCount: 0
})

const relatedArticles = ref([])
const prevArticle = ref(null)
const nextArticle = ref(null)
const tocItems = ref([])

// 计算属性
const articleId = computed(() => props.id || route.params.id)

// 方法
const loadArticle = async () => {
  if (!articleId.value) return

  loading.value = true
  error.value = ''

  try {
    const response = await axios.get(`/api/v1/articles/${articleId.value}`)

    if (response.data.code === 200) {
      const data = response.data.data
      Object.assign(article, data)

      // 加载相关数据
      loadRelatedArticles()
      loadNavigationArticles()
      generateTableOfContents()

      // 增加浏览量
      incrementViewCount()
    } else {
      error.value = response.data.message || '文章不存在'
    }
  } catch (err) {
    console.error('加载文章失败:', err)
    error.value = '加载文章失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const loadRelatedArticles = async () => {
  try {
    const response = await axios.get(`/api/v1/articles/${articleId.value}/related`)
    if (response.data.code === 200) {
      relatedArticles.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载相关文章失败:', error)
  }
}

const loadNavigationArticles = async () => {
  try {
    const response = await axios.get(`/api/v1/articles/${articleId.value}/navigation`)
    if (response.data.code === 200) {
      const data = response.data.data
      prevArticle.value = data.prev
      nextArticle.value = data.next
    }
  } catch (error) {
    console.error('加载导航文章失败:', error)
  }
}

const generateTableOfContents = () => {
  nextTick(() => {
    const headings = document.querySelectorAll('.prose h1, .prose h2, .prose h3, .prose h4')
    tocItems.value = Array.from(headings).map((heading, index) => {
      const id = `heading-${index}`
      heading.id = id
      return {
        id,
        text: heading.textContent,
        level: parseInt(heading.tagName.charAt(1))
      }
    })
  })
}

const incrementViewCount = async () => {
  try {
    await axios.post(`/api/v1/articles/${articleId.value}/view`)
  } catch (error) {
    console.error('增加浏览量失败:', error)
  }
}

const toggleLike = async () => {
  try {
    const response = await axios.post(`/api/v1/articles/${articleId.value}/like`)
    if (response.data.code === 200) {
      isLiked.value = !isLiked.value
      article.likeCount = response.data.data.likeCount
      ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('点赞操作失败')
  }
}

const shareArticle = () => {
  if (navigator.share) {
    navigator.share({
      title: article.title,
      text: article.summary,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href).then(() => {
      ElMessage.success('链接已复制到剪贴板')
    })
  }
}

const collectArticle = async () => {
  try {
    const response = await axios.post(`/api/v1/articles/${articleId.value}/collect`)
    if (response.data.code === 200) {
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败，请先登录')
  }
}

const scrollToHeading = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
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
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadArticle()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  // 检查主题偏好
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = prefersDark
  document.documentElement.classList.toggle('dark', isDark.value)
})
</script>

<style scoped>
.article-detail-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-main {
  flex: 1;
}

.container-wide {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

/* 面包屑导航 */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: var(--space-4) 0 var(--space-6) 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.breadcrumb-item {
  color: inherit;
  text-decoration: none;
}

.breadcrumb-item:hover {
  color: var(--primary-500);
}

.breadcrumb-item.current {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.breadcrumb-separator {
  color: var(--text-tertiary);
}

/* 文章布局 */
.article-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-8);
  margin-bottom: var(--space-12);
}

/* 文章主体 */
.article-main {
  min-width: 0;
}

.article-container {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

/* 文章头部 */
.article-header {
  padding: var(--space-8);
  border-bottom: 1px solid var(--border-secondary);
}

.article-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1.2;
  margin: 0 0 var(--space-6) 0;
}

.article-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.meta-primary {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.author-name {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.publish-date {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.meta-secondary {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.article-stats {
  display: flex;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 文章分类和标签 */
.article-taxonomy {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.taxonomy-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.taxonomy-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
  min-width: 50px;
}

.category-tag {
  background: var(--primary-100);
  color: var(--primary-700);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

/* 文章封面 */
.article-cover {
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 文章内容 */
.article-content {
  padding: var(--space-8);
}

.prose {
  max-width: none;
  color: var(--text-primary);
  line-height: 1.75;
}

.prose h1,
.prose h2,
.prose h3,
.prose h4,
.prose h5,
.prose h6 {
  font-weight: var(--font-semibold);
  margin-top: var(--space-6);
  margin-bottom: var(--space-4);
  line-height: 1.3;
}

.prose h1 { font-size: var(--text-3xl); }
.prose h2 { font-size: var(--text-2xl); }
.prose h3 { font-size: var(--text-xl); }
.prose h4 { font-size: var(--text-lg); }

.prose p {
  margin: var(--space-4) 0;
}

.prose ul,
.prose ol {
  margin: var(--space-4) 0;
  padding-left: var(--space-6);
}

.prose li {
  margin: var(--space-2) 0;
}

.prose pre {
  background: var(--gray-100);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  margin: var(--space-4) 0;
  overflow-x: auto;
}

.prose code {
  background: var(--gray-100);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-family: var(--font-mono);
  font-size: 0.875em;
}

.prose blockquote {
  border-left: 4px solid var(--primary-500);
  padding-left: var(--space-4);
  margin: var(--space-6) 0;
  font-style: italic;
  color: var(--text-secondary);
}

/* 文章操作 */
.article-actions {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-6) var(--space-8);
  border-top: 1px solid var(--border-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.action-btn:hover {
  border-color: var(--primary-500);
  color: var(--primary-500);
}

.action-btn.active {
  background: var(--primary-500);
  border-color: var(--primary-500);
  color: white;
}

/* 文章导航 */
.article-navigation {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  padding: var(--space-8);
}

.nav-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  padding: var(--space-4);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast) var(--ease-out);
}

.nav-item:hover {
  border-color: var(--primary-500);
}

.nav-item.next-article {
  text-align: right;
}

.nav-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

.nav-title {
  color: var(--text-primary);
  text-decoration: none;
  font-weight: var(--font-medium);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 侧边栏 */
.article-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.sidebar-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-secondary);
}

.card-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.card-body {
  padding: var(--space-5);
}

/* 作者信息 */
.author-profile {
  text-align: center;
}

.author-profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: var(--space-3);
}

.author-profile-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.author-profile-bio {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-4) 0;
  line-height: 1.5;
}

.author-stats {
  display: flex;
  justify-content: space-around;
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-secondary);
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
}

.stat-value {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* 相关文章 */
.related-articles {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.related-article-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  text-decoration: none;
  transition: background-color var(--duration-fast) var(--ease-out);
}

.related-article-item:hover {
  background: var(--bg-secondary);
}

.related-article-image {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  object-fit: cover;
  flex-shrink: 0;
}

.related-article-content {
  flex: 1;
  min-width: 0;
}

.related-article-title {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-article-meta {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.view-count {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* 目录 */
.table-of-contents {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.toc-item {
  display: block;
  padding: var(--space-1) 0;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  border-left: 2px solid transparent;
  padding-left: var(--space-3);
  transition: all var(--duration-fast) var(--ease-out);
}

.toc-item:hover {
  color: var(--primary-500);
  border-left-color: var(--primary-500);
}

.toc-level-2 { margin-left: var(--space-4); }
.toc-level-3 { margin-left: var(--space-6); }
.toc-level-4 { margin-left: var(--space-8); }

/* 评论区域 */
.comments-section {
  background: var(--bg-secondary);
  padding: var(--space-8) 0;
}

.comments-container {
  max-width: 800px;
  margin: 0 auto;
}

/* 加载和错误状态 */
.loading-section,
.error-section {
  text-align: center;
  padding: var(--space-12) var(--space-8);
}

.loading-spinner {
  font-size: var(--text-4xl);
  color: var(--primary-500);
  margin-bottom: var(--space-4);
}

.rotating {
  animation: rotate 1s linear infinite;
}

.error-icon {
  font-size: var(--text-6xl);
  margin-bottom: var(--space-4);
}

.error-section h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.error-section p {
  color: var(--text-secondary);
  margin: 0 0 var(--space-6) 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container-wide {
    padding: 0 var(--space-2);
  }

  .article-layout {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .article-sidebar {
    order: -1;
  }

  .article-title {
    font-size: var(--text-3xl);
  }

  .article-header {
    padding: var(--space-6);
  }

  .article-content {
    padding: var(--space-6);
  }

  .article-actions {
    padding: var(--space-4) var(--space-6);
    flex-wrap: wrap;
  }

  .article-navigation {
    grid-template-columns: 1fr;
    padding: var(--space-6);
  }

  .nav-item.next-article {
    text-align: left;
  }
}

@media (max-width: 480px) {
  .breadcrumb {
    font-size: var(--text-xs);
    margin: var(--space-2) 0 var(--space-4) 0;
  }

  .breadcrumb-item.current {
    max-width: 120px;
  }

  .article-title {
    font-size: var(--text-2xl);
  }

  .meta-primary {
    flex-direction: column;
    align-items: flex-start;
  }

  .article-stats {
    flex-wrap: wrap;
    gap: var(--space-2);
  }

  .taxonomy-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-1);
  }

  .action-btn {
    flex-direction: column;
    gap: var(--space-1);
    padding: var(--space-2);
    text-align: center;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>