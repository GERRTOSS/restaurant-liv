package company.restaurant.Listener;

import company.restaurant.mq.CouponNotifyMessage;
import company.restaurant.mq.KitchenNotifyConsumer;
import company.restaurant.mq.MQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//发放优惠劵事件监听器
@Component
@Slf4j
@RequiredArgsConstructor
public class CouponEventListener {
    private final MQProducer  mqProducer;
    //事件监听器发送时间为after
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCouponNotifyMessage(CouponNotifyMessage message) {
        log.info("订单已经全部出餐，事务提交成功，准备发送MQ消息，orderId={}", message.getOrderId());
        //组装消息体
        CouponNotifyMessage couponNotifyMessage = new CouponNotifyMessage(
                message.getOrderId(), message.getUserId()
        );
        //调用写好的生产者方法
        mqProducer.sendCouponNotify(couponNotifyMessage);
    }
}
