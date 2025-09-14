package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体
 *
 * @author Gang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_tag")
public class Tag extends BaseEntity {

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String name;

    /**
     * 标签颜色
     */
    @TableField("tag_color")
    private String color;

    /**
     * 标签描述
     */
    @TableField("tag_desc")
    private String description;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;
}