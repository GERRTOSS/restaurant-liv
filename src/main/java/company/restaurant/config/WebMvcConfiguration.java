package company.restaurant.config;

import company.restaurant.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//日志拦截器组件的配置类，可以被spring boot扫描到
//WebMvcConfigurer
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    //引入配置好的拦截器
    private final LogInterceptor logInterceptor ;
    public WebMvcConfiguration(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")//拦截所有路径
                .excludePathPatterns("/doc.html", "/webjars/**", "/swagger-resources/**","/v3/api-docs/**", "/favicon.ico");//排除接口文档路径
    }


}
