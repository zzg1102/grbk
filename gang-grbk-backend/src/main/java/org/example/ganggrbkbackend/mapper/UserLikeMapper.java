package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.UserLike;

/**
 * 用户点赞记录Mapper
 *
 * @author Gang
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {

    /**
     * 查询用户对目标的点赞状态
     */
    @Select("SELECT COUNT(*) FROM tb_user_like WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType} AND status = 1 AND delete_flag = 0")
    int getUserLikeStatus(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetType") Integer targetType);

    /**
     * 统计目标的点赞总数
     */
    @Select("SELECT COUNT(*) FROM tb_user_like WHERE target_id = #{targetId} AND target_type = #{targetType} AND status = 1 AND delete_flag = 0")
    int countLikesByTarget(@Param("targetId") Long targetId, @Param("targetType") Integer targetType);
}
