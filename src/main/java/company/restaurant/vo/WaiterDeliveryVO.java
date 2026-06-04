package company.restaurant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

//传菜任务VO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaiterDeliveryVO {
    //任务明细id，实际上就是id
    private Long id;
    private Long orderId;
    private Long dishId;
    private String dishName;
    private Long tableId;
    private String tableName;
    private Integer itemStatus;
    private String itemStatusText;
    private LocalDateTime finishTime;//完成时间（厨师做完的时间）
    private LocalDateTime deliveryTime;//送达时间
    private Long waiterId;//传菜员ID
    private String waiterName;
    private Long chefId;//厨师ID
    private String chefName;


}
