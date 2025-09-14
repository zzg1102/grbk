package org.example.ganggrbkbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.CommentQueryDTO;
import org.example.ganggrbkbackend.domain.dto.CommentSaveDTO;
import org.example.ganggrbkbackend.domain.entity.Comment;
import org.example.ganggrbkbackend.domain.vo.CommentVO;
import org.example.ganggrbkbackend.mapper.CommentMapper;
import org.example.ganggrbkbackend.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PageResult<CommentVO> pageComments(CommentQueryDTO queryDTO) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getArticleId() != null) {
            queryWrapper.eq(Comment::getArticleId, queryDTO.getArticleId());
        }

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.like(Comment::getContent, queryDTO.getKeyword());
        }

        if (queryDTO.getIsReview() != null) {
            queryWrapper.eq(Comment::getIsReview, queryDTO.getIsReview());
        }

        queryWrapper.eq(Comment::getDeleted, 0);
        queryWrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        List<CommentVO> commentVOList = commentPage.getRecords().stream()
                .map(comment -> {
                    CommentVO vo = new CommentVO();
                    BeanUtils.copyProperties(comment, vo);
                    return vo;
                }).collect(Collectors.toList());

        return new PageResult<CommentVO>(commentVOList, commentPage.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<CommentVO> getCommentsByArticleId(Long articleId) {
        List<CommentVO> parentComments = commentMapper.selectCommentsByArticleId(articleId);

        if (parentComments.isEmpty()) {
            return parentComments;
        }

        List<Long> parentIds = parentComments.stream()
                .map(CommentVO::getId)
                .collect(Collectors.toList());

        Map<Long, List<CommentVO>> replyMap = new HashMap<>();
        for (Long parentId : parentIds) {
            List<CommentVO> replies = commentMapper.selectRepliesByParentId(parentId);
            replyMap.put(parentId, replies);
        }

        for (CommentVO parentComment : parentComments) {
            parentComment.setReplyComments(replyMap.getOrDefault(parentComment.getId(), new ArrayList<>()));
        }

        return parentComments;
    }

    @Override
    public CommentVO getCommentById(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return null;
        }
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }

    @Override
    public Long saveComment(CommentSaveDTO saveDTO, Long userId, String ipAddress) {
        Comment comment = new Comment();
        comment.setArticleId(saveDTO.getArticleId());
        comment.setParentId(saveDTO.getParentId());
        comment.setContent(saveDTO.getContent());
        comment.setUserId(userId);
        comment.setIpAddress(ipAddress);
        comment.setIsReview(1); // 默认审核通过
        comment.setLikeCount(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setDeleted(1);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);

            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getParentId, id);
            queryWrapper.eq(Comment::getDeleted, 0);
            List<Comment> replyComments = commentMapper.selectList(queryWrapper);

            for (Comment replyComment : replyComments) {
                replyComment.setDeleted(1);
                replyComment.setUpdateTime(LocalDateTime.now());
                commentMapper.updateById(replyComment);
            }
        }
    }

    @Override
    public void reviewComment(Long id, Integer isReview) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setIsReview(isReview);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);
        }
    }

    @Override
    public void likeComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);
        }
    }
}