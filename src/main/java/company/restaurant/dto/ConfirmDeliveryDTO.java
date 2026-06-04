package company.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//确认送达DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmDeliveryDTO {
    //还是只需要知道配送的是哪个单子即可
    private Long id;
}
