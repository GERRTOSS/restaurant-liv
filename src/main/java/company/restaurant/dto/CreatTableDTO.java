package company.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

//创建桌子的DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatTableDTO {
    private String tableName;
    @NotNull(message = "桌子类型不能为空")
    @Min(value = 0)
    @Max(value = 1)
    private Integer tableType;
}
