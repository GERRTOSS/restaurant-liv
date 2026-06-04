package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

//员工效能分析VO
@Data
@Builder
public class OrderItemTimeStatsVO {
    private Long taskId;           // v_order_item_time_stats.task_id
    private Long orderId;          // order_id
    private Long dishId;           // dish_id
    private Long chefId;           // chef_id
    private Long waiterId;         // waiter_id
    private Integer waitAcceptMinutes;  // 顾客下单到厨师接单等了多久
    private Integer cookMinutes;         // 厨师炒菜用了多久
    private Integer deliverMinutes;      // 传菜用了多久
    private Integer totalMinutes;        // 全流程总耗时
}
