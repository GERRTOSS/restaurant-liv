package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

//热销榜VO，配合视图
@Data
@Builder
public class HotDishVO {
    private Long dishId;
    private String dishName;
    private Long salesVolume;  // 注意：这个字段名要和 Mapper 里 AS 的别名一致
}
