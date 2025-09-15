package org.example.ganggrbkbackend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 *
 * @author Gang
 */
@Configuration
@MapperScan("org.example.ganggrbkbackend.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(100L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 自动填充处理器
     */
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // 只有当字段存在时才自动填充
            if (metaObject.hasSetter("createTime")) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            }
            if (metaObject.hasSetter("updateTime")) {
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
            if (metaObject.hasSetter("createBy")) {
                this.strictInsertFill(metaObject, "createBy", Long.class, 1L);
            }
            if (metaObject.hasSetter("updateBy")) {
                this.strictInsertFill(metaObject, "updateBy", Long.class, 1L);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 只有当字段存在时才自动填充
            if (metaObject.hasSetter("updateTime")) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
            if (metaObject.hasSetter("updateBy")) {
                this.strictUpdateFill(metaObject, "updateBy", Long.class, 1L);
            }
        }
    }
}