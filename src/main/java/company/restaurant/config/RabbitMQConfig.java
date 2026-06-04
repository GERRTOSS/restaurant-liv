package company.restaurant.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//RMQ的配置类
@Configuration
public class RabbitMQConfig {

    /** 交换机名字 */
    public static final String EXCHANGE_NAME = "restaurant.exchange";

    /** 队列1：后厨通知 */
    public static final String KITCHEN_QUEUE = "restaurant.kitchen.queue";
    //队列2：发放优惠劵
    public static final String Coupon_QUEUE = "restaurant.collLog.queue";

    /** 交换机：直连交换机，精准匹配 */
    @Bean
    public DirectExchange restaurantExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    /** 队列1 */
    @Bean
    public Queue kitchenQueue() {
        return QueueBuilder.durable(KITCHEN_QUEUE).build();
    }
    //队列2初始化
    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(Coupon_QUEUE).build();
    }

    /** 绑定1：交换机 → 队列1 */
    @Bean
    public Binding kitchenBinding() {
        return BindingBuilder
                .bind(kitchenQueue())//绑定队列
                .to(restaurantExchange())//绑定交换机
                .with("kitchen.notify");//规定路由键
    }
    //绑定2：交换机->队列2:发放优惠劵
    @Bean
    public Binding couponBinding() {
        return BindingBuilder
                .bind(couponQueue())
                .to(restaurantExchange())
                .with("coupon.notify");
    }

    /** JSON 消息转换器（发送对象时自动序列化） */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /** RabbitTemplate 配置 JSON 转换器 */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());//JSON序列化
        return template;
    }
}