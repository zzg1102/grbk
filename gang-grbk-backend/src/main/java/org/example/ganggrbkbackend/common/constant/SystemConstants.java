package org.example.ganggrbkbackend.common.constant;

/**
 * 系统常量
 *
 * @author Gang
 */
public class SystemConstants {

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 匿名用户
     */
    public static final String ANONYMOUS_USER = "匿名用户";

    /**
     * 系统操作者
     */
    public static final String SYSTEM_OPERATOR = "系统";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 文章状态 - 草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 0;

    /**
     * 文章状态 - 已发布
     */
    public static final int ARTICLE_STATUS_PUBLISHED = 1;

    /**
     * 文章状态 - 已下线
     */
    public static final int ARTICLE_STATUS_OFFLINE = 2;

    /**
     * 评论状态 - 待审核
     */
    public static final int COMMENT_STATUS_PENDING = 0;

    /**
     * 评论状态 - 已通过
     */
    public static final int COMMENT_STATUS_APPROVED = 1;

    /**
     * 评论状态 - 已拒绝
     */
    public static final int COMMENT_STATUS_REJECTED = 2;

    /**
     * 用户类型 - 普通用户
     */
    public static final int USER_TYPE_NORMAL = 0;

    /**
     * 用户类型 - 管理员
     */
    public static final int USER_TYPE_ADMIN = 1;

    /**
     * 文件上传路径
     */
    public static final String UPLOAD_PATH = "/uploads/";

    /**
     * 图片文件后缀
     */
    public static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

    /**
     * 允许上传的文件类型
     */
    public static final String[] ALLOWED_FILE_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
            "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
}