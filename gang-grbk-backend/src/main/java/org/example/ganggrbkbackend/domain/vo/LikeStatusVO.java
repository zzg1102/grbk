package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

/**
 * 点赞状态返回对象
 *
 * @author Gang
 */
@Data
public class LikeStatusVO {

    /**
     * 是否已点赞
     */
    private Boolean isLiked;

    /**
     * 点赞总数
     */
    private Integer likeCount;

    /**
     * 操作结果消息
     */
    private String message;

    public LikeStatusVO() {}

    public LikeStatusVO(Boolean isLiked, Integer likeCount, String message) {
        this.isLiked = isLiked;
        this.likeCount = likeCount;
        this.message = message;
    }

    public static LikeStatusVO success(Boolean isLiked, Integer likeCount) {
        return new LikeStatusVO(isLiked, likeCount, isLiked ? "点赞成功" : "取消点赞");
    }
}
