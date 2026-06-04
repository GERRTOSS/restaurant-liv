package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//返回发放工资记录的VO
@Data
@Builder
public class SalaryRecordVO {
    private Long id;
    private Long staffId;//员工id
    private String staffName;
    private LocalDateTime payDate;//支付日期
    private Long adminId;
    private BigDecimal amount;//总计钱数
    private String remark;
    private LocalDateTime createTime;//创建时间
}
