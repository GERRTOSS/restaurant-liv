package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_procurement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procurement {
    private Long id;
    private Long staffId;//关联服务员id
    private String content;//采购详细信息
    private BigDecimal totalCost;//总钱数
    private LocalDateTime createTime;
}
