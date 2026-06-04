package company.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//厨师完成DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishTaskDTO {
    //只需要知道任务明细id即可
    private Long itemId;
}
