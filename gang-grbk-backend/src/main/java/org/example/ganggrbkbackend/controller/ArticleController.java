package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.domain.dto.ArticleQueryDTO;
import org.example.ganggrbkbackend.domain.dto.ArticleSaveDTO;
import org.example.ganggrbkbackend.domain.vo.ArticleDetailVO;
import org.example.ganggrbkbackend.domain.vo.ArticleListVO;
import org.example.ganggrbkbackend.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 文章管理控制器
 *
 * @author Gang
 */
@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/v1/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "分页查询文章列表")
    @GetMapping
    public Result<PageResult<ArticleListVO>> pageArticles(@Valid ArticleQueryDTO queryDTO) {
        PageResult<ArticleListVO> pageResult = articleService.pageArticles(queryDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID查询文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleDetail(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        ArticleDetailVO articleDetail = articleService.getArticleDetail(id);
        return Result.success(articleDetail);
    }

    @Operation(summary = "查看文章详情（前台）")
    @GetMapping("/{id}/view")
    public Result<ArticleDetailVO> viewArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        // 增加浏览量
        articleService.incrementViewCount(id);
        ArticleDetailVO articleDetail = articleService.getArticleDetail(id);
        return Result.success(articleDetail);
    }

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<Long> createArticle(@Valid @RequestBody ArticleSaveDTO saveDTO) {
        Long articleId = articleService.saveArticle(saveDTO);
        return Result.success("文章创建成功", articleId);
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> updateArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id,
            @Valid @RequestBody ArticleSaveDTO saveDTO) {
        saveDTO.setId(id);
        articleService.updateArticle(saveDTO);
        return Result.success("文章更新成功");
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.deleteArticle(id);
        return Result.success("文章删除成功");
    }

    @Operation(summary = "发布文章")
    @PutMapping("/{id}/publish")
    public Result<Void> publishArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.publishArticle(id);
        return Result.success("文章发布成功");
    }

    @Operation(summary = "下线文章")
    @PutMapping("/{id}/offline")
    public Result<Void> offlineArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.offlineArticle(id);
        return Result.success("文章下线成功");
    }

    @Operation(summary = "置顶/取消置顶文章")
    @PutMapping("/{id}/top")
    public Result<Void> topArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id,
            @Parameter(description = "是否置顶：0-否，1-是", required = true)
            @RequestParam @NotNull(message = "置顶状态不能为空") Integer isTop) {
        articleService.topArticle(id, isTop);
        return Result.success(isTop == 1 ? "文章置顶成功" : "文章取消置顶成功");
    }

    @Operation(summary = "点赞文章")
    @PutMapping("/{id}/like")
    public Result<Void> likeArticle(
            @Parameter(description = "文章ID", required = true)
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.incrementLikeCount(id);
        return Result.success("点赞成功");
    }

    @Operation(summary = "查询热门文章")
    @GetMapping("/hot")
    public Result<List<ArticleListVO>> getHotArticles(
            @Parameter(description = "查询数量")
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ArticleListVO> hotArticles = articleService.getHotArticles(limit);
        return Result.success(hotArticles);
    }

    @Operation(summary = "查询最新文章")
    @GetMapping("/latest")
    public Result<List<ArticleListVO>> getLatestArticles(
            @Parameter(description = "查询数量")
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ArticleListVO> latestArticles = articleService.getLatestArticles(limit);
        return Result.success(latestArticles);
    }
}