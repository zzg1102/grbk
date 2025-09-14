package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.domain.dto.SystemConfigSaveDTO;
import org.example.ganggrbkbackend.domain.vo.SystemConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author Gang
 */
public interface SystemConfigService {

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SystemConfigVO> getAllConfigs();

    /**
     * 根据配置类型获取配置
     *
     * @param configType 配置类型
     * @return 配置列表
     */
    List<SystemConfigVO> getConfigsByType(String configType);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，支持默认值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 保存或更新配置
     *
     * @param saveDTO 配置信息
     * @return 配置ID
     */
    Long saveConfig(SystemConfigSaveDTO saveDTO);

    /**
     * 批量保存配置
     *
     * @param configs 配置键值对
     */
    void batchSaveConfigs(Map<String, String> configs);

    /**
     * 删除配置
     *
     * @param configId 配置ID
     */
    void deleteConfig(Long configId);

    /**
     * 根据配置键删除配置
     *
     * @param configKey 配置键
     */
    void deleteConfigByKey(String configKey);

    /**
     * 获取网站基本信息
     *
     * @return 网站信息
     */
    Map<String, String> getSiteInfo();

    /**
     * 更新网站基本信息
     *
     * @param siteInfo 网站信息
     */
    void updateSiteInfo(Map<String, String> siteInfo);

    /**
     * 刷新配置缓存
     */
    void refreshCache();
}