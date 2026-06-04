package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//催单请求DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrgeOrderDTO {
    //订单ID
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
