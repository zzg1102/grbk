package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {

    private Long id;

    private String name;

    private String color;

    private Integer useCount;

    private Integer articleCount;

    private LocalDateTime createTime;
}