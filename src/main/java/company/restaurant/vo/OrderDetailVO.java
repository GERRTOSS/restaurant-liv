package company.restaurant.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//返回给用户的order订单信息
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailVO {
    //订单id
    private Long orderId;
    private Long userId;
    private Long tableId;
    //桌号显示名称
    private String tableDisplayName;
    private BigDecimal totalPrice;
    private Integer totalWeight;
    private Integer totalCookTime;
    private Integer orderStatus;//订单状态
    private LocalDateTime createTime;//创建时间
    private Integer dishCount;//菜品数量
    //订单状态文本
    private String orderStatusText;
    private Integer payStatus;//支付状态
    //支付状态文本
    private String payStatusText;
    private BigDecimal actualAmount;//实付金额
    //预计完成时间：总时间+下单时间
    private LocalDateTime estimatedFinishTime;
    private String remark;//备注信息
    //订单详情
    private List<OrderItemDetailVO> orderItem;
}
