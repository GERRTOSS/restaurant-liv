package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_dish")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer estWeight;
    private Integer cookTime;
    private Integer categoryCode;//菜品识别码
    private Integer attributeCode;//属性码 0常驻 1限时
    private LocalDateTime expireTime;//限时时间
    private Integer stock;//限时菜品库存
    private String description;//备注
    private String imageUrl;//图片链接
}
