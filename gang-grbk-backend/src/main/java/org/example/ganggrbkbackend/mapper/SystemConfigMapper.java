package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.SystemConfig;

/**
 * 系统配置Mapper
 *
 * @author Gang
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置值
     */
    @Select("SELECT config_value FROM tb_system_config WHERE config_key = #{configKey}")
    String selectValueByKey(@Param("configKey") String configKey);

    /**
     * 根据配置键查询配置
     */
    @Select("SELECT * FROM tb_system_config WHERE config_key = #{configKey}")
    SystemConfig selectByKey(@Param("configKey") String configKey);
}