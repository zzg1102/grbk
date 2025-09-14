package org.example.ganggrbkbackend.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传响应VO
 *
 * @author Gang
 */
@Data
@Schema(description = "文件上传响应")
public class FileUploadVO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "上传时间")
    private String uploadTime;
}