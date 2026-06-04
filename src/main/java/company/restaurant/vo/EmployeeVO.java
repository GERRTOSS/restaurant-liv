package company.restaurant.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//返回员工信息VO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeVO {
    private Long id;//用户id
    private String username;//用户名
    private Integer roleId;//角色码
    private Integer jobType;
    private BigDecimal baseSalary;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
