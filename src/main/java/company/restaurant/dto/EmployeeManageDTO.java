package company.restaurant.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//员工管理DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeManageDTO {
    private Long id;//用户id
    @NotNull(message = "员工姓名不能为空")
    private String username;//用户名
    private String password;//密码
    @NotNull(message = "角色码不能为空")
    @Min(value =0,message = "角色ID无效")
    @Max(value =2,message = "角色ID无效")
    private Integer roleId;
    @NotNull(message = "工种不能为空")
    private Integer jobType;//工种
    @NotNull(message = "基本工资不能为空")
    private BigDecimal baseSalary;//基本工资
}
