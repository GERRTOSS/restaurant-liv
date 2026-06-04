package company.restaurant.mq;

import company.restaurant.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 后厨通知消费者
 * 监听后厨队列，收到消息后推送给 WebSocket 连接的后厨端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KitchenNotifyConsumer {
    // Spring 自动注入的 STOMP 消息发送模板
    private final SimpMessagingTemplate messagingTemplate;
    @RabbitListener(queues = RabbitMQConfig.KITCHEN_QUEUE)
    public void handleKitchenNotify(KitchenNotifyMessage message) {
        log.info("后厨收到新订单通知，orderId={}, 桌号={}, 菜品数={}",
                message.getOrderId(),
                message.getTableDisplayName(),
                message.getDishes().size());


        try{
        messagingTemplate.convertAndSend("/topic/kitchen", message);
        log.info("web推送成功:message={}",message);
        }catch(Exception e){
            log.info("web推送失败",e);
        }


        // 目前打印日志，后厨人员通过刷新页面看到新订单
        log.info("后厨订单详情：{}", message);
    }
}