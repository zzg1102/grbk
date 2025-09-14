package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private String description;

    private Integer sort;

    private Integer status;

    private Integer articleCount;

    private LocalDateTime createTime;
}