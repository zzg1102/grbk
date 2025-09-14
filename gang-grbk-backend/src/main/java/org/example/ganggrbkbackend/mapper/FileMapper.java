package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.File;

import java.util.List;

/**
 * 文件Mapper
 *
 * @author Gang
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    /**
     * 根据MD5查询文件
     */
    @Select("SELECT * FROM tb_file WHERE md5 = #{md5} AND status = 1")
    File selectByMd5(@Param("md5") String md5);

    /**
     * 根据上传者查询文件列表
     */
    @Select("SELECT * FROM tb_file WHERE upload_by = #{uploadBy} AND status = 1 ORDER BY create_time DESC")
    List<File> selectByUploadBy(@Param("uploadBy") Long uploadBy);

    /**
     * 根据文件类型查询文件列表
     */
    @Select("SELECT * FROM tb_file WHERE file_type LIKE CONCAT(#{fileType}, '%') AND status = 1 ORDER BY create_time DESC")
    List<File> selectByFileType(@Param("fileType") String fileType);
}