package org.example.ganggrbkbackend.service.impl;

import org.example.ganggrbkbackend.domain.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ARTICLE_CACHE_PREFIX = "article:";
    private static final String HOT_ARTICLES_KEY = "hot_articles";
    private static final String CATEGORY_ARTICLES_PREFIX = "category_articles:";
    private static final String TAG_ARTICLES_PREFIX = "tag_articles:";

    @Cacheable(value = "articles", key = "#id")
    public Article getArticleFromCache(Long id) {
        return null; // Spring会自动处理缓存逻辑
    }

    @CachePut(value = "articles", key = "#article.id")
    public Article putArticleToCache(Article article) {
        return article;
    }

    @CacheEvict(value = "articles", key = "#id")
    public void evictArticleFromCache(Long id) {
        // 清除单个文章缓存
    }

    @CacheEvict(value = "articles", allEntries = true)
    public void evictAllArticlesCache() {
        // 清除所有文章缓存
    }

    public void cacheHotArticles(List<Article> hotArticles) {
        redisTemplate.opsForValue().set(HOT_ARTICLES_KEY, hotArticles, 30, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    public List<Article> getHotArticlesFromCache() {
        return (List<Article>) redisTemplate.opsForValue().get(HOT_ARTICLES_KEY);
    }

    public void cacheArticlesByCategory(Long categoryId, List<Article> articles) {
        String key = CATEGORY_ARTICLES_PREFIX + categoryId;
        redisTemplate.opsForValue().set(key, articles, 15, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    public List<Article> getArticlesByCategoryFromCache(Long categoryId) {
        String key = CATEGORY_ARTICLES_PREFIX + categoryId;
        return (List<Article>) redisTemplate.opsForValue().get(key);
    }

    public void cacheArticlesByTag(Long tagId, List<Article> articles) {
        String key = TAG_ARTICLES_PREFIX + tagId;
        redisTemplate.opsForValue().set(key, articles, 15, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    public List<Article> getArticlesByTagFromCache(Long tagId) {
        String key = TAG_ARTICLES_PREFIX + tagId;
        return (List<Article>) redisTemplate.opsForValue().get(key);
    }

    public void incrementViewCount(Long articleId) {
        String key = "article_view:" + articleId;
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public Long getViewCount(Long articleId) {
        String key = "article_view:" + articleId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }

    public void cacheSearchResults(String keyword, List<Article> results) {
        String key = "search:" + keyword;
        redisTemplate.opsForValue().set(key, results, 10, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    public List<Article> getSearchResultsFromCache(String keyword) {
        String key = "search:" + keyword;
        return (List<Article>) redisTemplate.opsForValue().get(key);
    }

    public void clearCacheByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void clearAllCache() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}