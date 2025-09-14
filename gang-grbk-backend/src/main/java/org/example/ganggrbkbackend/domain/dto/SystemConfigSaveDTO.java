package org.example.ganggrbkbackend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 系统配置保存DTO
 *
 * @author Gang
 */
@Data
@Schema(description = "系统配置保存DTO")
public class SystemConfigSaveDTO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键", required = true)
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    private String configDesc;

    @Schema(description = "配置类型")
    private String configType;
}