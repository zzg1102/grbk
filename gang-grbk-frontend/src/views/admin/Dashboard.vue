<template>
  <div class="admin-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userInfo?.nickname || userInfo?.username }}！</h1>
          <p>今天是 {{ currentDate }}，继续创作精彩内容吧</p>
        </div>
        <div class="quick-actions">
          <router-link to="/admin/articles/create" class="btn btn-primary btn-lg write-article-btn">
            <el-icon><EditPen /></el-icon>
            <span>写新文章</span>
            <small>开始创作精彩内容</small>
          </router-link>
          <router-link to="/admin/articles" class="btn btn-ghost btn-lg">
            <el-icon><Document /></el-icon>
            <span>管理文章</span>
            <small>查看和编辑已有文章</small>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon articles">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.articleCount }}</div>
          <div class="stat-label">总文章数</div>
          <div class="stat-trend" :class="{ positive: stats.articleGrowth > 0 }">
            <el-icon><TrendCharts /></el-icon>
            {{ stats.articleGrowth > 0 ? '+' : '' }}{{ stats.articleGrowth }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon views">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(stats.totalViews) }}</div>
          <div class="stat-label">总浏览量</div>
          <div class="stat-trend" :class="{ positive: stats.viewGrowth > 0 }">
            <el-icon><TrendCharts /></el-icon>
            {{ stats.viewGrowth > 0 ? '+' : '' }}{{ formatNumber(stats.viewGrowth) }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon comments">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.commentCount }}</div>
          <div class="stat-label">总评论数</div>
          <div class="stat-trend" :class="{ positive: stats.commentGrowth > 0 }">
            <el-icon><TrendCharts /></el-icon>
            {{ stats.commentGrowth > 0 ? '+' : '' }}{{ stats.commentGrowth }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon likes">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.likeCount }}</div>
          <div class="stat-label">总点赞数</div>
          <div class="stat-trend" :class="{ positive: stats.likeGrowth > 0 }">
            <el-icon><TrendCharts /></el-icon>
            {{ stats.likeGrowth > 0 ? '+' : '' }}{{ stats.likeGrowth }}
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="dashboard-content">
      <!-- 左侧内容 -->
      <div class="main-content">
        <!-- 最近文章 -->
        <div class="content-card">
          <div class="card-header">
            <h3>最近文章</h3>
            <router-link to="/admin/articles" class="view-all-link">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="card-body">
            <div v-if="recentArticles.length === 0" class="empty-state">
              <el-icon><Document /></el-icon>
              <p>还没有文章，<router-link to="/admin/articles/create">创建第一篇</router-link></p>
            </div>
            <div v-else class="article-list">
              <div
                v-for="article in recentArticles"
                :key="article.id"
                class="article-item"
                @click="editArticle(article.id)"
              >
                <div class="article-info">
                  <h4 class="article-title">{{ article.title }}</h4>
                  <div class="article-meta">
                    <span class="article-status" :class="getStatusClass(article.status)">
                      {{ getStatusText(article.status) }}
                    </span>
                    <span class="article-date">{{ formatDate(article.updateTime) }}</span>
                    <span class="article-stats">
                      <el-icon><View /></el-icon>
                      {{ article.viewCount || 0 }}
                    </span>
                  </div>
                </div>
                <div class="article-actions">
                  <el-button size="small" text @click.stop="editArticle(article.id)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button size="small" text @click.stop="viewArticle(article.id)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门文章 -->
        <div class="content-card">
          <div class="card-header">
            <h3>热门文章</h3>
          </div>
          <div class="card-body">
            <div v-if="hotArticles.length === 0" class="empty-state">
              <el-icon><TrendCharts /></el-icon>
              <p>暂无热门文章数据</p>
            </div>
            <div v-else class="hot-articles">
              <div
                v-for="(article, index) in hotArticles"
                :key="article.id"
                class="hot-article-item"
              >
                <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                <div class="article-info">
                  <h4 class="article-title">{{ article.title }}</h4>
                  <div class="article-stats">
                    <span><el-icon><View /></el-icon>{{ formatNumber(article.viewCount) }}</span>
                    <span><el-icon><Star /></el-icon>{{ article.likeCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <aside class="sidebar-content">
        <!-- 快速操作 -->
        <div class="content-card">
          <div class="card-header">
            <h3>快速操作</h3>
          </div>
          <div class="card-body">
            <div class="quick-actions-grid">
              <router-link to="/admin/articles/create" class="quick-action-item">
                <el-icon><EditPen /></el-icon>
                <span>写文章</span>
              </router-link>
              <router-link to="/admin/articles" class="quick-action-item">
                <el-icon><Document /></el-icon>
                <span>文章管理</span>
              </router-link>
              <router-link to="/admin/categories" class="quick-action-item">
                <el-icon><Folder /></el-icon>
                <span>分类管理</span>
              </router-link>
              <router-link to="/admin/tags" class="quick-action-item">
                <el-icon><PriceTag /></el-icon>
                <span>标签管理</span>
              </router-link>
              <router-link to="/admin/comments" class="quick-action-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>评论管理</span>
              </router-link>
              <router-link to="/admin/settings" class="quick-action-item">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </router-link>
            </div>
          </div>
        </div>

        <!-- 最新评论 -->
        <div class="content-card">
          <div class="card-header">
            <h3>最新评论</h3>
            <router-link to="/admin/comments" class="view-all-link">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="card-body">
            <div v-if="recentComments.length === 0" class="empty-state">
              <el-icon><ChatDotRound /></el-icon>
              <p>暂无评论</p>
            </div>
            <div v-else class="comment-list">
              <div
                v-for="comment in recentComments"
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-avatar">
                  <el-avatar :size="32" :src="comment.avatar">
                    {{ comment.nickname?.charAt(0) || comment.username?.charAt(0) }}
                  </el-avatar>
                </div>
                <div class="comment-content">
                  <div class="comment-author">{{ comment.nickname || comment.username }}</div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-meta">
                    <span>{{ comment.articleTitle }}</span>
                    <span>{{ formatDate(comment.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 系统信息 -->
        <div class="content-card">
          <div class="card-header">
            <h3>系统信息</h3>
          </div>
          <div class="card-body">
            <div class="system-info">
              <div class="info-item">
                <span class="info-label">博客版本</span>
                <span class="info-value">v1.0.0</span>
              </div>
              <div class="info-item">
                <span class="info-label">运行时间</span>
                <span class="info-value">{{ uptime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">最后更新</span>
                <span class="info-value">{{ lastUpdate }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  EditPen, Document, View, ChatDotRound, Star, TrendCharts, ArrowRight,
  Edit, Folder, PriceTag, Setting
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const userInfo = ref(JSON.parse(localStorage.getItem('user_info') || '{}'))
const recentArticles = ref([])
const hotArticles = ref([])
const recentComments = ref([])

const stats = reactive({
  articleCount: 0,
  totalViews: 0,
  commentCount: 0,
  likeCount: 0,
  articleGrowth: 0,
  viewGrowth: 0,
  commentGrowth: 0,
  likeGrowth: 0
})

// 计算属性
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const uptime = computed(() => {
  // 这里应该从后端获取真实的运行时间
  return '7天 12小时'
})

const lastUpdate = computed(() => {
  return new Date().toLocaleDateString('zh-CN')
})

// 方法
const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statsResponse = await axios.get('/v1/admin/stats')
    if (statsResponse.data.code === 200) {
      Object.assign(stats, statsResponse.data.data)
    }

    // 加载最近文章
    const articlesResponse = await axios.get('/v1/articles/latest?limit=5')
    if (articlesResponse.data.code === 200) {
      recentArticles.value = articlesResponse.data.data || []
    }

    // 加载热门文章
    const hotResponse = await axios.get('/v1/articles/hot?limit=5')
    if (hotResponse.data.code === 200) {
      hotArticles.value = hotResponse.data.data || []
    }

    // 加载最新评论
    const commentsResponse = await axios.get('/v1/comments/latest?limit=5')
    if (commentsResponse.data.code === 200) {
      recentComments.value = commentsResponse.data.data || []
    }
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
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

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)} 分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)} 小时前`
  } else if (diff < 2592000000) {
    return `${Math.floor(diff / 86400000)} 天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'draft'
    case 1: return 'published'
    case 2: return 'offline'
    default: return 'draft'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '草稿'
    case 1: return '已发布'
    case 2: return '已下线'
    default: return '未知'
  }
}

const editArticle = (id) => {
  router.push(`/admin/articles/edit/${id}`)
}

const viewArticle = (id) => {
  window.open(`/article/${id}`, '_blank')
}

// 生命周期
onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: var(--space-6);
  background: var(--bg-primary);
  min-height: 100vh;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  color: white;
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-lg);
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h1 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-2) 0;
}

.welcome-text p {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
}

.quick-actions {
  display: flex;
  gap: var(--space-4);
  margin-top: var(--space-6);
}

.write-article-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: var(--space-4) var(--space-6);
  border-radius: var(--radius-xl);
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  min-width: 160px;
  backdrop-filter: blur(10px);
  transition: all var(--duration-normal) var(--ease-out);
}

.write-article-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.write-article-btn .el-icon {
  font-size: var(--text-2xl);
}

.write-article-btn span {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

.write-article-btn small {
  font-size: var(--text-xs);
  opacity: 0.8;
  text-align: center;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
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
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-2xl);
}

.stat-icon.articles {
  background: linear-gradient(135deg, var(--primary-100), var(--primary-200));
  color: var(--primary-600);
}

.stat-icon.views {
  background: linear-gradient(135deg, var(--success-100), var(--success-200));
  color: var(--success-600);
}

.stat-icon.comments {
  background: linear-gradient(135deg, var(--warning-100), var(--warning-200));
  color: var(--warning-600);
}

.stat-icon.likes {
  background: linear-gradient(135deg, var(--error-100), var(--error-200));
  color: var(--error-600);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.stat-trend.positive {
  color: var(--success-500);
}

/* 主要内容区域 */
.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-8);
}

.main-content,
.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

/* 内容卡片 */
.content-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5) var(--space-6);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.card-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--primary-500);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: color var(--duration-fast) var(--ease-out);
}

.view-all-link:hover {
  color: var(--primary-600);
}

.card-body {
  padding: var(--space-6);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: var(--space-8) 0;
  color: var(--text-tertiary);
}

.empty-state .el-icon {
  font-size: var(--text-4xl);
  margin-bottom: var(--space-3);
}

/* 文章列表 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.article-item:hover {
  border-color: var(--primary-300);
  background: var(--primary-50);
}

.article-info {
  flex: 1;
}

.article-title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.article-status {
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.article-status.draft {
  background: var(--warning-100);
  color: var(--warning-700);
}

.article-status.published {
  background: var(--success-100);
  color: var(--success-700);
}

.article-status.offline {
  background: var(--error-100);
  color: var(--error-700);
}

.article-stats {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.article-actions {
  display: flex;
  gap: var(--space-2);
}

/* 热门文章 */
.hot-articles {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.hot-article-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: white;
}

.rank-number.rank-1 {
  background: var(--warning-500);
}

.rank-number.rank-2 {
  background: var(--gray-400);
}

.rank-number.rank-3 {
  background: var(--warning-600);
}

.rank-number:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: var(--gray-300);
  color: var(--text-secondary);
}

/* 快速操作 */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-3);
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  text-decoration: none;
  color: var(--text-secondary);
  transition: all var(--duration-fast) var(--ease-out);
}

.quick-action-item:hover {
  border-color: var(--primary-300);
  background: var(--primary-50);
  color: var(--primary-500);
}

.quick-action-item .el-icon {
  font-size: var(--text-2xl);
}

.quick-action-item span {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.comment-item {
  display: flex;
  gap: var(--space-3);
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-author {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.comment-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.comment-meta {
  display: flex;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 系统信息 */
.system-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--text-sm);
}

.info-label {
  color: var(--text-secondary);
}

.info-value {
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: var(--space-4);
  }
  
  .welcome-banner {
    padding: var(--space-6);
  }
  
  .banner-content {
    flex-direction: column;
    gap: var(--space-4);
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .article-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .article-actions {
    align-self: flex-end;
  }
}
</style>
