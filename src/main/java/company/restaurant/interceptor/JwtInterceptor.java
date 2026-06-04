package company.restaurant.interceptor;


import company.restaurant.context.UserContext;
import company.restaurant.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 * 用于拦截需要登录的接口，验证 token 并提取用户信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private static final String JSON_UTF8 = "application/json;charset=utf-8";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 1. 从请求头获取 token
        String token = getTokenFromRequest(request);
        // 2. 如果没有 token，返回401未授权
        if (!StringUtils.hasText(token)) {
            log.warn("请求未携带token: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(JSON_UTF8);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }

        // 3. 验证 token 是否有效
        if (!jwtUtil.validateToken(token)) {
            log.warn("无效的token: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(JSON_UTF8);
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期，请重新登录\",\"data\":null}");
            return false;
        }

        // 4. 解析 token，提取用户信息
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            Integer roleId = claims.get("roleId", Integer.class);
            Integer jobType = claims.get("jobType", Integer.class);

            // 5. 将用户信息存入 ThreadLocal
            UserContext.setCurrentUser(userId, username, roleId, jobType);

            log.debug("用户 [{}] (roleId={}) 访问: {}", username, roleId, request.getRequestURI());

            return true;

        } catch (Exception e) {
            log.error("解析token失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(JSON_UTF8);
            response.getWriter().write("{\"code\":401,\"message\":\"token解析失败\",\"data\":null}");
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler, Exception ex) {
        // 请求完成后清除 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
    /**
     * 从请求头中提取 token
     * 支持两种格式：
     * 1. Authorization: Bearer <token>
     * 2. Authorization: <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader)) {
            // 如果是 "Bearer <token>" 格式，去掉 "Bearer " 前缀
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            // 否则直接返回（兼容不带 Bearer 前缀的情况）
            return authHeader;
        }

        return null;
    }
}
