package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体
 *
 * @author Gang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_comment")
public class Comment extends BaseEntity {

    /**
     * 文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父评论ID（回复评论时使用）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论用户IP
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 评论地址
     */
    @TableField("ip_source")
    private String ipSource;

    /**
     * 评论用户设备
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 是否审核通过：0-待审核，1-通过，2-驳回
     */
    @TableField("is_review")
    private Integer isReview;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;
}