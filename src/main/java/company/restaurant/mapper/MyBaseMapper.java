package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

//增强版的MP，是为了使用MP提供的批量插入组件
public interface MyBaseMapper<T> extends BaseMapper<T> {
    //使用MP隐藏的真实批量插入方法
    int insertBatchSomeColumn(@Param("list") List<T> list);
}
