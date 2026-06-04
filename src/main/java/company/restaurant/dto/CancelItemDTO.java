package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//退菜请求dto
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelItemDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    //任务明细ID
    @NotNull(message = "任务明细ID")
    private Long itemId;
}
