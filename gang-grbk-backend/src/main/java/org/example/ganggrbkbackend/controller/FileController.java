package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.vo.FileUploadVO;
import org.example.ganggrbkbackend.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件管理控制器
 *
 * @author Gang
 */
@Tag(name = "文件管理", description = "文件上传、下载、删除等接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadVO> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.uploadFile(file);
        return Result.success("文件上传成功", result);
    }

    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<FileUploadVO> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.uploadImage(file);
        return Result.success("图片上传成功", result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success("文件删除成功");
    }

    @Operation(summary = "获取用户文件列表")
    @GetMapping("/user/{userId}")
    public Result<List<FileUploadVO>> getUserFiles(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        List<FileUploadVO> files = fileService.getUserFiles(userId);
        return Result.success(files);
    }

    @Operation(summary = "文件访问接口")
    @GetMapping("/{year}/{month}/{day}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename) throws IOException {

        // 构建文件路径
        String relativePath = String.format("%s/%s/%s/%s", year, month, day, filename);
        Path filePath = Paths.get("/uploads", relativePath); // 使用配置的上传路径

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // 获取文件的MIME类型
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @Operation(summary = "检查文件类型是否支持")
    @GetMapping("/check-type")
    public Result<Boolean> checkFileType(
            @Parameter(description = "文件类型", required = true)
            @RequestParam String contentType,
            @Parameter(description = "是否只检查图片", required = false)
            @RequestParam(defaultValue = "false") Boolean imageOnly) {

        boolean supported;
        if (imageOnly) {
            supported = fileService.isImageFile(contentType);
        } else {
            supported = fileService.isAllowedFileType(contentType);
        }

        return Result.success(supported);
    }
}