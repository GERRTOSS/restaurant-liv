package company.restaurant.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//接收用户创建订单的DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {
    private Long userId;//用户id
    private Long tableId;//桌号id
    private List<OrderItemDTO> items;
    private String remarks;//备注信息

}
