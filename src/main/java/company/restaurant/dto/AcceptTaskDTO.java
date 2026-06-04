package company.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//厨师接单DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptTaskDTO {
    //这里也是只需要知道任务明细的id即可
    private Long itemId;
}
