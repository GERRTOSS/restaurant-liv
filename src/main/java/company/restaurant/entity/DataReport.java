package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@TableName("t_data_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataReport {
    private Long id;
    private LocalDateTime reportDate;//日期
    private BigDecimal totalRevenue;//总收入
    private Integer totalOrders;//总单量
    private Integer avgCookTime;//平均出餐时长
}
