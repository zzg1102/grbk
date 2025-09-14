package org.example.ganggrbkbackend.domain.dto;

import lombok.Data;

/**
 * 文章查询DTO
 *
 * @author Gang
 */
@Data
public class ArticleQueryDTO {

    /**
     * 页码
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 文章状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 排序字段
     */
    private String sortField = "create_time";

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortOrder = "desc";
}