package company.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

//采购模块
@Data
public class ProcurementDTO {
    @NotBlank(message = "采购内容不能为空")
    private String content;
    @NotNull(message = "采购总金额不能为空")
    private BigDecimal totalCost;
}
