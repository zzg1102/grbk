package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.common.utils.PageResult;
import org.example.ganggrbkbackend.domain.dto.CommentQueryDTO;
import org.example.ganggrbkbackend.domain.dto.CommentSaveDTO;
import org.example.ganggrbkbackend.domain.vo.CommentVO;
import org.example.ganggrbkbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "分页查询评论列表")
    @PostMapping("/page")
    public Result<PageResult<CommentVO>> pageComments(@Valid @RequestBody CommentQueryDTO queryDTO) {
        PageResult<CommentVO> result = commentService.pageComments(queryDTO);
        return Result.ok(result);
    }

    @Operation(summary = "根据文章ID获取评论列表")
    @GetMapping("/article/{articleId}")
    public Result<List<CommentVO>> getCommentsByArticleId(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        List<CommentVO> comments = commentService.getCommentsByArticleId(articleId);
        return Result.ok(comments);
    }

    @Operation(summary = "根据ID获取评论详情")
    @GetMapping("/{id}")
    public Result<CommentVO> getCommentById(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        CommentVO comment = commentService.getCommentById(id);
        return Result.ok(comment);
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Long> saveComment(@Valid @RequestBody CommentSaveDTO saveDTO,
                                   HttpServletRequest request) {
        // 临时使用固定用户ID，后续接入认证后获取当前用户
        Long userId = 1L;
        String ipAddress = getClientIpAddress(request);

        Long commentId = commentService.saveComment(saveDTO, userId, ipAddress);
        return Result.ok(commentId);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.ok();
    }

    @Operation(summary = "审核评论")
    @PutMapping("/{id}/review")
    public Result<Void> reviewComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @Parameter(description = "审核状态: 1-通过, 2-驳回") @RequestParam Integer isReview) {
        commentService.reviewComment(id, isReview);
        return Result.ok();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.likeComment(id);
        return Result.ok();
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0] : "";
    }
}