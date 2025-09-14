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
    `use_count` INT DEFAULT 0 COMMENT '使用次数',
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

-- 插入系统配置
INSERT INTO `tb_system_config` (`id`, `config_key`, `config_value`, `config_desc`, `config_type`, `create_by`) VALUES
(1, 'site.name', 'Gang的个人博客', '网站名称', 'SITE', 1),
(2, 'site.description', '一个基于Spring Boot和Vue的个人博客系统', '网站描述', 'SITE', 1),
(3, 'site.keywords', 'Java,Spring Boot,Vue,个人博客', '网站关键词', 'SITE', 1),
(4, 'site.author', 'Gang', '网站作者', 'SITE', 1),
(5, 'site.icp', '', 'ICP备案号', 'SITE', 1),
(6, 'comment.enable', '1', '是否开启评论功能', 'COMMENT', 1),
(7, 'comment.audit', '0', '评论是否需要审核', 'COMMENT', 1);