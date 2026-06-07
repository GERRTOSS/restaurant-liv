package company.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 开启使用 STOMP 协议来传输基于代理(message broker)的消息
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 注册一个端点，前端通过这个端点进行连接
        registry.addEndpoint("/ws/kitchen")
                .setAllowedOriginPatterns("*") // 允许跨域，方便前端本地调试
                .withSockJS(); // 开启 SockJS 降级机制（如果浏览器不支持 WebSocket，会自动降级为 HTTP 轮询）
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //1.设置心跳
        // 2. 配置消息代理 (Message Broker)
        // 开启一个简单的基于内存的消息代理，前缀为 "/topic" 的消息都会被推送到客户端
        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000,10000})//心跳每隔10s发送
                .setTaskScheduler(heartBeatScheduler());//配置调度器用来发心跳
    }
    /*
    * 定义一个心跳线程池调度器*/
    @Bean
    public TaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);//开启一个线程，专用来发送心跳
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.initialize();
        return scheduler;
    }
}