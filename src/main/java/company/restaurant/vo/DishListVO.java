package company.restaurant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//菜品VO（返回值）
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishListVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer estWeight;
    private Integer cookTime;
    private Integer categoryCode;//菜品识别码
    private Integer attributeCode;//属性码 0常驻 1限时
    private Integer stock;//限时菜品库存
    private Boolean isAvailable;//是否可用
    private String description;
    private String imageUrl;
}
