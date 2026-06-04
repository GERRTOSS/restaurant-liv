package company.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//接收用户订单明细的DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    @NotNull(message = "订单id不能为空")
    private Long orderId;//订单id
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;//菜品id
    @NotNull(message = "数量不能为空")
    @Min(value = 1 , message="数量至少为1")
    private Integer quantity;//数量
}
