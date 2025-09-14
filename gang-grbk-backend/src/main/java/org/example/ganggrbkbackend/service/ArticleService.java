package org.example.ganggrbkbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.ArticleQueryDTO;
import org.example.ganggrbkbackend.domain.dto.ArticleSaveDTO;
import org.example.ganggrbkbackend.domain.entity.Article;
import org.example.ganggrbkbackend.domain.vo.ArticleDetailVO;
import org.example.ganggrbkbackend.domain.vo.ArticleListVO;

import java.util.List;

/**
 * 文章服务接口
 *
 * @author Gang
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章
     */
    PageResult<ArticleListVO> pageArticles(ArticleQueryDTO queryDTO);

    /**
     * 根据ID查询文章详情
     */
    ArticleDetailVO getArticleDetail(Long id);

    /**
     * 保存文章
     */
    Long saveArticle(ArticleSaveDTO saveDTO);

    /**
     * 更新文章
     */
    void updateArticle(ArticleSaveDTO saveDTO);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

    /**
     * 发布文章
     */
    void publishArticle(Long id);

    /**
     * 下线文章
     */
    void offlineArticle(Long id);

    /**
     * 置顶/取消置顶文章
     */
    void topArticle(Long id, Integer isTop);

    /**
     * 查询热门文章
     */
    List<ArticleListVO> getHotArticles(Integer limit);

    /**
     * 查询最新文章
     */
    List<ArticleListVO> getLatestArticles(Integer limit);

    /**
     * 增加文章浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 增加文章点赞数
     */
    void incrementLikeCount(Long id);
}