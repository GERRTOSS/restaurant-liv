package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//呼叫DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCallDTO {
    @NotNull(message = "呼叫内容不能为空")
    private String content;
}
