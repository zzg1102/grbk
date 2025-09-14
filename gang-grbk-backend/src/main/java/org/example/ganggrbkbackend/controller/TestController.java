package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.ganggrbkbackend.common.utils.Result;
import org.example.ganggrbkbackend.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 *
 * @author Gang
 */
@Tag(name = "测试接口", description = "用于测试各种功能的接口")
@RestController
@RequestMapping("/v1/test")
public class TestController {

    private final MessageService messageService;

    public TestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/hello")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from Gang GRBK Blog!");
        data.put("time", LocalDateTime.now());
        data.put("status", "success");
        return Result.success(data);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务运行正常");
    }

    @Operation(summary = "测试RabbitMQ消息发送", description = "发送测试消息到RabbitMQ")
    @PostMapping("/rabbitmq")
    public Result<String> testRabbitMQ(@RequestParam(defaultValue = "测试文章") String title) {
        // 发送文章创建消息
        messageService.sendArticleCreateMessage(999L, title);

        // 发送评论创建消息
        messageService.sendCommentCreateMessage(888L, 999L, "这是一个测试评论");

        // 发送邮件消息
        messageService.sendEmailMessage("test@example.com", "测试邮件", "这是一封测试邮件");

        return Result.success("RabbitMQ测试消息发送成功");
    }
}