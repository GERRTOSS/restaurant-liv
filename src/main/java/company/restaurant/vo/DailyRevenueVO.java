package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

//每日流水VO
@Data
@Builder
public class DailyRevenueVO {
    private LocalDate statDate;
    private BigDecimal totalRevenue;
    private Long orderCount;//订单总数
}
