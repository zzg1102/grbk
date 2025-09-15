<template>
  <div class="article-editor-layout">
    <!-- 顶部导航栏 -->
    <header class="editor-header">
      <div class="header-left">
        <router-link to="/admin" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回管理</span>
        </router-link>
        <div class="editor-title">
          <h1>{{ isEdit ? '编辑文章' : '创建新文章' }}</h1>
          <div class="auto-save-status" v-if="autoSaveStatus">
            <el-icon><Check /></el-icon>
            <span>{{ autoSaveStatus }}</span>
          </div>
        </div>
      </div>
      
      <div class="header-actions">
        <el-button @click="saveDraft" :loading="saving" plain>
          <el-icon><Document /></el-icon>
          保存草稿
        </el-button>
        <el-button type="primary" @click="publishArticle" :loading="publishing">
          <el-icon><Promotion /></el-icon>
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </header>

    <!-- 主编辑区域 -->
    <main class="editor-main">
      <div class="editor-container">
        <!-- 左侧编辑区 -->
        <div class="editor-content">
          <!-- 文章标题 -->
          <div class="title-section">
            <el-input
              v-model="articleForm.title"
              placeholder="请输入文章标题..."
              size="large"
              maxlength="100"
              show-word-limit
              class="title-input"
              @input="handleAutoSave"
            />
          </div>

          <!-- Markdown编辑器 -->
          <div class="markdown-editor">
            <div class="editor-toolbar">
              <div class="toolbar-left">
                <el-button-group>
                  <el-button size="small" @click="insertMarkdown('**粗体**')">
                    <el-icon><Bold /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertMarkdown('*斜体*')">
                    <el-icon><Italic /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertMarkdown('`代码`')">
                    <el-icon><Code /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertMarkdown('[链接](url)')">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertMarkdown('![图片](url)')">
                    <el-icon><Picture /></el-icon>
                  </el-button>
                </el-button-group>
              </div>
              <div class="toolbar-right">
                <el-switch
                  v-model="showPreview"
                  active-text="预览"
                  inactive-text="编辑"
                />
              </div>
            </div>

            <div class="editor-body" :class="{ 'split-view': showPreview }">
              <div class="editor-pane" v-show="!showPreview || showPreview">
                <el-input
                  v-model="articleForm.content"
                  type="textarea"
                  placeholder="开始写作..."
                  :rows="25"
                  resize="none"
                  class="content-textarea"
                  @input="handleAutoSave"
                  ref="contentTextarea"
                />
              </div>
              <div class="preview-pane" v-show="showPreview">
                <div class="markdown-preview" v-html="previewHtml"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧设置区 -->
        <aside class="editor-sidebar">
          <!-- 发布设置 -->
          <div class="sidebar-card">
            <h3 class="card-title">发布设置</h3>
            <div class="card-content">
              <!-- 文章状态 -->
              <div class="form-item">
                <label>文章状态</label>
                <el-select v-model="articleForm.status" placeholder="选择状态">
                  <el-option label="草稿" :value="0" />
                  <el-option label="已发布" :value="1" />
                  <el-option label="已下线" :value="2" />
                </el-select>
              </div>

              <!-- 发布时间 -->
              <div class="form-item">
                <label>发布时间</label>
                <el-date-picker
                  v-model="articleForm.publishTime"
                  type="datetime"
                  placeholder="选择发布时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </div>

              <!-- 置顶设置 -->
              <div class="form-item">
                <el-checkbox v-model="articleForm.isTop">置顶文章</el-checkbox>
              </div>
            </div>
          </div>

          <!-- 分类和标签 -->
          <div class="sidebar-card">
            <h3 class="card-title">分类和标签</h3>
            <div class="card-content">
              <!-- 分类选择 -->
              <div class="form-item">
                <label>文章分类</label>
                <el-select v-model="articleForm.categoryId" placeholder="选择分类" filterable>
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </div>

              <!-- 标签选择 -->
              <div class="form-item">
                <label>文章标签</label>
                <el-select
                  v-model="articleForm.tagIds"
                  multiple
                  filterable
                  allow-create
                  placeholder="选择或创建标签"
                  @change="handleTagChange"
                >
                  <el-option
                    v-for="tag in tags"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                  />
                </el-select>
              </div>
            </div>
          </div>

          <!-- 封面图片 -->
          <div class="sidebar-card">
            <h3 class="card-title">封面图片</h3>
            <div class="card-content">
              <div class="cover-upload">
                <el-upload
                  :show-file-list="false"
                  :before-upload="beforeCoverUpload"
                  :http-request="handleCoverUpload"
                  accept="image/*"
                  class="cover-uploader"
                >
                  <div v-if="articleForm.coverImage" class="cover-preview">
                    <img :src="articleForm.coverImage" alt="封面图片" />
                    <div class="cover-overlay">
                      <el-icon><Edit /></el-icon>
                      <span>更换封面</span>
                    </div>
                  </div>
                  <div v-else class="cover-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传封面图片</span>
                  </div>
                </el-upload>
                <el-button v-if="articleForm.coverImage" @click="removeCover" size="small" type="danger" plain>
                  移除封面
                </el-button>
              </div>
            </div>
          </div>

          <!-- 文章摘要 -->
          <div class="sidebar-card">
            <h3 class="card-title">文章摘要</h3>
            <div class="card-content">
              <el-input
                v-model="articleForm.summary"
                type="textarea"
                :rows="4"
                placeholder="请输入文章摘要，留空将自动提取..."
                maxlength="200"
                show-word-limit
              />
            </div>
          </div>

          <!-- SEO设置 -->
          <div class="sidebar-card">
            <h3 class="card-title">SEO设置</h3>
            <div class="card-content">
              <div class="form-item">
                <label>SEO关键词</label>
                <el-input
                  v-model="articleForm.keywords"
                  placeholder="用逗号分隔多个关键词"
                />
              </div>
              <div class="form-item">
                <label>SEO描述</label>
                <el-input
                  v-model="articleForm.description"
                  type="textarea"
                  :rows="3"
                  placeholder="SEO描述"
                  maxlength="160"
                  show-word-limit
                />
              </div>
            </div>
          </div>
        </aside>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, Check, Document, Promotion, Bold, Italic, Code, Link, Picture,
  Edit, Plus
} from '@element-plus/icons-vue'
import { marked } from 'marked'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 响应式数据
const saving = ref(false)
const publishing = ref(false)
const showPreview = ref(false)
const autoSaveStatus = ref('')
const categories = ref([])
const tags = ref([])
const contentTextarea = ref(null)

// 判断是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 文章表单数据
const articleForm = reactive({
  id: null,
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  status: 0, // 0-草稿, 1-已发布, 2-已下线
  isTop: false,
  publishTime: null,
  keywords: '',
  description: ''
})

// Markdown预览
const previewHtml = computed(() => {
  return articleForm.content ? marked(articleForm.content) : '<p class="empty-preview">开始写作以查看预览...</p>'
})

// 自动保存定时器
let autoSaveTimer = null

// 方法
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

const loadTags = async () => {
  try {
    const response = await axios.get('/v1/tags')
    if (response.data.code === 200) {
      tags.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const loadArticle = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await axios.get(`/v1/articles/${route.params.id}`)
    if (response.data.code === 200) {
      const article = response.data.data
      Object.assign(articleForm, {
        id: article.id,
        title: article.title,
        content: article.content,
        summary: article.summary,
        coverImage: article.coverImage,
        categoryId: article.categoryId,
        tagIds: article.tags ? article.tags.map(tag => tag.id) : [],
        status: article.status,
        isTop: article.isTop === 1,
        publishTime: article.publishTime,
        keywords: article.keywords,
        description: article.description
      })
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

const handleAutoSave = () => {
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  autoSaveTimer = setTimeout(() => {
    saveDraft(true)
  }, 3000)
}

const saveDraft = async (isAutoSave = false) => {
  if (!articleForm.title.trim()) {
    if (!isAutoSave) {
      ElMessage.warning('请输入文章标题')
    }
    return
  }

  saving.value = true
  
  try {
    const formData = {
      ...articleForm,
      status: 0, // 强制设为草稿状态
      isTop: articleForm.isTop ? 1 : 0
    }

    let response
    if (isEdit.value && articleForm.id) {
      response = await axios.put(`/v1/articles/${articleForm.id}`, formData)
    } else {
      response = await axios.post('/v1/articles', formData)
      if (response.data.code === 200) {
        articleForm.id = response.data.data
        // 更新URL为编辑模式
        router.replace(`/admin/articles/edit/${articleForm.id}`)
      }
    }

    if (response.data.code === 200) {
      if (isAutoSave) {
        autoSaveStatus.value = '已自动保存'
        setTimeout(() => {
          autoSaveStatus.value = ''
        }, 2000)
      } else {
        ElMessage.success('草稿保存成功')
      }
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    if (!isAutoSave) {
      ElMessage.error('保存草稿失败')
    }
  } finally {
    saving.value = false
  }
}

const publishArticle = async () => {
  if (!articleForm.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!articleForm.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  if (!articleForm.categoryId) {
    ElMessage.warning('请选择文章分类')
    return
  }

  publishing.value = true

  try {
    const formData = {
      ...articleForm,
      status: 1, // 设为已发布状态
      isTop: articleForm.isTop ? 1 : 0,
      publishTime: articleForm.publishTime || new Date().toISOString().slice(0, 19).replace('T', ' ')
    }

    let response
    if (isEdit.value && articleForm.id) {
      response = await axios.put(`/v1/articles/${articleForm.id}`, formData)
    } else {
      response = await axios.post('/v1/articles', formData)
    }

    if (response.data.code === 200) {
      ElMessage.success(isEdit.value ? '文章更新成功' : '文章发布成功')
      router.push('/admin/articles')
    }
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布文章失败')
  } finally {
    publishing.value = false
  }
}

const insertMarkdown = (markdown) => {
  const textarea = contentTextarea.value?.$refs?.textarea || contentTextarea.value?.textarea
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = articleForm.content.substring(start, end)
  
  let insertText = markdown
  if (selectedText && markdown.includes('**')) {
    insertText = `**${selectedText}**`
  } else if (selectedText && markdown.includes('*')) {
    insertText = `*${selectedText}*`
  } else if (selectedText && markdown.includes('`')) {
    insertText = `\`${selectedText}\``
  }

  articleForm.content = 
    articleForm.content.substring(0, start) + 
    insertText + 
    articleForm.content.substring(end)

  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start + insertText.length, start + insertText.length)
  })
}

const handleTagChange = (tagIds) => {
  // 处理新创建的标签
  tagIds.forEach(tagId => {
    if (typeof tagId === 'string' && !tags.value.find(tag => tag.id === tagId)) {
      // 这是一个新标签，需要创建
      tags.value.push({ id: tagId, name: tagId })
    }
  })
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleCoverUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const response = await axios.post('/v1/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.data.code === 200) {
      articleForm.coverImage = response.data.data.url
      ElMessage.success('封面上传成功')
    }
  } catch (error) {
    console.error('封面上传失败:', error)
    ElMessage.error('封面上传失败')
  }
}

const removeCover = () => {
  articleForm.coverImage = ''
}

// 生命周期
onMounted(() => {
  loadCategories()
  loadTags()
  loadArticle()
})

// 监听路由变化
watch(() => route.params.id, () => {
  if (route.params.id) {
    loadArticle()
  } else {
    // 重置表单
    Object.assign(articleForm, {
      id: null,
      title: '',
      content: '',
      summary: '',
      coverImage: '',
      categoryId: null,
      tagIds: [],
      status: 0,
      isTop: false,
      publishTime: null,
      keywords: '',
      description: ''
    })
  }
})

// 页面离开前提醒保存
window.addEventListener('beforeunload', (e) => {
  if (articleForm.title || articleForm.content) {
    e.preventDefault()
    e.returnValue = '您有未保存的更改，确定要离开吗？'
  }
})
</script>

<style scoped>
.article-editor-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

/* 顶部导航栏 */
.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-6);
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-secondary);
  box-shadow: var(--shadow-sm);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: all var(--duration-fast) var(--ease-out);
}

.back-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.editor-title h1 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.auto-save-status {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--success-500);
  font-size: var(--text-sm);
  margin-top: var(--space-1);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* 主编辑区域 */
.editor-main {
  flex: 1;
  overflow: hidden;
}

.editor-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  height: 100%;
}

.editor-content {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.title-section {
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-secondary);
}

.title-input {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
}

.title-input :deep(.el-input__inner) {
  border: none;
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  padding: 0;
  background: transparent;
}

.title-input :deep(.el-input__inner):focus {
  box-shadow: none;
}

/* Markdown编辑器 */
.markdown-editor {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-6);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.editor-body {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr;
  overflow: hidden;
}

.editor-body.split-view {
  grid-template-columns: 1fr 1fr;
}

.editor-pane,
.preview-pane {
  overflow: auto;
}

.editor-pane {
  background: var(--bg-elevated);
}

.content-textarea :deep(.el-textarea__inner) {
  border: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--text-base);
  line-height: 1.6;
  padding: var(--space-6);
  background: transparent;
}

.content-textarea :deep(.el-textarea__inner):focus {
  box-shadow: none;
}

.preview-pane {
  background: var(--bg-primary);
  border-left: 1px solid var(--border-secondary);
}

.markdown-preview {
  padding: var(--space-6);
  line-height: 1.7;
  color: var(--text-primary);
}

.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin-top: var(--space-6);
  margin-bottom: var(--space-4);
  font-weight: var(--font-semibold);
}

.markdown-preview :deep(p) {
  margin-bottom: var(--space-4);
}

.markdown-preview :deep(code) {
  background: var(--bg-secondary);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.markdown-preview :deep(pre) {
  background: var(--bg-secondary);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  overflow-x: auto;
  margin-bottom: var(--space-4);
}

.empty-preview {
  color: var(--text-tertiary);
  text-align: center;
  margin-top: var(--space-12);
}

/* 右侧边栏 */
.editor-sidebar {
  background: var(--bg-elevated);
  border-left: 1px solid var(--border-secondary);
  overflow-y: auto;
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.sidebar-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.card-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.card-content {
  padding: var(--space-4);
}

.form-item {
  margin-bottom: var(--space-4);
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-item label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

/* 封面上传 */
.cover-upload {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
}

.cover-preview {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  color: white;
  opacity: 0;
  transition: opacity var(--duration-fast) var(--ease-out);
}

.cover-preview:hover .cover-overlay {
  opacity: 1;
}

.cover-placeholder {
  width: 100%;
  aspect-ratio: 16 / 9;
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.cover-placeholder:hover {
  border-color: var(--primary-500);
  color: var(--primary-500);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .editor-container {
    grid-template-columns: 1fr;
  }
  
  .editor-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .editor-header {
    padding: var(--space-3);
    flex-direction: column;
    gap: var(--space-3);
  }
  
  .header-left,
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .editor-body.split-view {
    grid-template-columns: 1fr;
  }
  
  .preview-pane {
    display: none;
  }
  
  .title-section {
    padding: var(--space-4);
  }
  
  .editor-toolbar {
    padding: var(--space-2) var(--space-4);
  }
  
  .content-textarea :deep(.el-textarea__inner) {
    padding: var(--space-4);
  }
}
</style>
