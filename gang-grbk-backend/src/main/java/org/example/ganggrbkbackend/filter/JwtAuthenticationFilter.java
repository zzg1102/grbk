package org.example.ganggrbkbackend.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ganggrbkbackend.common.utils.JwtUtil;
import org.example.ganggrbkbackend.domain.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 
 * @author Gang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_USER_KEY_PREFIX = "login:user:";
    private static final String LOGOUT_TOKEN_KEY_PREFIX = "logout:token:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String authorization = request.getHeader("Authorization");

        // 如果没有Authorization头或者不是Bearer token，继续执行过滤器链
        if (StrUtil.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.replace("Bearer ", "");

        try {
            // 检查token是否在黑名单中
            String tokenKey = LOGOUT_TOKEN_KEY_PREFIX + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
                log.warn("token已在黑名单中: {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            // 验证token
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            if (userId == null || StrUtil.isBlank(username)) {
                log.warn("token解析失败: {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            // 验证token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                log.warn("token已过期: {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            // 从Redis中获取用户信息
            String userKey = LOGIN_USER_KEY_PREFIX + userId;
            User user = (User) redisTemplate.opsForValue().get(userKey);

            if (user == null) {
                log.warn("用户信息不存在于缓存中: userId={}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // 将用户信息存储到请求属性中，供Controller使用
            request.setAttribute("currentUser", user);
            request.setAttribute("currentUserId", userId);

            // 创建认证对象并设置到SecurityContext中
            String role = user.getUserType() == 1 ? "ADMIN" : "USER";
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
            );
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(user, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT认证成功: userId={}, username={}, role={}", userId, username, role);

        } catch (Exception e) {
            log.error("JWT验证异常: token={}, error={}", token, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
