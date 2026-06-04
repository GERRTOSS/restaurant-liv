package company.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//分页请求DTO
@Data
public class PageQueryDTO {
    @Min(1)
    private Integer page=1;//当前页数
    @Min(1)@Max(100)
    private Integer pageSize=10;//每页条数
}
