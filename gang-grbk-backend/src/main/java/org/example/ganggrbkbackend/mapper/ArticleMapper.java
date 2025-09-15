package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.ganggrbkbackend.domain.entity.Article;

/**
 * 文章Mapper
 *
 * @author Gang
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 更新文章评论数
     */
    void updateCommentCount(@Param("id") Long id, @Param("commentCount") Integer commentCount);

    /**
     * 更新文章点赞数
     */
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") Integer likeCount);

}