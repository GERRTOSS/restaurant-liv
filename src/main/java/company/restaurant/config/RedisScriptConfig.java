package company.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

//Redis限时菜品库存配置类
@Configuration // 1. 告诉 Spring 这是一个配置类，项目启动时就会加载它
public class RedisScriptConfig {

    @Bean // 2. 将方法返回的对象（DefaultRedisScript）注册为 Spring 容器中的一个 Bean（单例）
    public DefaultRedisScript<Long> defaultRedisScript() {

        // 3. 创建 Redis 脚本封装对象
        // Generic Parameter <Long>：指定这个 Lua 脚本执行完后，返回给 Java 的数据类型。我们 Lua 里返回的是 1, 0, -1, -3，对应的就是 Java 的 Long。
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        // 4. 指定脚本文件的位置
        // ClassPathResource 会去项目的 src/main/resources 目录下寻找 "stock_check.lua" 文件并读取它
        redisScript.setLocation(new ClassPathResource("stock_check.lua"));

        // 5. 指定脚本执行完毕后反序列化的类型
        // 告诉 RedisTemplate，把 Redis 返回的数字强转成 Java 的 Long 类型，这样 Java 才能安全地用 Long result 接收。
        redisScript.setResultType(Long.class);

        // 6. 返回对象
        return redisScript;
    }
}
