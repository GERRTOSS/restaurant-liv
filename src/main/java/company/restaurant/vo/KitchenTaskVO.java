package company.restaurant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//用于返回厨师查看信息的VO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KitchenTaskVO {
    private Long taskId;
    private String dishName;
    private Long tableId;
    private String tableName;
    private Integer estTime;//制作时间
    private String orderRemarks;
    private Integer itemStatus;
    private String itemStatusText;
    private Integer isPriority;
    private LocalDateTime createTime;
    private LocalDateTime acceptTime;//接单时间
    private LocalDateTime finishTime;//完成时间
}
