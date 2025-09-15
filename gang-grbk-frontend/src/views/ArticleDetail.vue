<template>
  <div class="article-detail-layout">
    <!-- 简约导航栏 -->
    <header class="article-header">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/" class="back-link">
            <el-icon><ArrowLeft /></el-icon>
            <span>Gang's Blog</span>
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
              <!-- 文章封面 -->
              <div class="article-hero" v-if="article.coverImage">
                <div class="hero-image">
                  <img
                    :src="article.coverImage"
                    :alt="article.title"
                    loading="lazy"
                  />
                  <div class="hero-overlay"></div>
                </div>
                <div class="hero-content">
                  <div class="hero-meta">
                    <div class="article-category" v-if="article.categoryName">
                      <el-icon><Document /></el-icon>
                      <span>{{ article.categoryName }}</span>
                    </div>
                    <div class="reading-time">
                      <el-icon><Clock /></el-icon>
                      <span>{{ readTime }}分钟阅读</span>
                    </div>
                  </div>
                  <h1 class="hero-title">{{ article.title }}</h1>
                  <div class="hero-author">
                    <img
                      :src="article.authorAvatar || '/api/placeholder/40/40'"
                      :alt="article.authorName"
                      class="author-avatar"
                    />
                    <div class="author-info">
                      <span class="author-name">{{ article.authorName }}</span>
                      <div class="author-meta">
                        <span class="publish-date">{{ formatDate(article.createTime) }}</span>
                        <span class="meta-separator">·</span>
                        <span class="article-stats">
                          <el-icon><View /></el-icon>
                          {{ article.viewCount || 0 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 无封面的文章头部 -->
              <header v-else class="article-header-simple">
                <div class="article-category-simple" v-if="article.categoryName">
                  <el-icon><Document /></el-icon>
                  <span>{{ article.categoryName }}</span>
                </div>
                <h1 class="article-title-simple">{{ article.title }}</h1>
                <div class="article-meta-simple">
                  <div class="author-section">
                    <img
                      :src="article.authorAvatar || '/api/placeholder/48/48'"
                      :alt="article.authorName"
                      class="author-avatar"
                    />
                    <div class="author-info">
                      <span class="author-name">{{ article.authorName }}</span>
                      <div class="meta-row">
                        <span class="publish-date">{{ formatDate(article.createTime) }}</span>
                        <span class="meta-separator">·</span>
                        <span class="reading-time">{{ readTime }}分钟阅读</span>
                        <span class="meta-separator">·</span>
                        <span class="view-count">
                          <el-icon><View /></el-icon>
                          {{ article.viewCount || 0 }} 浏览
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </header>

              <!-- 文章标签 -->
              <div class="article-tags" v-if="article.tags && article.tags.length">
                <div class="tags-container">
                  <span
                    v-for="tag in article.tags"
                    :key="tag.id"
                    class="article-tag"
                  >
                    #{{ tag.name }}
                  </span>
                </div>
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

                <button 
                  class="action-btn comment-btn"
                  :class="{ active: showComments }"
                  @click="toggleComments"
                >
                  <el-icon><ChatDotRound /></el-icon>
                  <span>评论 ({{ article.commentCount || 0 }})</span>
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

              <!-- 评论区域 -->
              <div v-if="showComments" class="article-comments">
                <div class="comments-header">
                  <h3 class="comments-title">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>评论区</span>
                  </h3>
                  <div class="comments-actions">
                    <span class="comments-count">{{ article.commentCount || 0 }} 条评论</span>
                    <button class="close-comments-btn" @click="toggleComments" title="收起评论">
                      <el-icon><ArrowUp /></el-icon>
                    </button>
                  </div>
                </div>
                <div class="comments-content">
                  <CommentList :article-id="articleId" v-if="article.id" @comment-updated="handleCommentUpdated" />
                </div>
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
  Sunny, Moon, User, ArrowLeft, Setting, SwitchButton, Document, Clock, ArrowUp
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
const showShareDialog = ref(false)
const showComments = ref(false)

// 登录状态管理
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 检查是否为管理员
const isAdmin = computed(() => {
  return userInfo.value?.userType === 1
})

// 阅读时间计算
const readTime = computed(() => {
  if (!article.value?.content) return 0
  const wordsPerMinute = 200
  const wordCount = article.value.content.replace(/\s/g, '').length
  return Math.ceil(wordCount / wordsPerMinute)
})

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
      
      // 设置用户点赞状态
      isLiked.value = data.isLiked || false

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

// 处理评论更新事件
const handleCommentUpdated = async () => {
  // 重新加载文章数据以更新评论数量
  await loadArticle()
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
      prevArticle.value = data?.prev || null
      nextArticle.value = data?.next || null
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
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const response = await axios.put(`/api/v1/articles/${articleId.value}/like`)
    
    if (response.data.code === 200) {
      const result = response.data.data
      // 更新本地状态，无需重新加载整篇文章
      isLiked.value = result.isLiked
      article.likeCount = result.likeCount
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
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

const toggleComments = () => {
  showComments.value = !showComments.value
  if (showComments.value) {
    // 展开评论时，滚动到评论区域
    nextTick(() => {
      const commentsElement = document.querySelector('.article-comments')
      if (commentsElement) {
        commentsElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    })
  }
}

const collectArticle = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    // 暂时使用点赞接口作为收藏功能的替代
    ElMessage.info('收藏功能开发中，敬请期待')
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败')
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

// 用户操作处理
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

// 监听器
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadArticle()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  // 首先检查登录状态
  checkLoginStatus()

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
  background: var(--bg-primary);
}

/* 导航栏样式 */
.article-header {
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-secondary);
  padding: var(--space-4) 0;
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  backdrop-filter: blur(10px);
}

.header-container {
  max-width: none;
  margin: 0;
  padding: 0 var(--space-4);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 var(--space-3);
  }
}

.header-left {
  display: flex;
  align-items: center;
}

.back-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--primary-600);
  text-decoration: none;
  padding: var(--space-2) 0;
  border-radius: var(--radius-md);
  transition: all var(--duration-fast) var(--ease-out);
  font-weight: var(--font-semibold);
  font-size: var(--text-lg);
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
  font-size: var(--text-sm);
}

.action-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

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

.layout-main {
  flex: 1;
}

.container-wide {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
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
  grid-template-columns: 1fr 280px;
  gap: var(--space-6);
  margin-bottom: var(--space-12);
  max-width: none;
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
  margin-bottom: var(--space-8);
}

/* 文章英雄区域 - 有封面 */
.article-hero {
  position: relative;
  height: 60vh;
  min-height: 400px;
  max-height: 600px;
  overflow: hidden;
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

.hero-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.1) 0%,
    rgba(0, 0, 0, 0.3) 60%,
    rgba(0, 0, 0, 0.7) 100%
  );
}

.hero-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: var(--space-8);
  color: white;
  z-index: 2;
}

.hero-meta {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  flex-wrap: wrap;
}

.article-category,
.reading-time {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.hero-title {
  font-size: var(--text-5xl);
  font-weight: var(--font-black);
  line-height: 1.1;
  margin: 0 0 var(--space-6) 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-author {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.hero-author .author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.hero-author .author-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.hero-author .author-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

.hero-author .author-meta {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  opacity: 0.9;
}

.meta-separator {
  opacity: 0.6;
}

.hero-author .article-stats {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* 简单文章头部 - 无封面 */
.article-header-simple {
  padding: var(--space-8);
  text-align: center;
  border-bottom: 1px solid var(--border-secondary);
}

.article-category-simple {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: var(--primary-100);
  color: var(--primary-700);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  margin-bottom: var(--space-4);
}

.article-title-simple {
  font-size: var(--text-5xl);
  font-weight: var(--font-black);
  color: var(--text-primary);
  line-height: 1.1;
  margin: 0 0 var(--space-6) 0;
  letter-spacing: -0.025em;
}

.article-meta-simple {
  display: flex;
  justify-content: center;
}

.author-section {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.author-section .author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px solid var(--border-secondary);
}

.author-section .author-info {
  text-align: left;
}

.author-section .author-name {
  display: block;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.meta-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.view-count {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* 文章标签 */
.article-tags {
  padding: var(--space-6) var(--space-8);
  border-bottom: 1px solid var(--border-secondary);
}

.tags-container {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
  justify-content: center;
}

.article-tag {
  display: inline-flex;
  align-items: center;
  padding: var(--space-2) var(--space-3);
  background: var(--bg-secondary);
  color: var(--primary-600);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  text-decoration: none;
  transition: all var(--duration-fast) var(--ease-out);
}

.article-tag:hover {
  background: var(--primary-100);
  color: var(--primary-700);
  transform: translateY(-1px);
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
  padding: var(--space-6) var(--space-8);
}

.prose {
  max-width: none;
  color: var(--text-primary);
  line-height: 1.75;
  font-size: var(--text-base);
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

.action-btn.like-btn:hover {
  border-color: var(--error);
  color: var(--error);
}

.action-btn.comment-btn:hover {
  border-color: var(--success);
  color: var(--success);
}

.action-btn.active {
  background: var(--primary-500);
  border-color: var(--primary-500);
  color: white;
}

.action-btn.active.like-btn {
  background: var(--error) !important;
  border-color: var(--error) !important;
  color: white !important;
}

.action-btn.active.comment-btn {
  background: var(--success);
  border-color: var(--success);
  color: white;
}

/* 文章内评论区域 */
.article-comments {
  border-top: 1px solid var(--border-secondary);
}

.comments-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6) var(--space-8);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.comments-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.comments-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.comments-title .el-icon {
  font-size: var(--text-lg);
  color: var(--primary-500);
}

.comments-count {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  background: var(--bg-elevated);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  border: 1px solid var(--border-primary);
}

.comments-content {
  padding: var(--space-6) var(--space-8);
}

.close-comments-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-primary);
  border-radius: 50%;
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.close-comments-btn:hover {
  background: var(--bg-secondary);
  border-color: var(--primary-400);
  color: var(--primary-500);
  transform: translateY(-1px);
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
    padding: 0 var(--space-4);
  }

  .article-layout {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .article-sidebar {
    order: -1;
  }


  .hero-title,
  .article-title-simple {
    font-size: var(--text-4xl);
  }

  .article-hero {
    height: 50vh;
    min-height: 300px;
  }

  .hero-content {
    padding: var(--space-6);
  }

  .article-header-simple {
    padding: var(--space-6);
  }

  .article-tags {
    padding: var(--space-4) var(--space-6);
  }

  .article-content {
    padding: var(--space-4) var(--space-6);
  }

  .article-actions {
    padding: var(--space-4) var(--space-6);
    flex-wrap: wrap;
  }

  .comments-header {
    padding: var(--space-4) var(--space-6);
  }

  .comments-content {
    padding: var(--space-4) var(--space-6);
  }

  .article-navigation {
    grid-template-columns: 1fr;
    padding: var(--space-6);
  }

  .nav-item.next-article {
    text-align: left;
  }
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

@media (max-width: 480px) {
  .breadcrumb {
    font-size: var(--text-xs);
    margin: var(--space-2) 0 var(--space-4) 0;
  }

  .breadcrumb-item.current {
    max-width: 120px;
  }

  .hero-title,
  .article-title-simple {
    font-size: var(--text-3xl);
  }

  .article-hero {
    height: 40vh;
    min-height: 250px;
  }

  .hero-content {
    padding: var(--space-4);
  }

  .hero-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  .author-section {
    flex-direction: column;
    text-align: center;
    gap: var(--space-3);
  }

  .author-section .author-info {
    text-align: center;
  }

  .article-header-simple {
    padding: var(--space-4);
  }

  .article-tags {
    padding: var(--space-3) var(--space-4);
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