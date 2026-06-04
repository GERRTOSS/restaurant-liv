package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

//工资DTO
@Data
public class SalaryPayDTO {
    @NotNull(message = "员工id不能为空")
    private Long staffId;//员工id
    @NotNull(message = "发放金额不能为空")
    private BigDecimal amount;//员工工资
    private String remark;//备注信息
}
