package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//结算VO
@Data
@Builder
public class ProcurementVO {
    private Long id;
    private Long staffId;//采购员
    private String staffName;//采购员姓名
    private String content;//采购内容
    private BigDecimal totalCost;//采购总价
    private LocalDateTime createTime;
}
