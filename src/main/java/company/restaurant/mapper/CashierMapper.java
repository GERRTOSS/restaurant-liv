package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//支付的mapper层,这里直接调用两个存储过程进行核销+发送劵
@Mapper
public interface CashierMapper extends BaseMapper<Order> {
    /**
     * 调用结账核销存储过程
     * 存储过程内部自动完成：计算折扣、核销优惠券、更新订单支付状态
     */
    @Select("{call sp_complete_order_checkout(#{orderId}, #{couponId})}")
    void callCheckoutProcedure(Long orderId,Long couponId);
}

