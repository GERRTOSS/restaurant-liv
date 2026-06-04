package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.constant.CacheConstants;
import company.restaurant.entity.Dish;
import company.restaurant.mapper.DishMapper;
import company.restaurant.service.DishService;
import company.restaurant.util.CacheTool;
import company.restaurant.vo.DishListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
//菜单查询
@Service
@Slf4j
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final CacheTool cacheTool;

    @Override
    public List<DishListVO> getDishList(Integer categoryCode, String keyword) {
        // 1. 构建缓存 key
        String paramKey = (categoryCode == null ? "all" : categoryCode)
                + ":" + (StringUtils.hasText(keyword) ? keyword : "none");
        String key = CacheConstants.DISH_MENU_KEY_PREFIX + paramKey;
        String lockKey = "lock:" + key;

        return cacheTool.getWithLock(key, lockKey, () -> {
            log.info("缓存未命中，查询数据库，categoryCode={}, keyword={}",
                    categoryCode, keyword);
            //构建查询条件
            LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
            if (categoryCode != null) {
                wrapper.eq(Dish::getCategoryCode, categoryCode);
            }
            if (StringUtils.hasText(keyword)) {
                wrapper.like(Dish::getName, keyword);
            }
            //根据查询条件输出查询到的菜单
            List<Dish> dishList = dishMapper.selectList(wrapper);
            //装配
            return dishList.stream()
                    .map(dish -> {
                        //检测菜品可用性
                        Boolean isAvailable = dishMapper.checkDishAvailable(dish.getId());
                        //库存显示逻辑
                        Integer stock = dish.getAttributeCode() == 1
                                ? 999 : dish.getStock();
                        //装配为VO
                        return DishListVO.builder()
                                .id(dish.getId())
                                .name(dish.getName())
                                .price(dish.getPrice())
                                .estWeight(dish.getEstWeight())
                                .cookTime(dish.getCookTime())
                                .categoryCode(dish.getCategoryCode())
                                .attributeCode(dish.getAttributeCode())
                                .stock(stock)
                                .isAvailable(isAvailable)
                                .description(dish.getDescription())
                                .imageUrl(dish.getImageUrl())
                                .build();
                    }).toList();
        }, List.class);
    }
}
