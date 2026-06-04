package company.restaurant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//接口文档配置类：直接生成全部的接口，方便测试
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("餐厅管理系统API接口文档")
                        .version("1.0")
                        .description("基于 Spring Boot + Knife4j 构建的后端接口说明文档"));
    }
}
