package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 *
 * @author Gang
 */
@Data
@TableName("tb_comment")
public class Comment {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    @TableField("status")
    private Integer status;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}