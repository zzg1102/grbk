package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.ArticleTag;

import java.util.List;

/**
 * 文章标签关联Mapper
 *
 * @author Gang
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 根据文章ID查询标签ID列表
     */
    @Select("SELECT tag_id FROM tb_article_tag WHERE article_id = #{articleId}")
    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据标签ID查询文章ID列表
     */
    @Select("SELECT article_id FROM tb_article_tag WHERE tag_id = #{tagId}")
    List<Long> selectArticleIdsByTagId(@Param("tagId") Long tagId);

    /**
     * 删除文章的所有标签关联
     */
    @Delete("DELETE FROM tb_article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 删除标签的所有文章关联
     */
    @Delete("DELETE FROM tb_article_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);
}