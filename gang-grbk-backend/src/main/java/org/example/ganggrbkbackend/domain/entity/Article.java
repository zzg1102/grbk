package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体
 *
 * @author Gang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_article")
public class Article extends BaseEntity {

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 封面图片
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 作者ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 是否置顶：0-否，1-是
     */
    @TableField("is_top")
    private Integer isTop;

    /**
     * 文章状态：0-草稿，1-已发布，2-已下线
     */
    @TableField("status")
    private Integer status;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    // 关联查询字段，不对应数据库字段
    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 作者昵称
     */
    @TableField(exist = false)
    private String authorName;
}