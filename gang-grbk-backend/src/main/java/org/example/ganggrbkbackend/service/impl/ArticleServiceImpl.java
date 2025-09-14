package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.constant.SystemConstants;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.common.exception.ResultCodeEnum;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.ArticleQueryDTO;
import org.example.ganggrbkbackend.domain.dto.ArticleSaveDTO;
import org.example.ganggrbkbackend.domain.entity.Article;
import org.example.ganggrbkbackend.domain.vo.ArticleDetailVO;
import org.example.ganggrbkbackend.domain.vo.ArticleListVO;
import org.example.ganggrbkbackend.mapper.ArticleMapper;
import org.example.ganggrbkbackend.service.ArticleService;
import org.example.ganggrbkbackend.service.MessageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author Gang
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;
    private final MessageService messageService;
    private final CacheService cacheService;

    @Override
    public PageResult<ArticleListVO> pageArticles(ArticleQueryDTO queryDTO) {
        // 临时实现，使用基础查询
        Page<Article> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<Article> pageResult = articleMapper.selectPage(page, null);

        // 转换为VO（简化实现）
        List<ArticleListVO> records = pageResult.getRecords().stream()
                .map(article -> {
                    ArticleListVO vo = new ArticleListVO();
                    BeanUtil.copyProperties(article, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(
                records,
                pageResult.getTotal(),
                pageResult.getSize(),
                pageResult.getCurrent()
        );
    }

    @Override
    @Cacheable(value = "articles", key = "#id")
    public ArticleDetailVO getArticleDetail(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 异步增加浏览量到Redis
        cacheService.incrementViewCount(id);

        // 简化实现，直接转换
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtil.copyProperties(article, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "articles", key = "#result")
    public Long saveArticle(ArticleSaveDTO saveDTO) {
        Article article = new Article();
        BeanUtil.copyProperties(saveDTO, article);

        // 设置作者为当前登录用户（暂时设为管理员）
        article.setAuthorId(SystemConstants.SUPER_ADMIN_ID);

        // 如果是发布状态，设置发布时间
        if (SystemConstants.ARTICLE_STATUS_PUBLISHED == saveDTO.getStatus()) {
            article.setPublishTime(LocalDateTime.now());
        }

        // 设置默认值
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        if (article.getLikeCount() == null) {
            article.setLikeCount(0);
        }
        if (article.getCommentCount() == null) {
            article.setCommentCount(0);
        }
        if (article.getIsTop() == null) {
            article.setIsTop(0);
        }

        this.save(article);

        // 处理标签关联（这里先简化处理，后续实现标签功能时补充）
        if (CollUtil.isNotEmpty(saveDTO.getTagIds())) {
            // TODO: 保存文章标签关联
        }

        // 清除相关缓存
        cacheService.evictAllArticlesCache();
        cacheService.clearCacheByPattern("hot_articles*");
        cacheService.clearCacheByPattern("category_articles:*");

        // 如果是发布状态的新文章，发送消息到RabbitMQ
        if (article.getId() != null && SystemConstants.ARTICLE_STATUS_PUBLISHED == saveDTO.getStatus()) {
            messageService.sendArticleCreateMessage(article.getId(), article.getTitle());
        }

        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "articles", key = "#saveDTO.id")
    public void updateArticle(ArticleSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("文章ID不能为空");
        }

        Article existArticle = this.getById(saveDTO.getId());
        if (existArticle == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        Article article = new Article();
        BeanUtil.copyProperties(saveDTO, article);

        // 如果从草稿改为发布状态，设置发布时间
        if (SystemConstants.ARTICLE_STATUS_PUBLISHED != existArticle.getStatus() &&
                SystemConstants.ARTICLE_STATUS_PUBLISHED == saveDTO.getStatus()) {
            article.setPublishTime(LocalDateTime.now());
        }

        this.updateById(article);

        // 清除相关缓存
        cacheService.evictAllArticlesCache();
        cacheService.clearCacheByPattern("hot_articles*");
        cacheService.clearCacheByPattern("category_articles:*");

        // 处理标签关联（这里先简化处理）
        if (CollUtil.isNotEmpty(saveDTO.getTagIds())) {
            // TODO: 更新文章标签关联
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "articles", key = "#id")
    public void deleteArticle(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }
        this.removeById(id);

        // 清除相关缓存
        cacheService.evictAllArticlesCache();
        cacheService.clearCacheByPattern("hot_articles*");
        cacheService.clearCacheByPattern("category_articles:*");
    }

    @Override
    public void publishArticle(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        if (SystemConstants.ARTICLE_STATUS_PUBLISHED == article.getStatus()) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_ALREADY_PUBLISHED);
        }

        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setStatus(SystemConstants.ARTICLE_STATUS_PUBLISHED);
        updateArticle.setPublishTime(LocalDateTime.now());
        this.updateById(updateArticle);
    }

    @Override
    public void offlineArticle(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setStatus(SystemConstants.ARTICLE_STATUS_OFFLINE);
        this.updateById(updateArticle);
    }

    @Override
    public void topArticle(Long id, Integer isTop) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setIsTop(isTop);
        this.updateById(updateArticle);
    }

    @Override
    @Cacheable(value = "hotArticles", unless = "#result.size() == 0")
    public List<ArticleListVO> getHotArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 优先从缓存获取
        List<Article> cachedArticles = cacheService.getHotArticlesFromCache();
        if (cachedArticles != null && !cachedArticles.isEmpty()) {
            return cachedArticles.stream()
                    .limit(limit)
                    .map(article -> {
                        ArticleListVO vo = new ArticleListVO();
                        BeanUtil.copyProperties(article, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }

        // 从数据库查询并缓存
        List<Article> articles = this.list();
        articles = articles.stream()
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .limit(50) // 缓存更多数据
                .collect(Collectors.toList());

        cacheService.cacheHotArticles(articles);

        return articles.stream()
                .limit(limit)
                .map(article -> {
                    ArticleListVO vo = new ArticleListVO();
                    BeanUtil.copyProperties(article, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        // 临时实现：按创建时间排序
        List<Article> articles = this.list();
        return articles.stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(limit)
                .map(article -> {
                    ArticleListVO vo = new ArticleListVO();
                    BeanUtil.copyProperties(article, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = this.getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            this.updateById(article);
        }
    }

    @Override
    public void incrementLikeCount(Long id) {
        Article article = this.getById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            this.updateById(article);
        }
    }
}