package company.restaurant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//订单返回VO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChcekoutVO {
    private Long orderId;
    private Long couponId;
    private BigDecimal totalPrice;//订单原价
    private BigDecimal discountRate;//折扣率
    private BigDecimal discountAmount;//优惠金额
    private BigDecimal actualAmount;//实付金额
    private String couponStatus;//优惠卷状态描述
    private String payStatusText;//支付状态
}
