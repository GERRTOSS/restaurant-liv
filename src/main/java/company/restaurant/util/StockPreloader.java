package company.restaurant.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.entity.Dish;
import company.restaurant.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/*Redis里一开始是没有库存数据的，
需要一个机制把数据库的 stock 同步过去*/
@Component
public class StockPreloader implements CommandLineRunner {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void run(String... args) throws Exception {
        //查出所有限时菜品
        List<Dish> limitedDishes = dishMapper.selectList(
                new LambdaQueryWrapper<Dish>().eq(Dish::getAttributeCode,1)
        );
        for (Dish dish : limitedDishes) {
            //将库存存入redis，key为：dish:sock:{id}
            stringRedisTemplate.opsForValue().setIfAbsent("dish:stock:"+dish.getId(),dish.getStock().toString());
        }
    }
}
