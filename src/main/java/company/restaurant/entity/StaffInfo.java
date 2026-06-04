package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_staff_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffInfo {
    private Long id;
    private Long userId;
    private Integer jobType;//工种
    private LocalDateTime hireDate;
    private BigDecimal baseSalary;//基本工资
}
