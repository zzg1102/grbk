-- Gang GRBK Blog 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `gang_grbk_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `gang_grbk_blog`;

-- 1. 用户表
CREATE TABLE `tb_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(100) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `user_type` TINYINT DEFAULT 1 COMMENT '用户类型：0-普通用户，1-管理员',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 分类表
CREATE TABLE `tb_category` (
    `id` BIGINT NOT NULL COMMENT '分类ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `category_desc` VARCHAR(500) COMMENT '分类描述',
    `category_icon` VARCHAR(255) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 3. 标签表
CREATE TABLE `tb_tag` (
    `id` BIGINT NOT NULL COMMENT '标签ID',
    `tag_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
    `tag_color` VARCHAR(20) COMMENT '标签颜色',
    `tag_desc` TEXT COMMENT '标签描述',
    `use_count` INT DEFAULT 0 COMMENT '使用次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 4. 文章表
CREATE TABLE `tb_article` (
    `id` BIGINT NOT NULL COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary` TEXT COMMENT '文章摘要',
    `content` LONGTEXT COMMENT '文章内容',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `category_id` BIGINT COMMENT '分类ID',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `status` TINYINT DEFAULT 1 COMMENT '文章状态：0-草稿，1-已发布，2-已下线',
    `publish_time` DATETIME COMMENT '发布时间',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`),
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `tb_category` (`id`),
    CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 5. 文章标签关联表
CREATE TABLE `tb_article_tag` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_tag_id` (`tag_id`),
    CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 6. 评论表
CREATE TABLE `tb_comment` (
    `id` BIGINT NOT NULL COMMENT '评论ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT COMMENT '用户ID',
    `parent_id` BIGINT COMMENT '父评论ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `nickname` VARCHAR(100) COMMENT '昵称（游客评论）',
    `email` VARCHAR(100) COMMENT '邮箱（游客评论）',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 7. 系统配置表
CREATE TABLE `tb_system_config` (
    `id` BIGINT NOT NULL COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_desc` VARCHAR(500) COMMENT '配置描述',
    `config_type` VARCHAR(50) COMMENT '配置类型',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 8. 文件表
CREATE TABLE `tb_file` (
    `id` BIGINT NOT NULL COMMENT '文件ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `file_name` VARCHAR(255) NOT NULL COMMENT '存储文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_url` VARCHAR(500) COMMENT '文件访问URL',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `md5` VARCHAR(64) COMMENT '文件MD5值',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `upload_by` BIGINT COMMENT '上传人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_upload_by` (`upload_by`),
    KEY `idx_md5` (`md5`),
    CONSTRAINT `fk_file_upload_user` FOREIGN KEY (`upload_by`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- 9. 用户点赞记录表
CREATE TABLE `tb_user_like` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标ID（文章ID或评论ID）',
    `target_type` TINYINT NOT NULL COMMENT '目标类型：1-文章，2-评论',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-取消点赞，1-点赞',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
    KEY `idx_target` (`target_id`, `target_type`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户点赞记录表';

-- 插入初始数据

-- 插入管理员用户
INSERT INTO `tb_user` (`id`, `username`, `password`, `nickname`, `email`, `user_type`, `status`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSvvzb7WpzFBeCLB8VLl8rSrA3LnhFEG.Bw8V7F8XJkK', '系统管理员', 'admin@gangblog.com', 1, 1);

-- 插入默认分类
INSERT INTO `tb_category` (`id`, `category_name`, `category_desc`, `sort_order`, `create_by`) VALUES
(1, '技术分享', '分享技术相关的文章', 1, 1),
(2, '生活随笔', '记录生活的点点滴滴', 2, 1),
(3, '学习笔记', '学习过程中的笔记整理', 3, 1);

-- 插入默认标签
INSERT INTO `tb_tag` (`id`, `tag_name`, `tag_color`, `create_by`) VALUES
(1, 'Java', '#f56a00', 1),
(2, 'Spring Boot', '#2db7f5', 1),
(3, 'Vue.js', '#87d068', 1),
(4, '数据库', '#108ee9', 1),
(5, '前端', '#722ed1', 1);

-- 插入测试文章
INSERT INTO `tb_article` (`id`, `title`, `summary`, `content`, `cover_image`, `category_id`, `author_id`, `view_count`, `like_count`, `is_top`, `status`, `publish_time`, `create_by`) VALUES
(1, 'Spring Boot 3.0 新特性详解', 'Spring Boot 3.0 带来了许多令人兴奋的新特性，本文将详细介绍这些新功能。', '# Spring Boot 3.0 新特性详解\n\n## 概述\n\nSpring Boot 3.0 是一个重大版本更新，带来了许多令人兴奋的新特性和改进。\n\n## 主要新特性\n\n### 1. Java 17 基线要求\nSpring Boot 3.0 要求 Java 17 作为最低版本，这意味着你可以使用所有 Java 17 的新特性。\n\n### 2. 原生镜像支持\n通过 GraalVM，Spring Boot 3.0 提供了更好的原生镜像支持，大大减少了启动时间和内存占用。\n\n### 3. 可观察性改进\nSpring Boot 3.0 在可观察性方面有了显著改进，包括更好的指标、追踪和日志记录。\n\n## 总结\n\nSpring Boot 3.0 是一个值得升级的版本，它带来了许多有用的新特性。', NULL, 1, 1, 156, 23, 1, 1, '2024-01-15 10:30:00', 1),
(2, 'Vue 3 Composition API 实战指南', 'Vue 3 的 Composition API 为我们提供了更加灵活的代码组织方式，本文将通过实例来展示如何使用。', '# Vue 3 Composition API 实战指南\n\n## 什么是 Composition API\n\nComposition API 是 Vue 3 中引入的一种新的代码组织方式，它允许我们更好地组织和复用代码逻辑。\n\n```javascript\nimport { ref, computed, onMounted } from ''vue''\n\nexport default {\n  setup() {\n    const count = ref(0)\n    \n    const doubleCount = computed(() => count.value * 2)\n    \n    onMounted(() => {\n      console.log(''组件已挂载'')\n    })\n    \n    return {\n      count,\n      doubleCount\n    }\n  }\n}\n```\n\n## 优势\n\n1. 更好的类型推导\n2. 更好的代码组织\n3. 更容易复用逻辑\n\n## 结论\n\nComposition API 是 Vue 3 的一大亮点，值得深入学习。', NULL, 1, 1, 89, 15, 0, 1, '2024-01-14 14:20:00', 1),
(3, '我的编程学习心得', '分享一下我在编程学习路上的一些心得和体会，希望对大家有所帮助。', '# 我的编程学习心得\n\n## 开始的困惑\n\n刚开始学习编程的时候，我也和大多数人一样，感到非常困惑。面对大量的编程语言、框架和工具，不知道从何开始。\n\n## 找到方向\n\n经过一段时间的摸索，我发现学习编程最重要的是：\n\n1. **打好基础** - 不要急于学习框架，先把基础语法掌握好\n2. **多实践** - 光看不练是学不会的，一定要多写代码\n3. **持续学习** - 技术更新很快，要保持学习的习惯\n\n## 我的建议\n\n对于初学者，我建议：\n\n- 选择一门语言深入学习\n- 多看优秀的开源项目\n- 参与开源社区\n- 写技术博客总结\n\n## 结语\n\n学习编程是一个长期的过程，希望大家都能坚持下去！', NULL, 2, 1, 234, 45, 0, 1, '2024-01-13 09:15:00', 1),
(4, 'MySQL 8.0 性能优化技巧', '数据库性能优化是每个开发者都需要掌握的技能，本文分享一些 MySQL 8.0 的优化技巧。', '# MySQL 8.0 性能优化技巧\n\n## 索引优化\n\n### 1. 选择合适的索引类型\n- B-Tree 索引适合大部分查询场景\n- Hash 索引适合等值查询\n- 全文索引适合文本搜索\n\n### 2. 复合索引的使用\n```sql\n-- 创建复合索引\nCREATE INDEX idx_user_status_time ON tb_user (status, create_time);\n\n-- 遵循最左前缀原则\nSELECT * FROM tb_user WHERE status = 1 AND create_time > ''2024-01-01'';\n```\n\n## 查询优化\n\n### 避免 SELECT *\n只查询需要的字段，减少数据传输量。\n\n```sql\n-- 不好的写法\nSELECT * FROM tb_article WHERE status = 1;\n\n-- 好的写法\nSELECT id, title, summary FROM tb_article WHERE status = 1;\n```\n\n### 使用 LIMIT 限制结果集\n```sql\nSELECT id, title FROM tb_article ORDER BY create_time DESC LIMIT 10;\n```\n\n## 配置优化\n\n### innodb_buffer_pool_size\n这是最重要的参数之一，通常设置为物理内存的 70-80%。\n\n### query_cache_size\nMySQL 8.0 已经移除了查询缓存，但可以使用 Redis 等外部缓存。\n\n## 总结\n\n数据库优化是一个系统工程，需要从多个方面入手。', NULL, 1, 1, 178, 32, 0, 1, '2024-01-12 16:45:00', 1),
(5, '个人博客系统开发日记（一）', '记录开发这个博客系统的过程，分享一些技术选型和实现细节。', '# 个人博客系统开发日记（一）\n\n## 项目起源\n\n一直想要一个属于自己的博客系统，能够完全掌控内容和样式。市面上虽然有很多博客平台，但总感觉不够自由，所以决定自己动手开发一个。\n\n## 技术选型\n\n经过一番思考，我选择了以下技术栈：\n\n### 后端\n- **Spring Boot 3.0** - Java 生态最成熟的框架\n- **MySQL 8.0** - 稳定可靠的关系型数据库\n- **MyBatis Plus** - 简化数据访问层开发\n- **Redis** - 缓存和会话存储\n- **RabbitMQ** - 异步消息处理\n\n### 前端\n- **Vue 3** - 渐进式前端框架\n- **Element Plus** - 优秀的 UI 组件库\n- **Vite** - 快速的构建工具\n\n## 架构设计\n\n系统采用前后端分离的架构，后端提供 RESTful API，前端通过 Ajax 调用接口。\n\n## 开发进度\n\n目前已经完成：\n- 基础架构搭建\n- 用户认证系统\n- 文章管理功能\n- 分类标签管理\n\n## 下一步计划\n\n- 完善前端界面\n- 实现评论系统\n- 添加搜索功能\n- 优化性能\n\n## 感想\n\n开发过程虽然遇到了一些问题，但是看到功能一个个实现，还是很有成就感的。', NULL, 3, 1, 67, 12, 0, 1, '2024-01-11 11:30:00', 1);

-- 插入文章标签关联
INSERT INTO `tb_article_tag` (`id`, `article_id`, `tag_id`) VALUES
(1, 1, 1), -- Spring Boot 3.0 新特性详解 - Java
(2, 1, 2), -- Spring Boot 3.0 新特性详解 - Spring Boot
(3, 2, 3), -- Vue 3 Composition API 实战指南 - Vue.js
(4, 2, 5), -- Vue 3 Composition API 实战指南 - 前端
(5, 3, 1), -- 我的编程学习心得 - Java
(6, 4, 1), -- MySQL 8.0 性能优化技巧 - Java
(7, 4, 4), -- MySQL 8.0 性能优化技巧 - 数据库
(8, 5, 1), -- 个人博客系统开发日记（一） - Java
(9, 5, 2), -- 个人博客系统开发日记（一） - Spring Boot
(10, 5, 3); -- 个人博客系统开发日记（一） - Vue.js

-- 插入测试评论
INSERT INTO `tb_comment` (`id`, `article_id`, `user_id`, `content`, `nickname`, `email`, `ip_address`, `status`, `like_count`) VALUES
(1, 1, NULL, '写得很详细，对我很有帮助！', '小明', 'xiaoming@example.com', '192.168.1.100', 1, 5),
(2, 1, NULL, '期待更多 Spring Boot 相关的文章', '技术爱好者', 'tech@example.com', '192.168.1.101', 1, 3),
(3, 2, NULL, 'Composition API 确实比 Options API 更灵活', 'Vue开发者', 'vue@example.com', '192.168.1.102', 1, 8),
(4, 3, NULL, '很有共鸣，我也是这么过来的', '程序猿', 'programmer@example.com', '192.168.1.103', 1, 12),
(5, 3, NULL, '坚持学习真的很重要', '初学者', 'newbie@example.com', '192.168.1.104', 1, 6);

-- 插入系统配置
INSERT INTO `tb_system_config` (`id`, `config_key`, `config_value`, `config_desc`, `config_type`, `create_by`) VALUES
(1, 'site.name', 'Gang的个人博客', '网站名称', 'SITE', 1),
(2, 'site.description', '一个基于Spring Boot和Vue的个人博客系统', '网站描述', 'SITE', 1),
(3, 'site.keywords', 'Java,Spring Boot,Vue,个人博客', '网站关键词', 'SITE', 1),
(4, 'site.author', 'Gang', '网站作者', 'SITE', 1),
(5, 'site.icp', '', 'ICP备案号', 'SITE', 1),
(6, 'comment.enable', '1', '是否开启评论功能', 'COMMENT', 1),
(7, 'comment.audit', '0', '评论是否需要审核', 'COMMENT', 1);