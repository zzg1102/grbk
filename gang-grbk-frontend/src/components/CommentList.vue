<template>
  <div class="comment-list">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <div class="comment-form">
        <div class="form-header">
          <h4 class="form-title">
            <el-icon><EditPen /></el-icon>
            <span>发表评论</span>
          </h4>
        </div>
        <div class="input-area">
          <div class="input-wrapper">
            <textarea
              v-model="newComment.content"
              class="comment-textarea"
              placeholder="写下您的评论..."
              maxlength="500"
              :disabled="!isLoggedIn"
              rows="4"
            ></textarea>
            <div class="textarea-footer">
              <span class="char-count">{{ newComment.content.length }}/500</span>
            </div>
          </div>
          <div class="input-actions">
            <div class="login-tip" v-if="!isLoggedIn">
              <el-icon><InfoFilled /></el-icon>
              <span>请先登录后再发表评论</span>
            </div>
            <button
              class="submit-btn"
              @click="submitComment"
              :disabled="!newComment.content.trim() || !isLoggedIn || submitting"
              :class="{ loading: submitting }"
            >
              <el-icon v-if="submitting"><Loading /></el-icon>
              <el-icon v-else><Check /></el-icon>
              <span>{{ submitting ? '发表中...' : '发表评论' }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments" v-if="comments.length > 0">
      <div class="comments-container">
        <div class="comments-header">
          <h4 class="comments-title">
            <el-icon><ChatDotRound /></el-icon>
            <span>全部评论</span>
          </h4>
          <span class="comments-count">{{ comments.length }} 条</span>
        </div>

        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-avatar">
            <el-avatar :src="comment.avatar" :size="40">
              {{ comment.nickname?.charAt(0) || comment.username?.charAt(0) }}
            </el-avatar>
          </div>

          <div class="comment-content">
            <div class="comment-header">
              <span class="author-name">
                {{ comment.nickname || comment.username }}
                <el-tag v-if="comment.isAuthor" size="small" type="warning">作者</el-tag>
              </span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>

            <div class="comment-text">{{ comment.content }}</div>

            <div class="comment-actions">
              <el-button
                size="small"
                text
                @click="likeComment(comment)"
                :loading="comment.liking"
              >
                <el-icon><Star /></el-icon>
                {{ comment.likeCount || 0 }}
              </el-button>

              <el-button
                size="small"
                text
                @click="replyToComment(comment)"
                v-if="isLoggedIn"
              >
                <el-icon><ChatDotRound /></el-icon>
                回复
              </el-button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="reply-input">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="3"
                :placeholder="`回复 ${comment.nickname || comment.username}:`"
                maxlength="500"
                show-word-limit
              />
              <div class="reply-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button
                  size="small"
                  type="primary"
                  @click="submitReply(comment)"
                  :loading="replying"
                  :disabled="!replyContent.trim()"
                >
                  回复
                </el-button>
              </div>
            </div>

            <!-- 子评论列表 -->
            <div v-if="comment.replyComments && comment.replyComments.length > 0" class="reply-list">
              <div
                v-for="reply in comment.replyComments"
                :key="reply.id"
                class="reply-item"
              >
                <div class="reply-avatar">
                  <el-avatar :src="reply.avatar" :size="32">
                    {{ reply.nickname?.charAt(0) || reply.username?.charAt(0) }}
                  </el-avatar>
                </div>
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="author-name">
                      {{ reply.nickname || reply.username }}
                      <el-tag v-if="reply.isAuthor" size="small" type="warning">作者</el-tag>
                    </span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="reply-text">{{ reply.content }}</div>
                  <div class="reply-actions">
                    <el-button
                      size="small"
                      text
                      @click="likeComment(reply)"
                      :loading="reply.liking"
                    >
                      <el-icon><Star /></el-icon>
                      {{ reply.likeCount || 0 }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 暂无评论 -->
    <div v-else class="no-comments">
      <el-empty description="暂无评论，来发表第一个评论吧！" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, ChatDotRound, EditPen, InfoFilled, Loading, Check } from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({
  articleId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['comment-updated'])

// 响应式数据
const comments = ref([])
const loading = ref(false)
const submitting = ref(false)
const replying = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')

const newComment = ref({
  articleId: props.articleId,
  content: ''
})

// 检查用户登录状态
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('auth_token')
  const userInfo = localStorage.getItem('user_info')
  return !!(token && userInfo)
})

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('user_info')
  if (userInfoStr) {
    try {
      return JSON.parse(userInfoStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      return null
    }
  }
  return null
}

// 加载评论列表
const loadComments = async () => {
  try {
    loading.value = true
    const response = await axios.get(`/api/v1/comments/article/${props.articleId}`)
    if (response.data.code === 200) {
      comments.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.content.trim()) {
    return
  }

  const token = localStorage.getItem('auth_token')
  if (!token) {
    ElMessage.error('请先登录')
    return
  }

  try {
    submitting.value = true
    const response = await axios.post('/api/v1/comments', {
      articleId: props.articleId,
      content: newComment.value.content.trim()
    })

    if (response.data.code === 200) {
      ElMessage.success('评论发表成功')
      newComment.value.content = ''
      await loadComments()
      // 通知父组件更新文章数据
      emit('comment-updated')
    } else {
      ElMessage.error(response.data.message || '评论发表失败')
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  } finally {
    submitting.value = false
  }
}

// 回复评论
const replyToComment = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = async (parentComment) => {
  if (!replyContent.value.trim()) {
    return
  }

  const token = localStorage.getItem('auth_token')
  if (!token) {
    ElMessage.error('请先登录')
    return
  }

  try {
    replying.value = true
    const response = await axios.post('/api/v1/comments', {
      articleId: props.articleId,
      parentId: parentComment.id,
      content: replyContent.value.trim()
    })

    if (response.data.code === 200) {
      ElMessage.success('回复发表成功')
      cancelReply()
      await loadComments()
      // 通知父组件更新文章数据
      emit('comment-updated')
    } else {
      ElMessage.error(response.data.message || '回复发表失败')
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    ElMessage.error('发表回复失败')
  } finally {
    replying.value = false
  }
}

// 点赞评论
const likeComment = async (comment) => {
  const token = localStorage.getItem('auth_token')
  if (!token) {
    ElMessage.error('请先登录')
    return
  }

  try {
    comment.liking = true
    const response = await axios.post(`/api/v1/comments/${comment.id}/like`)

    if (response.data.code === 200) {
      comment.likeCount = (comment.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    } else {
      ElMessage.error(response.data.message || '点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  } finally {
    comment.liking = false
  }
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
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

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-list {
  width: 100%;
}

.comment-input {
  margin-bottom: var(--space-6);
}

.comment-form {
  background: var(--bg-elevated);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.form-header {
  padding: var(--space-4) var(--space-6);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.form-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.form-title .el-icon {
  font-size: var(--text-lg);
  color: var(--primary-500);
}

.input-area {
  padding: var(--space-6);
}

.input-wrapper {
  margin-bottom: var(--space-4);
}

.comment-textarea {
  width: 100%;
  min-height: 120px;
  padding: var(--space-4);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: var(--text-base);
  line-height: 1.6;
  resize: vertical;
  transition: all var(--duration-fast) var(--ease-out);
  outline: none;
}

.comment-textarea:focus {
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(var(--primary-500-rgb), 0.1);
}

.comment-textarea:disabled {
  background: var(--bg-secondary);
  color: var(--text-tertiary);
  cursor: not-allowed;
}

.comment-textarea::placeholder {
  color: var(--text-tertiary);
}

.textarea-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--space-2);
}

.char-count {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
}

.login-tip {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--info);
  font-size: var(--text-sm);
}

.login-tip .el-icon {
  font-size: var(--text-base);
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  background: var(--primary-500);
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-600);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(var(--primary-500-rgb), 0.3);
}

.submit-btn:disabled {
  background: var(--bg-tertiary);
  color: var(--text-tertiary);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.submit-btn.loading {
  cursor: wait;
}

/* 评论列表样式 */
.comments-container {
  background: var(--bg-elevated);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.comments-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-6);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-secondary);
}

.comments-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.comments-title .el-icon {
  font-size: var(--text-lg);
  color: var(--success-500);
}

.comments-count {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  background: var(--bg-elevated);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  border: 1px solid var(--border-primary);
}

.comment-item {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-secondary);
  transition: background var(--duration-fast) var(--ease-out);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-item:hover {
  background: var(--bg-secondary);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.author-name {
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comment-time {
  color: #999;
  font-size: 0.875rem;
}

.comment-text {
  margin-bottom: 0.5rem;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 1rem;
}

.reply-input {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #fafafa;
  border-radius: 6px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.reply-list {
  margin-top: 1rem;
  padding-left: 1rem;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f5f5f5;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.reply-time {
  color: #999;
  font-size: 0.75rem;
}

.reply-text {
  margin-bottom: 0.5rem;
  line-height: 1.4;
  font-size: 0.875rem;
}

.reply-actions {
  display: flex;
  gap: 1rem;
}

.no-comments {
  margin: 2rem 0;
}

@media (max-width: 768px) {
  .comment-item,
  .reply-item {
    flex-direction: column;
    gap: 0.5rem;
  }

  .comment-header,
  .reply-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
}
</style>
