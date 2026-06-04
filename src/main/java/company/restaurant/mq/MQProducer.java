package company.restaurant.mq;

import company.restaurant.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * MQ 消息发送服务
 * 统一管理所有消息发送逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MQProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送后厨通知消息
     *
     * @param message 消息体
     */
    public void sendKitchenNotify(KitchenNotifyMessage message) {
        log.info("发送后厨通知消息，orderId={}, 桌号={}, 菜品数量={}",
                message.getOrderId(),
                message.getTableDisplayName(),
                message.getDishes().size());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,   // 交换机
                "kitchen.notify",                // routing key
                message                          // 消息体（自动 JSON 序列化）
        );
    }
    //发送发放优惠劵
    public void sendCouponNotify(CouponNotifyMessage message) {
        log.info("根据订单orderId={},给用户：userId={},发放优惠劵.",message.getOrderId(),message.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "coupon.notify",
                message
        );
    }

}