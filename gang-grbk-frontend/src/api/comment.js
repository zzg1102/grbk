import axios from 'axios'

const API_BASE = '/api/comment'

export const commentApi = {
  // 根据文章ID获取评论列表
  getCommentsByArticleId(articleId) {
    return axios.get(`${API_BASE}/article/${articleId}`)
  },

  // 发表评论
  saveComment(data) {
    return axios.post(API_BASE, data)
  },

  // 点赞评论
  likeComment(commentId) {
    return axios.post(`${API_BASE}/${commentId}/like`)
  },

  // 分页查询评论（管理员用）
  pageComments(data) {
    return axios.post(`${API_BASE}/page`, data)
  },

  // 删除评论（管理员用）
  deleteComment(commentId) {
    return axios.delete(`${API_BASE}/${commentId}`)
  },

  // 审核评论（管理员用）
  reviewComment(commentId, isReview) {
    return axios.put(`${API_BASE}/${commentId}/review`, null, {
      params: { isReview }
    })
  }
}