<template>
  <div class="article-management">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">文章管理</h1>
        <p class="page-description">管理所有博客文章的发布、编辑和删除</p>
      </div>
      <div class="header-actions">
        <router-link to="/admin/articles/create" class="btn btn-primary btn-lg write-new-btn">
          <el-icon><EditPen /></el-icon>
          <div class="btn-content">
            <span>写新文章</span>
            <small>创作精彩内容</small>
          </div>
        </router-link>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchForm.title"
          placeholder="搜索文章标题..."
          class="search-input"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="searchForm.status" placeholder="文章状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已下线" :value="2" />
        </el-select>

        <el-select v-model="searchForm.categoryId" placeholder="文章分类" clearable @change="handleFilter">
          <el-option label="全部分类" value="" />
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </div>

      <div class="filter-right">
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="batchDelete" :disabled="selectedIds.length === 0" type="danger" plain>
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="articles"
        @selection-change="handleSelectionChange"
        row-key="id"
        class="article-table"
      >
        <el-table-column type="selection" width="50" />
        
        <el-table-column label="文章信息" min-width="300">
          <template #default="{ row }">
            <div class="article-info">
              <div class="article-cover">
                <img
                  v-if="row.coverImage"
                  :src="row.coverImage"
                  :alt="row.title"
                  @error="handleImageError"
                />
                <div v-else class="no-cover">
                  <el-icon><Picture /></el-icon>
                </div>
              </div>
              <div class="article-details">
                <h3 class="article-title" @click="viewArticle(row.id)">{{ row.title }}</h3>
                <p class="article-summary">{{ row.summary || '暂无摘要' }}</p>
                <div class="article-meta">
                  <span class="category-tag">{{ row.categoryName || '未分类' }}</span>
                  <span v-if="row.tags && row.tags.length" class="tags">
                    <el-tag
                      v-for="tag in row.tags.slice(0, 3)"
                      :key="tag.id"
                      size="small"
                      class="tag-item"
                    >
                      {{ tag.name }}
                    </el-tag>
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
            <div v-if="row.isTop" class="top-badge">
              <el-icon><Top /></el-icon>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="数据统计" width="120" align="center">
          <template #default="{ row }">
            <div class="stats-info">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ row.viewCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ row.likeCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ row.commentCount || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="150" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <div class="publish-time">{{ formatDate(row.createTime) }}</div>
              <div class="update-time">更新: {{ formatDate(row.updateTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="editArticle(row.id)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              
              <el-dropdown @command="(command) => handleAction(command, row)">
                <el-button size="small" type="primary" plain>
                  更多
                  <el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="view">
                      <el-icon><View /></el-icon>
                      预览
                    </el-dropdown-item>
                    <el-dropdown-item command="publish" v-if="row.status !== 1">
                      <el-icon><Promotion /></el-icon>
                      发布
                    </el-dropdown-item>
                    <el-dropdown-item command="offline" v-if="row.status === 1">
                      <el-icon><Download /></el-icon>
                      下线
                    </el-dropdown-item>
                    <el-dropdown-item command="top" v-if="!row.isTop">
                      <el-icon><Top /></el-icon>
                      置顶
                    </el-dropdown-item>
                    <el-dropdown-item command="untop" v-if="row.isTop">
                      <el-icon><Bottom /></el-icon>
                      取消置顶
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && articles.length === 0" class="empty-state">
      <el-empty description="暂无文章数据">
        <router-link to="/admin/articles/create" class="btn btn-primary">
          <el-icon><EditPen /></el-icon>
          创建第一篇文章
        </router-link>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  EditPen, Search, Refresh, Delete, Picture, View, Star, ChatDotRound,
  Edit, ArrowDown, Promotion, Download, Top, Bottom
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const categories = ref([])
const selectedIds = ref([])
const total = ref(0)

const pagination = reactive({
  current: 1,
  size: 20
})

const searchForm = reactive({
  title: '',
  status: '',
  categoryId: ''
})

// 方法
const loadArticles = async () => {
  loading.value = true
  
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }

    // 过滤空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await axios.get('/v1/articles', { params })
    
    if (response.data.code === 200) {
      const result = response.data.data
      articles.value = result.records || []
      total.value = result.total || 0
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
    ElMessage.error('加载文章列表失败')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await axios.get('/v1/categories')
    if (response.data.code === 200) {
      categories.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
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

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadArticles()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadArticles()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const refreshData = () => {
  loadArticles()
}

const editArticle = (id) => {
  router.push(`/admin/articles/edit/${id}`)
}

const viewArticle = (id) => {
  window.open(`/article/${id}`, '_blank')
}

const handleAction = async (command, row) => {
  switch (command) {
    case 'view':
      viewArticle(row.id)
      break
    case 'publish':
      await publishArticle(row.id)
      break
    case 'offline':
      await offlineArticle(row.id)
      break
    case 'top':
      await topArticle(row.id, 1)
      break
    case 'untop':
      await topArticle(row.id, 0)
      break
    case 'delete':
      await deleteArticle(row.id)
      break
  }
}

const publishArticle = async (id) => {
  try {
    await ElMessageBox.confirm('确定要发布这篇文章吗？', '发布确认', {
      type: 'warning'
    })

    const response = await axios.put(`/v1/articles/${id}/publish`)
    if (response.data.code === 200) {
      ElMessage.success('文章发布成功')
      loadArticles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布文章失败:', error)
      ElMessage.error('发布文章失败')
    }
  }
}

const offlineArticle = async (id) => {
  try {
    await ElMessageBox.confirm('确定要下线这篇文章吗？', '下线确认', {
      type: 'warning'
    })

    const response = await axios.put(`/v1/articles/${id}/offline`)
    if (response.data.code === 200) {
      ElMessage.success('文章下线成功')
      loadArticles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下线文章失败:', error)
      ElMessage.error('下线文章失败')
    }
  }
}

const topArticle = async (id, isTop) => {
  try {
    const response = await axios.put(`/v1/articles/${id}/top?isTop=${isTop}`)
    if (response.data.code === 200) {
      ElMessage.success(isTop ? '文章置顶成功' : '取消置顶成功')
      loadArticles()
    }
  } catch (error) {
    console.error('置顶操作失败:', error)
    ElMessage.error('置顶操作失败')
  }
}

const deleteArticle = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？删除后无法恢复！', '删除确认', {
      type: 'error'
    })

    const response = await axios.delete(`/v1/articles/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('文章删除成功')
      loadArticles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
  }
}

const batchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的文章')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 篇文章吗？删除后无法恢复！`,
      '批量删除确认',
      { type: 'error' }
    )

    const response = await axios.delete('/v1/articles/batch', {
      data: { ids: selectedIds.value }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadArticles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'info'
    default: return 'info'
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

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
}

// 生命周期
onMounted(() => {
  loadArticles()
  loadCategories()
})
</script>

<style scoped>
.article-management {
  padding: var(--space-6);
  background: var(--bg-primary);
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-6);
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.page-description {
  color: var(--text-secondary);
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
  padding: var(--space-4);
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.filter-left {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.search-input {
  width: 300px;
}

.filter-right {
  display: flex;
  gap: var(--space-3);
}

/* 表格区域 */
.table-section {
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.article-table {
  width: 100%;
}

.article-table :deep(.el-table__header) {
  background: var(--bg-secondary);
}

.article-table :deep(.el-table__row:hover) {
  background: var(--primary-50);
}

/* 文章信息 */
.article-info {
  display: flex;
  gap: var(--space-3);
  align-items: flex-start;
}

.article-cover {
  width: 80px;
  height: 60px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  color: var(--text-tertiary);
  font-size: var(--text-lg);
}

.article-details {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.article-title:hover {
  color: var(--primary-500);
}

.article-summary {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-2) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  gap: var(--space-2);
  align-items: center;
  flex-wrap: wrap;
}

.category-tag {
  background: var(--primary-100);
  color: var(--primary-700);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.tags {
  display: flex;
  gap: var(--space-1);
}

.tag-item {
  font-size: var(--text-xs);
}

/* 状态列 */
.top-badge {
  margin-top: var(--space-1);
  color: var(--warning-500);
  font-size: var(--text-sm);
}

/* 统计信息 */
.stats-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 时间信息 */
.time-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.publish-time {
  font-size: var(--text-sm);
  color: var(--text-primary);
}

.update-time {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

/* 分页 */
.pagination-section {
  padding: var(--space-4);
  display: flex;
  justify-content: center;
  background: var(--bg-elevated);
  border-top: 1px solid var(--border-secondary);
}

/* 空状态 */
.empty-state {
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--space-8);
}

.write-new-btn {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-6);
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  border: none;
  border-radius: var(--radius-xl);
  color: white;
  text-decoration: none;
  box-shadow: var(--shadow-lg);
  transition: all var(--duration-normal) var(--ease-out);
}

.write-new-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-700));
}

.write-new-btn .el-icon {
  font-size: var(--text-xl);
}

.btn-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.btn-content span {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  line-height: 1;
}

.btn-content small {
  font-size: var(--text-xs);
  opacity: 0.9;
  line-height: 1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .article-table :deep(.el-table__fixed-right) {
    display: none;
  }
}

@media (max-width: 768px) {
  .article-management {
    padding: var(--space-4);
  }
  
  .page-header {
    flex-direction: column;
    gap: var(--space-4);
    align-items: flex-start;
  }
  
  .filter-section {
    flex-direction: column;
    gap: var(--space-4);
    align-items: stretch;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
  
  .search-input {
    width: 100%;
  }
  
  .article-info {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .article-cover {
    width: 100%;
    height: 120px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: var(--space-1);
  }
}
</style>
