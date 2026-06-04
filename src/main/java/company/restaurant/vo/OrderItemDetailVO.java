package company.restaurant.vo;
//返回给用户的订单明细VO

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDetailVO {
    private Long id;//订单明细id
    private Long orderId;//订单id
    private Long dishId;//菜品id
    private String dishName;
    private Integer itemStatus;//订单状态
    //订单状态文本
    private String itemStatusText;
    private Integer isPriority;//是否加急
    //加急文本
    private String isPriorityText;
    private LocalDateTime createTime;//创建时间
}
