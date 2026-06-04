package company.restaurant.mq;

import company.restaurant.config.RabbitMQConfig;

import company.restaurant.mapper.CouponMapper;
import org.springframework.messaging.handler.annotation.Header;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.stereotype.Component;
// 1. Channel 用这个（RabbitMQ 客户端的）
import com.rabbitmq.client.Channel;

//消费者：用来发放优惠券的消费者
@Component
@Slf4j
@RequiredArgsConstructor
public class CouponConsumer {
    //为了调用存储过程来发放优惠券
    private final CouponMapper couponMapper;
    @RabbitListener(queues = RabbitMQConfig.Coupon_QUEUE)
    //@Header() long deliveryTag:投递标签
    public void handleCouponIssue(CouponNotifyMessage message,//具体的包裹信息
                                  Channel channel,//和MQ的通信信道
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try{
            log.info("收到发券消息：orderId={}", message.getOrderId());
            //调用存储过程发券
            couponMapper.issueCoupon(message.getOrderId(),message.getUserId());
            //消息处理成功，手动确认
            channel.basicAck(deliveryTag,false);
            log.info("发券成功，userId={}", message.getUserId());
        }catch (Exception e){
            log.error(e.getMessage());
            try{
                //失败后重新入队，等待重试
                channel.basicNack(deliveryTag,false,true);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
        }

    }
}
