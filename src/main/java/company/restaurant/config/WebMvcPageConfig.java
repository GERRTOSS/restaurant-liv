package company.restaurant.config;
//图片上传处理config:配置 Spring MVC 把这个本地路径映射成可访问的静态资源
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
@Configuration
@Slf4j
public class WebMvcPageConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 健壮性处理：确保 uploadPath 结尾有斜杠 '/'
        // 比如把 "/home/restaurant/uploads" 转换为 "/home/restaurant/uploads/"
        String locationPath = uploadPath;
        if (!locationPath.endsWith("/")) {
            locationPath = locationPath + "/";
        }

        // 2. 拼接 file: 前缀得到真正的本地资源定位符
        // 最终拼接结果：file:/home/restaurant/uploads/
        String resourceLocation = "file:" + locationPath;

        // 3. 配置映射关系
        // 访问 http://localhost:8080/images/2023/10/24/xxx.png
        // 实际访问 Linux 下的 /home/restaurant/uploads/2023/10/24/xxx.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);

        log.info("【图片服务器静态资源映射】/images/** ➡️ 物理路径: {}", resourceLocation);
    }
}