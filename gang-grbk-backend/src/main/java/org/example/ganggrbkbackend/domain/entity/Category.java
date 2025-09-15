package org.example.ganggrbkbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类实体
 *
 * @author Gang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category")
public class Category extends BaseEntity {

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String name;

    /**
     * 分类描述
     */
    @TableField("category_desc")
    private String description;

    /**
     * 分类图标
     */
    @TableField("category_icon")
    private String categoryIcon;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sort;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 分类浏览量
     */
    @TableField("view_count")
    private Integer viewCount;
}