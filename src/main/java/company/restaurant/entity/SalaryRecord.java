package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_salary_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecord {
    private Long id;

    private Long staffId;      // 员工ID
    private BigDecimal amount; // 发放金额
    private LocalDateTime payDate;   // 发放日期（建议用 LocalDateTime 保持一致）

    private Long adminId;      // 发放的管理员ID
    private String remark;     // 备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
