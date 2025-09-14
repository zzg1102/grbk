package org.example.ganggrbkbackend.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置VO
 *
 * @author Gang
 */
@Data
@Schema(description = "系统配置响应")
public class SystemConfigVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    private String configDesc;

    @Schema(description = "配置类型")
    private String configType;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}