package company.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//配送请求DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {
    //任务明细表id只需要传入id就知道配送的是哪个单子了
    private Long id;
}
