package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.Category;

import java.util.List;

/**
 * 分类Mapper
 *
 * @author Gang
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT c.*, COUNT(a.id) as article_count " +
            "FROM tb_category c " +
            "LEFT JOIN tb_article a ON c.id = a.category_id AND a.deleted = 0 " +
            "WHERE c.deleted = 0 " +
            "GROUP BY c.id " +
            "ORDER BY c.sort_order ASC, c.create_time DESC")
    List<Category> selectCategoriesWithArticleCount();

    @Select("SELECT COUNT(*) FROM tb_article WHERE category_id = #{categoryId} AND deleted = 0")
    Integer countArticlesByCategoryId(Long categoryId);
}