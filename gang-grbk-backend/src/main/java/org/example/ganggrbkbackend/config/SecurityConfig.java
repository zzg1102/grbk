package org.example.ganggrbkbackend.config;

import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security配置
 *
 * @author Gang
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（开发阶段）
            .csrf(AbstractHttpConfigurer::disable)

            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // 配置会话管理
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 公开接口
                .requestMatchers(
                    "/v1/auth/login",
                    "/v1/auth/register",
                    "/v1/auth/test",
                    "/v1/test/**",
                    "/v1/articles",
                    "/v1/articles/**",
                    "/v1/categories",
                    "/v1/categories/**",
                    "/v1/tags",
                    "/v1/tags/**",
                    "/v1/comments/article/**",
                    "/placeholder/**",
                    "/doc.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/error"
                ).permitAll()
                // 需要认证的接口
                .requestMatchers(
                    "/v1/auth/logout",
                    "/v1/auth/refresh",
                    "/v1/comments",
                    "/v1/comments/*/like",
                    "/v1/files/**",
                    "/v1/users/**",
                    "/v1/articles/save",
                    "/v1/articles/*/update",
                    "/v1/articles/*/delete"
                ).authenticated()
                // 需要管理员权限的接口
                .requestMatchers(
                    "/v1/admin/**",
                    "/v1/system-config/**"
                ).hasRole("ADMIN")
                // 其他请求需要认证
                .anyRequest().authenticated()
            )

            // 禁用表单登录和HTTP基本认证
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}