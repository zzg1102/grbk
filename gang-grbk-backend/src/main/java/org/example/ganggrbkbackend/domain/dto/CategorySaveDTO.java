package org.example.ganggrbkbackend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String description;

    private Integer sort;

    private Integer status;
}