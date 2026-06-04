package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.OrderItem;
import company.restaurant.vo.KitchenTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//任务明细表
@Mapper
public interface OrderItemMapper extends MyBaseMapper<OrderItem> {

    //引入创建好的视图的view:厨师待看面板，直接查询到厨师要做的工作
    @Select("select * from v_kitchen_pending_tasks")
    List<KitchenTaskVO> kitchenPending();
}
