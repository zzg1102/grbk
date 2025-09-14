package org.example.ganggrbkbackend.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ganggrbkbackend.common.utils.JwtUtil;
import org.example.ganggrbkbackend.domain.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_USER_KEY_PREFIX = "login:user:";
    private static final String LOGOUT_TOKEN_KEY_PREFIX = "logout:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        if (StrUtil.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            log.warn("请求缺少有效的Authorization头: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authorization.replace("Bearer ", "");

        try {
            // 检查token是否在黑名单中
            String tokenKey = LOGOUT_TOKEN_KEY_PREFIX + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
                log.warn("token已在黑名单中: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 验证token
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            if (userId == null || StrUtil.isBlank(username)) {
                log.warn("token解析失败: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 验证token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                log.warn("token已过期: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 从Redis中获取用户信息
            String userKey = LOGIN_USER_KEY_PREFIX + userId;
            User user = (User) redisTemplate.opsForValue().get(userKey);

            if (user == null) {
                log.warn("用户信息不存在于缓存中: userId={}", userId);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 将用户信息存储到请求属性中，供Controller使用
            request.setAttribute("currentUser", user);
            request.setAttribute("currentUserId", userId);

            return true;

        } catch (Exception e) {
            log.error("JWT验证异常: token={}, error={}", token, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}