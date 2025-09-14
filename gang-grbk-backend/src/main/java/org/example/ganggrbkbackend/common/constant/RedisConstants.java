package org.example.ganggrbkbackend.common.constant;

/**
 * Redis缓存常量
 *
 * @author Gang
 */
public class RedisConstants {

    /**
     * 用户缓存前缀
     */
    public static final String USER_CACHE_PREFIX = "user:info:";

    /**
     * 文章缓存前缀
     */
    public static final String ARTICLE_CACHE_PREFIX = "article:info:";

    /**
     * 分类缓存前缀
     */
    public static final String CATEGORY_CACHE_PREFIX = "category:info:";

    /**
     * 标签缓存前缀
     */
    public static final String TAG_CACHE_PREFIX = "tag:info:";

    /**
     * 文章浏览量缓存前缀
     */
    public static final String ARTICLE_VIEW_COUNT_PREFIX = "article:view:";

    /**
     * 热门文章缓存Key
     */
    public static final String HOT_ARTICLES_KEY = "hot:articles";

    /**
     * JWT Token黑名单前缀
     */
    public static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * 限流前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate:limit:";

    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 邮件验证码前缀
     */
    public static final String EMAIL_CODE_PREFIX = "email:code:";

    /**
     * 默认缓存过期时间（秒）
     */
    public static final long DEFAULT_EXPIRE_TIME = 3600L;

    /**
     * 长期缓存过期时间（秒）- 1天
     */
    public static final long LONG_EXPIRE_TIME = 86400L;

    /**
     * 短期缓存过期时间（秒）- 5分钟
     */
    public static final long SHORT_EXPIRE_TIME = 300L;
}