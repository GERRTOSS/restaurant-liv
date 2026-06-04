package company.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

//顾客提交评价、反馈
@Data
public class CreateReviewDTO {
    @NotNull(message = "评价类型不能为空")
    @Min(value = 0)
    @Max(value = 1)
    private Integer type;//评价类型
    @Min(value = 0)
    @Max(value = 5)
    private Integer rating;//星级
    @NotNull(message = "内容不能为空")
    private String content;//评价、反馈内容
}
