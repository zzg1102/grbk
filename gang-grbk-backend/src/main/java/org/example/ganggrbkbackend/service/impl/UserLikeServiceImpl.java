package org.example.ganggrbkbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.ganggrbkbackend.domain.entity.Article;
import org.example.ganggrbkbackend.domain.entity.UserLike;
import org.example.ganggrbkbackend.domain.vo.LikeStatusVO;
import org.example.ganggrbkbackend.mapper.ArticleMapper;
import org.example.ganggrbkbackend.mapper.UserLikeMapper;
import org.example.ganggrbkbackend.service.UserLikeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户点赞服务实现
 *
 * @author Gang
 */
@Slf4j
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "articles", key = "#targetId", condition = "#targetType == 1")
    public LikeStatusVO toggleLike(Long userId, Long targetId, Integer targetType) {
        // 查询现有点赞记录
        LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLike::getUserId, userId)
                   .eq(UserLike::getTargetId, targetId)
                   .eq(UserLike::getTargetType, targetType)
                   .eq(UserLike::getDeleteFlag, 0);
        
        UserLike existingLike = this.getOne(queryWrapper);
        
        boolean isLiked;
        if (existingLike == null) {
            // 首次点赞
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setTargetId(targetId);
            userLike.setTargetType(targetType);
            userLike.setStatus(UserLike.STATUS_LIKED);
            
            this.save(userLike);
            isLiked = true;
        } else {
            // 切换点赞状态
            if (existingLike.getStatus().equals(UserLike.STATUS_LIKED)) {
                // 取消点赞
                existingLike.setStatus(UserLike.STATUS_UNLIKED);
                isLiked = false;
            } else {
                // 重新点赞
                existingLike.setStatus(UserLike.STATUS_LIKED);
                isLiked = true;
            }
            this.updateById(existingLike);
        }
        
        // 获取最新点赞数
        int likeCount = baseMapper.countLikesByTarget(targetId, targetType);
        
        // 如果是文章点赞，同步更新tb_article表的like_count字段
        if (targetType.equals(UserLike.TARGET_TYPE_ARTICLE)) {
            articleMapper.updateLikeCount(targetId, likeCount);
        }
        
        return LikeStatusVO.success(isLiked, likeCount);
    }

    @Override
    public boolean getUserLikeStatus(Long userId, Long targetId, Integer targetType) {
        return baseMapper.getUserLikeStatus(userId, targetId, targetType) > 0;
    }

    @Override
    public int getLikeCount(Long targetId, Integer targetType) {
        return baseMapper.countLikesByTarget(targetId, targetType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAllArticleLikeCount() {
        // 获取所有文章
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleteFlag, 0));
        
        for (Article article : articles) {
            // 统计每篇文章的实际点赞数
            int realLikeCount = baseMapper.countLikesByTarget(article.getId(), UserLike.TARGET_TYPE_ARTICLE);
            // 更新到tb_article表
            articleMapper.updateLikeCount(article.getId(), realLikeCount);
        }
        
        log.info("同步完成，共处理 {} 篇文章的点赞数", articles.size());
    }
}
