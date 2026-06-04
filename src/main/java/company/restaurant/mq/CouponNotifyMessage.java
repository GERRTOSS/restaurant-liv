package company.restaurant.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//发放优惠劵的MQ方法的实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponNotifyMessage {
    private Long orderId;
    private Long userId;
}
