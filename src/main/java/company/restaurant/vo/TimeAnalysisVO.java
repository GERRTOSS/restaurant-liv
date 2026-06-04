package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

//员工效率时间分析VO
@Data
@Builder
public class TimeAnalysisVO {
    private Integer hourSlot;//小时段
    private Long orderCount;//
    private BigDecimal avgRevenue;//
}
