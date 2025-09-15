package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import org.example.ganggrbkbackend.domain.entity.UserLike;
import org.example.ganggrbkbackend.domain.vo.ArticleDetailVO;
import org.example.ganggrbkbackend.domain.vo.ArticleListVO;
import org.example.ganggrbkbackend.mapper.ArticleMapper;
import org.example.ganggrbkbackend.mapper.ArticleTagMapper;
import org.example.ganggrbkbackend.service.ArticleService;
import org.example.ganggrbkbackend.service.MessageService;
import org.example.ganggrbkbackend.service.UserLikeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final ArticleTagMapper articleTagMapper;
    private final MessageService messageService;
    private final CacheService cacheService;
    private final UserLikeService userLikeService;

    @Override
    public PageResult<ArticleListVO> pageArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getDeleteFlag, 0) // 未删除
                   .eq(Article::getStatus, 1); // 已发布
        
        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }
        
        // 标题搜索
        if (StrUtil.isNotBlank(queryDTO.getTitle())) {
            queryWrapper.like(Article::getTitle, queryDTO.getTitle());
        }
        
        // 标签筛选
        if (queryDTO.getTagId() != null) {
            // 通过文章标签关联表查询包含指定标签的文章ID
            List<Long> articleIds = articleTagMapper.selectArticleIdsByTagId(queryDTO.getTagId());
            if (CollUtil.isNotEmpty(articleIds)) {
                queryWrapper.in(Article::getId, articleIds);
            } else {
                // 如果没有文章包含此标签，返回空结果
                queryWrapper.eq(Article::getId, -1);
            }
        }
        
        // 排序
        if (StrUtil.isNotBlank(queryDTO.getSortField())) {
            boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            switch (queryDTO.getSortField()) {
                case "create_time":
                    queryWrapper.orderBy(true, isAsc, Article::getCreateTime);
                    break;
                case "view_count":
                    queryWrapper.orderBy(true, isAsc, Article::getViewCount);
                    break;
                case "like_count":
                    queryWrapper.orderBy(true, isAsc, Article::getLikeCount);
                    break;
                default:
                    queryWrapper.orderByDesc(Article::getCreateTime);
            }
        } else {
            // 默认按创建时间倒序，置顶文章优先
            queryWrapper.orderByDesc(Article::getIsTop)
                       .orderByDesc(Article::getCreateTime);
        }
        
        IPage<Article> pageResult = articleMapper.selectPage(page, queryWrapper);

        // 转换为VO
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
        return getArticleDetail(id, null);
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long id, Long userId) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 异步增加浏览量到Redis
        cacheService.incrementViewCount(id);

        // 简化实现，直接转换
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtil.copyProperties(article, vo);

        // 设置用户点赞状态
        if (userId != null) {
            vo.setIsLiked(userLikeService.getUserLikeStatus(userId, id, UserLike.TARGET_TYPE_ARTICLE));
        } else {
            vo.setIsLiked(false);
        }

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

    @Override
    public Map<String, Object> getArticleNavigation(Long id) {
        Map<String, Object> navigation = new HashMap<>();
        
        // 查询上一篇文章
        LambdaQueryWrapper<Article> prevWrapper = new LambdaQueryWrapper<>();
        prevWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_PUBLISHED)
                   .lt(Article::getId, id)
                   .orderByDesc(Article::getId)
                   .last("LIMIT 1");
        Article prevArticle = this.getOne(prevWrapper);
        
        if (prevArticle != null) {
            Map<String, Object> prev = new HashMap<>();
            prev.put("id", prevArticle.getId());
            prev.put("title", prevArticle.getTitle());
            navigation.put("prev", prev);
        }
        
        // 查询下一篇文章
        LambdaQueryWrapper<Article> nextWrapper = new LambdaQueryWrapper<>();
        nextWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_PUBLISHED)
                   .gt(Article::getId, id)
                   .orderByAsc(Article::getId)
                   .last("LIMIT 1");
        Article nextArticle = this.getOne(nextWrapper);
        
        if (nextArticle != null) {
            Map<String, Object> next = new HashMap<>();
            next.put("id", nextArticle.getId());
            next.put("title", nextArticle.getTitle());
            navigation.put("next", next);
        }
        
        return navigation;
    }

    @Override
    public List<ArticleListVO> getRelatedArticles(Long id, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        
        // 获取当前文章信息
        Article currentArticle = this.getById(id);
        if (currentArticle == null) {
            return Collections.emptyList();
        }
        
        List<ArticleListVO> relatedArticles = new ArrayList<>();
        
        // 1. 优先推荐同分类的文章
        if (currentArticle.getCategoryId() != null) {
            LambdaQueryWrapper<Article> categoryWrapper = new LambdaQueryWrapper<>();
            categoryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_PUBLISHED)
                          .eq(Article::getCategoryId, currentArticle.getCategoryId())
                          .ne(Article::getId, id)
                          .orderByDesc(Article::getViewCount)
                          .last("LIMIT " + Math.min(limit, 3));
            
            List<Article> categoryArticles = this.list(categoryWrapper);
            for (Article article : categoryArticles) {
                ArticleListVO vo = new ArticleListVO();
                BeanUtil.copyProperties(article, vo);
                relatedArticles.add(vo);
            }
        }
        
        // 2. 如果同分类文章不够，推荐热门文章
        if (relatedArticles.size() < limit) {
            int remainingCount = limit - relatedArticles.size();
            List<Long> excludeIds = relatedArticles.stream()
                    .map(ArticleListVO::getId)
                    .collect(Collectors.toList());
            excludeIds.add(id);
            
            LambdaQueryWrapper<Article> hotWrapper = new LambdaQueryWrapper<>();
            hotWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_PUBLISHED)
                     .notIn(!excludeIds.isEmpty(), Article::getId, excludeIds)
                     .orderByDesc(Article::getViewCount)
                     .orderByDesc(Article::getLikeCount)
                     .last("LIMIT " + remainingCount);
            
            List<Article> hotArticles = this.list(hotWrapper);
            for (Article article : hotArticles) {
                ArticleListVO vo = new ArticleListVO();
                BeanUtil.copyProperties(article, vo);
                relatedArticles.add(vo);
            }
        }
        
        return relatedArticles;
    }
}