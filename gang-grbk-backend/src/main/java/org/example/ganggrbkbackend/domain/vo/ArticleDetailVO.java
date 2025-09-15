package org.example.ganggrbkbackend.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情VO
 *
 * @author Gang
 */
@Data
public class ArticleDetailVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 分类信息
     */
    private CategoryVO category;

    /**
     * 作者信息
     */
    private AuthorVO author;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 文章状态
     */
    private Integer status;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 标签列表
     */
    private List<TagVO> tags;

    /**
     * 分类VO
     */
    @Data
    public static class CategoryVO {
        private Long id;
        private String categoryName;
    }

    /**
     * 作者VO
     */
    @Data
    public static class AuthorVO {
        private Long id;
        private String nickname;
        private String avatar;
    }

    /**
     * 标签VO
     */
    @Data
    public static class TagVO {
        private Long id;
        private String tagName;
        private String tagColor;
    }
}