package org.example.ganggrbkbackend.listener;

import org.example.ganggrbkbackend.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RabbitMQ消息监听器
 *
 * @author Gang
 */
@Component
public class MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);

    /**
     * 监听文章消息
     */
    @RabbitListener(queues = RabbitMQConfig.ARTICLE_QUEUE)
    public void handleArticleMessage(Map<String, Object> message) {
        try {
            Long articleId = ((Number) message.get("articleId")).longValue();
            String title = (String) message.get("title");
            String action = (String) message.get("action");

            log.info("收到文章消息: articleId={}, title={}, action={}", articleId, title, action);

            // 处理文章相关的业务逻辑
            if ("create".equals(action)) {
                handleArticleCreate(articleId, title);
            }

        } catch (Exception e) {
            log.error("处理文章消息失败: {}", message, e);
        }
    }

    /**
     * 监听评论消息
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_QUEUE)
    public void handleCommentMessage(Map<String, Object> message) {
        try {
            Long commentId = ((Number) message.get("commentId")).longValue();
            Long articleId = ((Number) message.get("articleId")).longValue();
            String content = (String) message.get("content");
            String action = (String) message.get("action");

            log.info("收到评论消息: commentId={}, articleId={}, action={}", commentId, articleId, action);

            // 处理评论相关的业务逻辑
            if ("create".equals(action)) {
                handleCommentCreate(commentId, articleId, content);
            }

        } catch (Exception e) {
            log.error("处理评论消息失败: {}", message, e);
        }
    }

    /**
     * 监听邮件消息
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(Map<String, Object> message) {
        try {
            String to = (String) message.get("to");
            String subject = (String) message.get("subject");
            String content = (String) message.get("content");

            log.info("收到邮件消息: to={}, subject={}", to, subject);

            // 处理邮件发送逻辑
            handleEmailSend(to, subject, content);

        } catch (Exception e) {
            log.error("处理邮件消息失败: {}", message, e);
        }
    }

    /**
     * 处理文章创建事件
     */
    private void handleArticleCreate(Long articleId, String title) {
        log.info("处理文章创建事件: articleId={}, title={}", articleId, title);

        // 这里可以添加具体的业务逻辑：
        // 1. 更新搜索索引
        // 2. 发送通知
        // 3. 更新统计数据
        // 4. 触发其他相关操作
    }

    /**
     * 处理评论创建事件
     */
    private void handleCommentCreate(Long commentId, Long articleId, String content) {
        log.info("处理评论创建事件: commentId={}, articleId={}", commentId, articleId);

        // 这里可以添加具体的业务逻辑：
        // 1. 发送邮件通知文章作者
        // 2. 更新文章评论统计
        // 3. 内容审核
        // 4. 敏感词过滤
    }

    /**
     * 处理邮件发送事件
     */
    private void handleEmailSend(String to, String subject, String content) {
        log.info("处理邮件发送事件: to={}, subject={}", to, subject);

        // 这里可以添加具体的邮件发送逻辑：
        // 1. 调用邮件服务
        // 2. 记录发送日志
        // 3. 处理发送失败重试
        // 模拟邮件发送
        log.info("邮件发送成功: 已向 {} 发送主题为 '{}' 的邮件", to, subject);
    }
}