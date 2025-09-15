<template>
  <div class="write-article-layout">
    <!-- 简约顶部栏 -->
    <header class="write-header">
      <div class="header-left">
        <router-link to="/" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回首页</span>
        </router-link>
      </div>
      
      <div class="header-center">
        <div class="save-status" v-if="saveStatus">
          <el-icon><Check /></el-icon>
          <span>{{ saveStatus }}</span>
        </div>
      </div>
      
      <div class="header-right">
        <el-button @click="saveDraft" :loading="saving" text>
          保存草稿
        </el-button>
        <el-button type="primary" @click="publishArticle" :loading="publishing">
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </header>

    <!-- 主编辑区域 -->
    <main class="write-main">
      <div class="write-container">
        <!-- 文章标题 -->
        <div class="title-section">
          <textarea
            v-model="articleForm.title"
            placeholder="请输入文章标题..."
            class="title-input"
            maxlength="100"
            rows="1"
            @input="handleTitleInput"
            @keydown.enter.prevent
          />
          <div class="title-count">{{ articleForm.title.length }}/100</div>
        </div>

        <!-- 文章摘要 -->
        <div class="summary-section">
          <textarea
            v-model="articleForm.summary"
            placeholder="写一段简短的摘要来介绍这篇文章..."
            class="summary-input"
            maxlength="200"
            rows="2"
            @input="handleAutoSave"
          />
          <div class="summary-count">{{ articleForm.summary.length }}/200</div>
        </div>

        <!-- 封面图片 -->
        <div class="cover-section" v-if="showCover || articleForm.coverImage">
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
                <div class="cover-actions">
                  <el-button size="small" @click.stop="removeCover">移除</el-button>
                </div>
              </div>
              <div v-else class="cover-placeholder">
                <el-icon><Picture /></el-icon>
                <span>点击上传封面图片</span>
              </div>
            </el-upload>
          </div>
        </div>

        <!-- Markdown编辑器 -->
        <div class="editor-section">
          <div class="editor-toolbar">
            <div class="toolbar-left">
              <el-button-group size="small">
                <el-button @click="insertMarkdown('**', '**')" title="粗体">
                  <strong>B</strong>
                </el-button>
                <el-button @click="insertMarkdown('*', '*')" title="斜体">
                  <em>I</em>
                </el-button>
                <el-button @click="insertMarkdown('`', '`')" title="代码">
                  <span style="font-family: monospace; font-weight: bold;">&lt;/&gt;</span>
                </el-button>
                <el-button @click="insertMarkdown('[', '](url)')" title="链接">
                  <el-icon><Link /></el-icon>
                </el-button>
                <el-button @click="showCover = !showCover" title="封面图片">
                  <el-icon><Picture /></el-icon>
                </el-button>
              </el-button-group>
            </div>
            <div class="toolbar-right">
              <span class="word-count">{{ wordCount }} 字</span>
              <el-switch
                v-model="showPreview"
                active-text="预览"
                inactive-text="编辑"
                size="small"
              />
            </div>
          </div>

          <div class="editor-body" :class="{ 'preview-mode': showPreview }">
            <div class="editor-pane" v-show="!showPreview">
              <textarea
                v-model="articleForm.content"
                placeholder="开始写作..."
                class="content-textarea"
                @input="handleContentInput"
                @keydown="handleKeydown"
                ref="contentTextarea"
              />
            </div>
            <div class="preview-pane" v-show="showPreview">
              <div class="markdown-preview" v-html="previewHtml"></div>
            </div>
          </div>
        </div>

        <!-- 文章设置 -->
        <div class="settings-section">
          <div class="settings-row">
            <div class="setting-item">
              <label>分类</label>
              <el-select v-model="articleForm.categoryId" placeholder="选择分类" size="small">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </div>
            <div class="setting-item">
              <label>标签</label>
              <el-select
                v-model="articleForm.tagIds"
                multiple
                filterable
                allow-create
                placeholder="选择或创建标签"
                size="small"
                style="min-width: 200px"
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
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, Check, Link, Picture
} from '@element-plus/icons-vue'
import { marked } from 'marked'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 响应式数据
const saving = ref(false)
const publishing = ref(false)
const showPreview = ref(false)
const showCover = ref(false)
const saveStatus = ref('')
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
  status: 0 // 0-草稿, 1-已发布
})

// 字数统计
const wordCount = computed(() => {
  return articleForm.content.replace(/\s/g, '').length
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
    const response = await axios.get('/api/v1/categories')
    if (response.data.code === 200) {
      categories.value = response.data.data || []
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
    }
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const loadArticle = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await axios.get(`/api/v1/articles/${route.params.id}`)
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
        status: article.status
      })
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

const handleTitleInput = () => {
  // 自动调整标题输入框高度
  const textarea = event.target
  textarea.style.height = 'auto'
  textarea.style.height = textarea.scrollHeight + 'px'
  handleAutoSave()
}

const handleContentInput = () => {
  handleAutoSave()
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

  saving.value = !isAutoSave
  
  try {
    const formData = {
      ...articleForm,
      status: 0 // 强制设为草稿状态
    }

    let response
    if (isEdit.value && articleForm.id) {
      response = await axios.put(`/api/v1/articles/${articleForm.id}`, formData)
    } else {
      response = await axios.post('/api/v1/articles', formData)
      if (response.data.code === 200) {
        articleForm.id = response.data.data
        // 更新URL为编辑模式
        router.replace(`/write/${articleForm.id}`)
      }
    }

    if (response.data.code === 200) {
      if (isAutoSave) {
        saveStatus.value = '已自动保存'
        setTimeout(() => {
          saveStatus.value = ''
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
      status: 1 // 设为已发布状态
    }

    let response
    if (isEdit.value && articleForm.id) {
      response = await axios.put(`/api/v1/articles/${articleForm.id}`, formData)
    } else {
      response = await axios.post('/api/v1/articles', formData)
    }

    if (response.data.code === 200) {
      ElMessage.success(isEdit.value ? '文章更新成功' : '文章发布成功')
      router.push('/articles')
    }
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布文章失败')
  } finally {
    publishing.value = false
  }
}

const insertMarkdown = (before, after = '') => {
  const textarea = contentTextarea.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = articleForm.content.substring(start, end)
  
  const insertText = before + selectedText + after

  articleForm.content = 
    articleForm.content.substring(0, start) + 
    insertText + 
    articleForm.content.substring(end)

  nextTick(() => {
    textarea.focus()
    const newPos = start + before.length + selectedText.length
    textarea.setSelectionRange(newPos, newPos)
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
    const response = await axios.post('/api/v1/files/upload', formData, {
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
  showCover.value = false
}

const handleKeydown = (e) => {
  // Tab键插入缩进
  if (e.key === 'Tab') {
    e.preventDefault()
    const start = e.target.selectionStart
    const end = e.target.selectionEnd
    
    articleForm.content = 
      articleForm.content.substring(0, start) + 
      '  ' + 
      articleForm.content.substring(end)
    
    nextTick(() => {
      e.target.setSelectionRange(start + 2, start + 2)
    })
  }
}

// 处理URL查询参数
const initializeFromQuery = () => {
  const query = route.query
  
  if (query.categoryId && !isEdit.value) {
    articleForm.categoryId = parseInt(query.categoryId)
    console.log('预选分类:', query.categoryId)
  }
}

// 生命周期
onMounted(() => {
  loadCategories()
  loadTags()
  loadArticle()
  
  // 处理查询参数
  initializeFromQuery()
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
      status: 0
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
.write-article-layout {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
}

/* 顶部栏 */
.write-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-6);
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-secondary);
  position: sticky;
  top: 0;
  z-index: 100;
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

.save-status {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--success-500);
  font-size: var(--text-sm);
}

.header-right {
  display: flex;
  gap: var(--space-3);
}

/* 主编辑区域 */
.write-main {
  flex: 1;
  overflow: auto;
}

.write-container {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-6);
}

/* 标题区域 */
.title-section {
  position: relative;
  margin-bottom: var(--space-8);
}

.title-input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1.2;
  resize: none;
  overflow: hidden;
  min-height: 60px;
}

.title-input::placeholder {
  color: var(--text-tertiary);
}

.title-count {
  position: absolute;
  bottom: -20px;
  right: 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 摘要区域 */
.summary-section {
  position: relative;
  margin-bottom: var(--space-8);
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border-left: 4px solid var(--primary-500);
}

.summary-input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--text-lg);
  color: var(--text-secondary);
  line-height: 1.6;
  resize: none;
  overflow: hidden;
}

.summary-input::placeholder {
  color: var(--text-tertiary);
}

.summary-count {
  position: absolute;
  bottom: 8px;
  right: 12px;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 封面区域 */
.cover-section {
  margin-bottom: var(--space-8);
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
}

.cover-preview {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.cover-preview img {
  width: 100%;
  height: auto;
  display: block;
}

.cover-actions {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-12) var(--space-6);
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.cover-placeholder:hover {
  border-color: var(--primary-500);
  color: var(--primary-500);
}

.cover-placeholder .el-icon {
  font-size: var(--text-4xl);
}

/* 编辑器区域 */
.editor-section {
  margin-bottom: var(--space-8);
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) 0;
  margin-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-secondary);
}

.word-count {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin-right: var(--space-4);
}

.editor-body {
  position: relative;
  min-height: 500px;
}

.editor-body.preview-mode .editor-pane {
  display: none;
}

.content-textarea {
  width: 100%;
  height: 500px;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--text-lg);
  color: var(--text-primary);
  line-height: 1.7;
  resize: vertical;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.content-textarea::placeholder {
  color: var(--text-tertiary);
}

.preview-pane {
  min-height: 500px;
  padding: var(--space-4);
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-secondary);
}

.markdown-preview {
  color: var(--text-primary);
  line-height: 1.7;
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

/* 设置区域 */
.settings-section {
  border-top: 1px solid var(--border-secondary);
  padding-top: var(--space-6);
}

.settings-row {
  display: flex;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.setting-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  min-width: 150px;
}

.setting-item label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .write-container {
    padding: var(--space-6) var(--space-4);
  }
  
  .title-input {
    font-size: var(--text-3xl);
  }
  
  .summary-input {
    font-size: var(--text-base);
  }
  
  .content-textarea {
    font-size: var(--text-base);
  }
  
  .settings-row {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .write-header {
    padding: var(--space-3) var(--space-4);
    flex-wrap: wrap;
    gap: var(--space-3);
  }
  
  .header-right {
    order: 3;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
