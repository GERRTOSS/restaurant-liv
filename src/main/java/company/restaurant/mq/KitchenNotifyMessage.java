package company.restaurant.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 下单后通知后厨的消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenNotifyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;              // 订单ID
    private String tableDisplayName;   // 桌号展示名，如"1号桌"或"包厢A"
    private String remarks;            // 订单备注
    private LocalDateTime createTime;  // 下单时间
    private List<KitchenDishItem> dishes; // 菜品列表

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitchenDishItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long taskId;           // 任务明细ID（厨师接单用这个）
        private String dishName;       // 菜品名
        private Integer estTime;       // 预计制作时间（分钟）
        private Integer isPriority;    // 是否加急
    }
}