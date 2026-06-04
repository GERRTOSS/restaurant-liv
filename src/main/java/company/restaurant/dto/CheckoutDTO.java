package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//结账DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutDTO {
    //订单id
    @NotNull(message = "订单id不能为空")
    private Long orderId;
    //优惠卷id
    private Long couponId;
}
