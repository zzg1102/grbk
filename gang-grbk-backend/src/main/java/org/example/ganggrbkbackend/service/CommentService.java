package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.CommentQueryDTO;
import org.example.ganggrbkbackend.domain.dto.CommentSaveDTO;
import org.example.ganggrbkbackend.domain.vo.CommentVO;

import java.util.List;

public interface CommentService {

    PageResult<CommentVO> pageComments(CommentQueryDTO queryDTO);

    List<CommentVO> getCommentsByArticleId(Long articleId);

    CommentVO getCommentById(Long id);

    Long saveComment(CommentSaveDTO saveDTO, Long userId, String ipAddress);

    void deleteComment(Long id);

    void reviewComment(Long id, Integer isReview);

    void likeComment(Long id);
    
    /**
     * 批量更新所有文章的评论数量
     */
    void updateAllArticleCommentCount();
    
    /**
     * 更新指定文章的评论数量
     */
    void updateArticleCommentCount(Long articleId);
}