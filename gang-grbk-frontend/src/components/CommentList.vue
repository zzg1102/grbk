<template>
  <div class="comment-list">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-card shadow="hover">
        <template #header>
          <span>发表评论</span>
        </template>
        <div class="input-area">
          <el-input
            v-model="newComment.content"
            type="textarea"
            :rows="4"
            placeholder="写下您的评论..."
            maxlength="500"
            show-word-limit
            :disabled="!isLoggedIn"
          />
          <div class="input-actions">
            <div class="login-tip" v-if="!isLoggedIn">
              <el-text type="info">请先登录后再发表评论</el-text>
            </div>
            <el-button
              type="primary"
              @click="submitComment"
              :loading="submitting"
              :disabled="!newComment.content.trim() || !isLoggedIn"
            >
              发表评论
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 评论列表 -->
    <div class="comments" v-if="comments.length > 0">
      <el-card shadow="hover">
        <template #header>
          <span>评论列表 ({{ comments.length }})</span>
        </template>

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
      </el-card>
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
import { Star, ChatDotRound } from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({
  articleId: {
    type: [Number, String],
    required: true
  }
})

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

// 模拟登录状态，实际应从store获取
const isLoggedIn = computed(() => {
  // TODO: 从store获取用户登录状态
  return false
})

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
  margin-top: 2rem;
}

.comment-input {
  margin-bottom: 2rem;
}

.input-area {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-item {
  display: flex;
  gap: 1rem;
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
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
