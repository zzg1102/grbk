package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.CategorySaveDTO;
import org.example.ganggrbkbackend.domain.vo.CategoryVO;
import org.example.ganggrbkbackend.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "分类相关接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public Result<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.ok(categories);
    }

    @Operation(summary = "获取启用的分类")
    @GetMapping("/enabled")
    public Result<List<CategoryVO>> getEnabledCategories() {
        List<CategoryVO> categories = categoryService.getEnabledCategories();
        return Result.ok(categories);
    }

    @Operation(summary = "根据ID获取分类详情")
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryVO category = categoryService.getCategoryById(id);
        return Result.ok(category);
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Long> saveCategory(@Valid @RequestBody CategorySaveDTO saveDTO) {
        Long id = categoryService.saveCategory(saveDTO);
        return Result.ok(id);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO saveDTO) {
        saveDTO.setId(id);
        categoryService.updateCategory(saveDTO);
        return Result.ok();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.ok();
    }
}