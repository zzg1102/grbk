package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.domain.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 *
 * @author Gang
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    FileUploadVO uploadFile(MultipartFile file);

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 文件信息
     */
    FileUploadVO uploadImage(MultipartFile file);

    /**
     * 根据文件ID删除文件
     *
     * @param fileId 文件ID
     */
    void deleteFile(Long fileId);

    /**
     * 获取用户上传的文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileUploadVO> getUserFiles(Long userId);

    /**
     * 验证文件类型是否允许
     *
     * @param contentType 文件类型
     * @return 是否允许
     */
    boolean isAllowedFileType(String contentType);

    /**
     * 验证是否为图片文件
     *
     * @param contentType 文件类型
     * @return 是否为图片
     */
    boolean isImageFile(String contentType);
}