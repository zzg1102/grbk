package org.example.ganggrbkbackend.service;

import org.example.ganggrbkbackend.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RabbitMQ消息发送服务
 *
 * @author Gang
 */
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final RabbitTemplate rabbitTemplate;

    public MessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送文章创建消息
     *
     * @param articleId 文章ID
     * @param title     文章标题
     */
    public void sendArticleCreateMessage(Long articleId, String title) {
        try {
            Map<String, Object> message = Map.of(
                    "articleId", articleId,
                    "title", title,
                    "action", "create",
                    "timestamp", System.currentTimeMillis()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BLOG_EXCHANGE,
                    RabbitMQConfig.ARTICLE_ROUTING_KEY,
                    message
            );

            log.info("文章创建消息发送成功: articleId={}, title={}", articleId, title);
        } catch (Exception e) {
            log.error("文章创建消息发送失败: articleId={}, title={}", articleId, title, e);
        }
    }

    /**
     * 发送评论创建消息
     *
     * @param commentId 评论ID
     * @param articleId 文章ID
     * @param content   评论内容
     */
    public void sendCommentCreateMessage(Long commentId, Long articleId, String content) {
        try {
            Map<String, Object> message = Map.of(
                    "commentId", commentId,
                    "articleId", articleId,
                    "content", content,
                    "action", "create",
                    "timestamp", System.currentTimeMillis()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BLOG_EXCHANGE,
                    RabbitMQConfig.COMMENT_ROUTING_KEY,
                    message
            );

            log.info("评论创建消息发送成功: commentId={}, articleId={}", commentId, articleId);
        } catch (Exception e) {
            log.error("评论创建消息发送失败: commentId={}, articleId={}", commentId, articleId, e);
        }
    }

    /**
     * 发送邮件消息
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendEmailMessage(String to, String subject, String content) {
        try {
            Map<String, Object> message = Map.of(
                    "to", to,
                    "subject", subject,
                    "content", content,
                    "timestamp", System.currentTimeMillis()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BLOG_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message
            );

            log.info("邮件消息发送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("邮件消息发送失败: to={}, subject={}", to, subject, e);
        }
    }
}