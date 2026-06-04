package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_coupon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    private Long id;
    private Long userId;
    private BigDecimal discountRate;
    private Integer isUsed;
    private LocalDateTime expireTime;
    private Integer sourceType;
}
