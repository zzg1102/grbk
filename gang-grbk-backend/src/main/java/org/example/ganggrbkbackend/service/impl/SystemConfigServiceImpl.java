package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.common.exception.ResultCodeEnum;
import org.example.ganggrbkbackend.domain.dto.SystemConfigSaveDTO;
import org.example.ganggrbkbackend.domain.entity.SystemConfig;
import org.example.ganggrbkbackend.domain.vo.SystemConfigVO;
import org.example.ganggrbkbackend.mapper.SystemConfigMapper;
import org.example.ganggrbkbackend.service.SystemConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 *
 * @author Gang
 */
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    // 本地缓存
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    @Override
    @Cacheable(value = "systemConfigs", key = "'all'")
    public List<SystemConfigVO> getAllConfigs() {
        List<SystemConfig> configs = this.list();
        return configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "systemConfigs", key = "#configType")
    public List<SystemConfigVO> getConfigsByType(String configType) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigType, configType)
                .orderByAsc(SystemConfig::getId);

        List<SystemConfig> configs = this.list(wrapper);
        return configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "configValues", key = "#configKey")
    public String getConfigValue(String configKey) {
        // 先从本地缓存获取
        if (configCache.containsKey(configKey)) {
            return configCache.get(configKey);
        }

        // 从数据库获取
        String value = systemConfigMapper.selectValueByKey(configKey);
        if (value != null) {
            configCache.put(configKey, value);
        }
        return value;
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"systemConfigs", "configValues"}, allEntries = true)
    public Long saveConfig(SystemConfigSaveDTO saveDTO) {
        SystemConfig config;

        if (saveDTO.getId() != null) {
            // 更新
            config = this.getById(saveDTO.getId());
            if (config == null) {
                throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "配置不存在");
            }
            BeanUtils.copyProperties(saveDTO, config);
            this.updateById(config);
        } else {
            // 检查配置键是否已存在
            SystemConfig existingConfig = systemConfigMapper.selectByKey(saveDTO.getConfigKey());
            if (existingConfig != null) {
                throw new BusinessException(ResultCodeEnum.PARAM_ERROR.getCode(), "配置键已存在");
            }

            // 新增
            config = new SystemConfig();
            BeanUtils.copyProperties(saveDTO, config);
            this.save(config);
        }

        // 清除本地缓存
        configCache.remove(saveDTO.getConfigKey());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"systemConfigs", "configValues"}, allEntries = true)
    public void batchSaveConfigs(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            SystemConfig existingConfig = systemConfigMapper.selectByKey(key);
            if (existingConfig != null) {
                // 更新
                existingConfig.setConfigValue(value);
                this.updateById(existingConfig);
            } else {
                // 新增
                SystemConfig newConfig = new SystemConfig();
                newConfig.setConfigKey(key);
                newConfig.setConfigValue(value);
                newConfig.setConfigType("SYSTEM");
                this.save(newConfig);
            }

            // 清除本地缓存
            configCache.remove(key);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"systemConfigs", "configValues"}, allEntries = true)
    public void deleteConfig(Long configId) {
        SystemConfig config = this.getById(configId);
        if (config == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "配置不存在");
        }

        this.removeById(configId);
        configCache.remove(config.getConfigKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"systemConfigs", "configValues"}, allEntries = true)
    public void deleteConfigByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);

        this.remove(wrapper);
        configCache.remove(configKey);
    }

    @Override
    @Cacheable(value = "siteInfo", key = "'site'")
    public Map<String, String> getSiteInfo() {
        List<SystemConfigVO> siteConfigs = getConfigsByType("SITE");
        Map<String, String> siteInfo = new HashMap<>();

        for (SystemConfigVO config : siteConfigs) {
            siteInfo.put(config.getConfigKey(), config.getConfigValue());
        }

        return siteInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"systemConfigs", "configValues", "siteInfo"}, allEntries = true)
    public void updateSiteInfo(Map<String, String> siteInfo) {
        batchSaveConfigs(siteInfo);
    }

    @Override
    @CacheEvict(value = {"systemConfigs", "configValues", "siteInfo"}, allEntries = true)
    public void refreshCache() {
        configCache.clear();
    }

    /**
     * 转换为VO
     */
    private SystemConfigVO convertToVO(SystemConfig config) {
        SystemConfigVO vo = new SystemConfigVO();
        BeanUtils.copyProperties(config, vo);
        if (config.getCreateTime() != null) {
            vo.setCreateTime(config.getCreateTime().toString());
        }
        if (config.getUpdateTime() != null) {
            vo.setUpdateTime(config.getUpdateTime().toString());
        }
        return vo;
    }
}