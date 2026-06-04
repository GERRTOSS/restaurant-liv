package company.restaurant.Listener;

import company.restaurant.mq.KitchenNotifyMessage;
import company.restaurant.mq.MQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//下单消息监听器
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
    private final MQProducer mqProducer;
    //引入事务监听器用来监听事务结束
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    //此消息已经装配完整了，直接用来发送即可
    public void orderMessageEvent(KitchenNotifyMessage kitchenNotifyMessage) {
        mqProducer.sendKitchenNotify(kitchenNotifyMessage);
    }
}
