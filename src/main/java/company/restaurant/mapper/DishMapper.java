package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//菜单表mapper
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    //调用存储函数查询菜品是否可用
    /**
     * 调用数据库函数检查菜品是否可用
     * 对应的数据库函数：fn_check_dish_available(dish_id)
     * 判断逻辑：
     * 1. 常驻菜品（is_evergreen=1）→ 永远可用（返回1）
     * 2. 非常驻菜品 → 检查库存是否 > 0
     * @param dishId 菜品ID
     * @return true=可用, false=不可用
     */
    @Select("select fn_check_dish_available(#{dishId})")
    Boolean checkDishAvailable(Long dishId);

}
