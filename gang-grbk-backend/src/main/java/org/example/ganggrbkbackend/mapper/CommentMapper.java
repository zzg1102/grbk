package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.Comment;
import org.example.ganggrbkbackend.domain.vo.CommentVO;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE c.article_id = #{articleId} AND c.parent_id IS NULL AND c.deleted = 0 " +
            "ORDER BY c.create_time DESC")
    List<CommentVO> selectCommentsByArticleId(Long articleId);

    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE c.parent_id = #{parentId} AND c.deleted = 0 " +
            "ORDER BY c.create_time ASC")
    List<CommentVO> selectRepliesByParentId(Long parentId);

    @Select("SELECT COUNT(*) FROM tb_comment WHERE article_id = #{articleId} AND deleted = 0")
    Integer countCommentsByArticleId(Long articleId);
}