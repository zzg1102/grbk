package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户点赞记录实体
 *
 * @author Gang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user_like")
public class UserLike extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 目标ID（文章ID或评论ID）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 目标类型：1-文章，2-评论
     */
    @TableField("target_type")
    private Integer targetType;

    /**
     * 状态：0-取消点赞，1-点赞
     */
    @TableField("status")
    private Integer status;

    /**
     * 目标类型常量
     */
    public static final int TARGET_TYPE_ARTICLE = 1;
    public static final int TARGET_TYPE_COMMENT = 2;

    /**
     * 状态常量
     */
    public static final int STATUS_UNLIKED = 0;
    public static final int STATUS_LIKED = 1;
}
