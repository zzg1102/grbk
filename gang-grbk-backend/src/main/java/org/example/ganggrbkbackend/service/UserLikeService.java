package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.domain.vo.LikeStatusVO;

/**
 * 用户点赞服务接口
 *
 * @author Gang
 */
public interface UserLikeService {

    /**
     * 切换点赞状态（点赞/取消点赞）
     *
     * @param userId     用户ID
     * @param targetId   目标ID
     * @param targetType 目标类型：1-文章，2-评论
     * @return 点赞状态信息
     */
    LikeStatusVO toggleLike(Long userId, Long targetId, Integer targetType);

    /**
     * 获取用户对目标的点赞状态
     *
     * @param userId     用户ID
     * @param targetId   目标ID
     * @param targetType 目标类型
     * @return 是否已点赞
     */
    boolean getUserLikeStatus(Long userId, Long targetId, Integer targetType);

    /**
     * 获取目标的点赞总数
     *
     * @param targetId   目标ID
     * @param targetType 目标类型
     * @return 点赞总数
     */
    int getLikeCount(Long targetId, Integer targetType);

    /**
     * 同步所有文章的点赞数到tb_article表
     */
    void syncAllArticleLikeCount();
}
