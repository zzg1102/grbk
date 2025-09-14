package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.SystemConfigSaveDTO;
import org.example.ganggrbkbackend.domain.vo.SystemConfigVO;
import org.example.ganggrbkbackend.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 *
 * @author Gang
 */
@Tag(name = "系统配置", description = "系统配置管理接口")
@RestController
@RequestMapping("/api/v1/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "获取所有配置")
    @GetMapping
    public Result<List<SystemConfigVO>> getAllConfigs() {
        List<SystemConfigVO> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }

    @Operation(summary = "根据类型获取配置")
    @GetMapping("/type/{configType}")
    public Result<List<SystemConfigVO>> getConfigsByType(
            @Parameter(description = "配置类型", required = true)
            @PathVariable String configType) {
        List<SystemConfigVO> configs = systemConfigService.getConfigsByType(configType);
        return Result.success(configs);
    }

    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(
            @Parameter(description = "配置键", required = true)
            @PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    @Operation(summary = "保存配置")
    @PostMapping
    public Result<Long> saveConfig(@Valid @RequestBody SystemConfigSaveDTO saveDTO) {
        Long configId = systemConfigService.saveConfig(saveDTO);
        return Result.success("配置保存成功", configId);
    }

    @Operation(summary = "更新配置")
    @PutMapping("/{id}")
    public Result<Void> updateConfig(
            @Parameter(description = "配置ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SystemConfigSaveDTO saveDTO) {
        saveDTO.setId(id);
        systemConfigService.saveConfig(saveDTO);
        return Result.success("配置更新成功");
    }

    @Operation(summary = "批量保存配置")
    @PostMapping("/batch")
    public Result<Void> batchSaveConfigs(@RequestBody Map<String, String> configs) {
        systemConfigService.batchSaveConfigs(configs);
        return Result.success("批量保存配置成功");
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(
            @Parameter(description = "配置ID", required = true)
            @PathVariable Long id) {
        systemConfigService.deleteConfig(id);
        return Result.success("配置删除成功");
    }

    @Operation(summary = "获取网站信息")
    @GetMapping("/site")
    public Result<Map<String, String>> getSiteInfo() {
        Map<String, String> siteInfo = systemConfigService.getSiteInfo();
        return Result.success(siteInfo);
    }

    @Operation(summary = "更新网站信息")
    @PutMapping("/site")
    public Result<Void> updateSiteInfo(@RequestBody Map<String, String> siteInfo) {
        systemConfigService.updateSiteInfo(siteInfo);
        return Result.success("网站信息更新成功");
    }

    @Operation(summary = "刷新配置缓存")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        systemConfigService.refreshCache();
        return Result.success("配置缓存刷新成功");
    }
}