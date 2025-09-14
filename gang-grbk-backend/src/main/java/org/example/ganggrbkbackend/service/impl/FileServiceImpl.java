package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ganggrbkbackend.common.constant.SystemConstants;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.common.exception.ResultCodeEnum;
import org.example.ganggrbkbackend.domain.entity.File;
import org.example.ganggrbkbackend.domain.vo.FileUploadVO;
import org.example.ganggrbkbackend.mapper.FileMapper;
import org.example.ganggrbkbackend.service.FileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件服务实现
 *
 * @author Gang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    private final FileMapper fileMapper;

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private Long maxFileSize; // 10MB

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Override
    public FileUploadVO uploadFile(MultipartFile file) {
        return doUpload(file, false);
    }

    @Override
    public FileUploadVO uploadImage(MultipartFile file) {
        return doUpload(file, true);
    }

    /**
     * 执行文件上传
     */
    private FileUploadVO doUpload(MultipartFile file, boolean imageOnly) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }

        // 文件大小检查
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ResultCodeEnum.FILE_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // 文件类型检查
        if (imageOnly) {
            if (!isImageFile(contentType)) {
                throw new BusinessException(ResultCodeEnum.FILE_TYPE_NOT_SUPPORTED.getCode(), "只支持图片文件");
            }
        } else {
            if (!isAllowedFileType(contentType)) {
                throw new BusinessException(ResultCodeEnum.FILE_TYPE_NOT_SUPPORTED);
            }
        }

        try {
            // 计算文件MD5
            String md5 = MD5.create().digestHex(file.getBytes());

            // 检查文件是否已存在（相同MD5）
            File existingFile = fileMapper.selectByMd5(md5);
            if (existingFile != null) {
                // 返回已存在文件的信息
                return convertToVO(existingFile);
            }

            // 生成文件名和路径
            String fileExtension = FileUtil.extName(originalFilename);
            String fileName = IdUtil.simpleUUID() + "." + fileExtension;

            // 按日期创建目录
            String dateDir = DateUtil.format(new Date(), "yyyy/MM/dd");
            String relativePath = dateDir + "/" + fileName;

            // 创建完整的文件路径
            Path uploadDir = Paths.get(uploadPath, dateDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 生成访问URL
            String fileUrl = String.format("http://localhost:%s%s/files/%s",
                serverPort, contextPath, relativePath);

            // 保存文件记录到数据库
            File fileEntity = new File();
            fileEntity.setOriginalName(originalFilename);
            fileEntity.setFileName(fileName);
            fileEntity.setFilePath(filePath.toString());
            fileEntity.setFileUrl(fileUrl);
            fileEntity.setFileType(contentType);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setMd5(md5);
            fileEntity.setStatus(1);
            fileEntity.setUploadBy(SystemConstants.SUPER_ADMIN_ID); // 暂时使用管理员ID

            this.save(fileEntity);

            return convertToVO(fileEntity);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteFile(Long fileId) {
        File file = this.getById(fileId);
        if (file == null) {
            throw new BusinessException(ResultCodeEnum.FILE_NOT_FOUND);
        }

        // 删除文件系统中的文件
        try {
            Path filePath = Paths.get(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            log.warn("删除文件系统中的文件失败: {}", e.getMessage());
        }

        // 删除数据库记录
        this.removeById(fileId);
    }

    @Override
    public List<FileUploadVO> getUserFiles(Long userId) {
        List<File> files = fileMapper.selectByUploadBy(userId);
        return files.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAllowedFileType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return Arrays.asList(SystemConstants.ALLOWED_FILE_TYPES).contains(contentType);
    }

    @Override
    public boolean isImageFile(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/");
    }

    /**
     * 转换为VO
     */
    private FileUploadVO convertToVO(File file) {
        FileUploadVO vo = new FileUploadVO();
        BeanUtils.copyProperties(file, vo);
        if (file.getCreateTime() != null) {
            vo.setUploadTime(file.getCreateTime().toString());
        }
        return vo;
    }
}