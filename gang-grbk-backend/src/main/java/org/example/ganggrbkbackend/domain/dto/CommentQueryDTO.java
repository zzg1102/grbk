package org.example.ganggrbkbackend.domain.dto;

import lombok.Data;

@Data
public class CommentQueryDTO {

    private Integer current = 1;

    private Integer size = 10;

    private Long articleId;

    private Long userId;

    private String content;

    private String keyword;

    private Integer isReview;

    private String startTime;

    private String endTime;
}