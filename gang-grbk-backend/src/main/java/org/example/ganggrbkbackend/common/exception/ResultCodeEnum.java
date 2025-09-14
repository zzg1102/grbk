package org.example.ganggrbkbackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 *
 * @author Gang
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不被允许"),

    // 用户相关错误码 1000-1999
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_ACCOUNT_LOCKED(1004, "账号已被锁定"),
    USER_ALREADY_EXISTS(1005, "用户已存在"),

    // 文章相关错误码 2000-2999
    ARTICLE_NOT_FOUND(2001, "文章不存在"),
    ARTICLE_ALREADY_PUBLISHED(2002, "文章已发布"),
    ARTICLE_NOT_PUBLISHED(2003, "文章未发布"),

    // 分类相关错误码 3000-3999
    CATEGORY_NOT_FOUND(3001, "分类不存在"),
    CATEGORY_ALREADY_EXISTS(3002, "分类已存在"),
    CATEGORY_HAS_ARTICLES(3003, "分类下存在文章，无法删除"),

    // 标签相关错误码 4000-4999
    TAG_NOT_FOUND(4001, "标签不存在"),
    TAG_ALREADY_EXISTS(4002, "标签已存在"),

    // 评论相关错误码 5000-5999
    COMMENT_NOT_FOUND(5001, "评论不存在"),
    COMMENT_AUDIT_PENDING(5002, "评论待审核"),

    // 文件相关错误码 6000-6999
    FILE_UPLOAD_FAILED(6001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(6002, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(6003, "文件大小超出限制"),
    FILE_NOT_FOUND(6004, "文件不存在");

    private final Integer code;
    private final String message;
}