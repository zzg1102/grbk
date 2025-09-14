package org.example.ganggrbkbackend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagSaveDTO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String color;
}