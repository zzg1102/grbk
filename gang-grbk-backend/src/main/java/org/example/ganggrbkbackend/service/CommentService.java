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
}