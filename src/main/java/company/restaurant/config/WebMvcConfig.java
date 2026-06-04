package company.restaurant.config;
import company.restaurant.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器，配置拦截规则
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有 /api/** 接口
                .addPathPatterns("/api/**")
                // 排除不需要登录的接口
                .excludePathPatterns(
                        "/api/user/register",      // 用户注册
                        "/api/user/login",         // 用户登录
                        "/api/user/test",          // 测试接口
                        "/api/dish/list"           // 浏览菜单（可选：如果需要登录才能看菜单，就去掉这行）
                );
    }
}