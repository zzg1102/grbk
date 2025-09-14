package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.ganggrbkbackend.domain.entity.Article;

/**
 * 文章Mapper
 *
 * @author Gang
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    // 暂时只保留基础的CRUD操作，复杂查询后续添加

}