package org.example.ganggrbkbackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 *
 * @author Gang
 */
@Configuration
public class RabbitMQConfig {

    // 队列名称常量
    public static final String ARTICLE_QUEUE = "article.queue";
    public static final String COMMENT_QUEUE = "comment.queue";
    public static final String EMAIL_QUEUE = "email.queue";

    // 交换机名称常量
    public static final String BLOG_EXCHANGE = "blog.exchange";

    // 路由键常量
    public static final String ARTICLE_ROUTING_KEY = "article.create";
    public static final String COMMENT_ROUTING_KEY = "comment.create";
    public static final String EMAIL_ROUTING_KEY = "email.send";

    /**
     * 消息转换器 - 使用JSON格式
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * 监听器容器工厂配置
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    /**
     * 声明交换机
     */
    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange(BLOG_EXCHANGE, true, false);
    }

    /**
     * 声明文章队列
     */
    @Bean
    public Queue articleQueue() {
        return QueueBuilder.durable(ARTICLE_QUEUE).build();
    }

    /**
     * 声明评论队列
     */
    @Bean
    public Queue commentQueue() {
        return QueueBuilder.durable(COMMENT_QUEUE).build();
    }

    /**
     * 声明邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    /**
     * 绑定文章队列到交换机
     */
    @Bean
    public Binding articleBinding() {
        return BindingBuilder.bind(articleQueue()).to(blogExchange()).with(ARTICLE_ROUTING_KEY);
    }

    /**
     * 绑定评论队列到交换机
     */
    @Bean
    public Binding commentBinding() {
        return BindingBuilder.bind(commentQueue()).to(blogExchange()).with(COMMENT_ROUTING_KEY);
    }

    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(blogExchange()).with(EMAIL_ROUTING_KEY);
    }
}