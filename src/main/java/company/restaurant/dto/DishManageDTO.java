package company.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//用于接收菜品管理的DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishManageDTO {
    private Long id;
    @NotNull(message = "菜品名称不能为空")
    private String name;
    @NotNull(message = "菜品价格不能为空")
    private BigDecimal price;
    private Integer estWeight;
    @NotNull(message = "菜品制作时间不能为空")
    private Integer cookTime;
    @NotNull(message = "菜品识别码不能为空")
    private Integer categoryCode;//菜品识别码
    @NotNull(message = "菜品属性码不能为空")
    private Integer attributeCode;//属性码 0常驻 1限时
    private LocalDateTime expireTime;//限时时间
    private Integer stock;//限时菜品库存
    private String description;//菜品简介
    private String imageUrl;//菜品图片URL

}
