package org.example.ganggrbkbackend.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 文章保存/更新DTO
 *
 * @author Gang
 */
@Data
public class ArticleSaveDTO {

    /**
     * 文章ID（更新时需要）
     */
    private Long id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 文章状态：0-草稿，1-已发布
     */
    @NotNull(message = "文章状态不能为空")
    private Integer status;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;
}