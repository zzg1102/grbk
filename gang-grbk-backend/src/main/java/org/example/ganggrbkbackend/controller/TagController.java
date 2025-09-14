package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.TagSaveDTO;
import org.example.ganggrbkbackend.domain.vo.TagVO;
import org.example.ganggrbkbackend.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理", description = "标签相关接口")
@RestController
@RequestMapping("/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.ok(tags);
    }

    @Operation(summary = "获取所有标签")
    @GetMapping("/list")
    public Result<List<TagVO>> getAllTagsList() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.ok(tags);
    }

    @Operation(summary = "获取启用的标签")
    @GetMapping("/enabled")
    public Result<List<TagVO>> getEnabledTags() {
        List<TagVO> tags = tagService.getEnabledTags();
        return Result.ok(tags);
    }

    @Operation(summary = "获取热门标签")
    @GetMapping("/hot")
    public Result<List<TagVO>> getHotTags(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<TagVO> tags = tagService.getHotTags(limit);
        return Result.ok(tags);
    }

    @Operation(summary = "根据ID获取标签详情")
    @GetMapping("/{id}")
    public Result<TagVO> getTagById(@PathVariable Long id) {
        TagVO tag = tagService.getTagById(id);
        return Result.ok(tag);
    }

    @Operation(summary = "新增标签")
    @PostMapping
    public Result<Long> saveTag(@Valid @RequestBody TagSaveDTO saveDTO) {
        Long id = tagService.saveTag(saveDTO);
        return Result.ok(id);
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Long id, @Valid @RequestBody TagSaveDTO saveDTO) {
        saveDTO.setId(id);
        tagService.updateTag(saveDTO);
        return Result.ok();
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.ok();
    }
}