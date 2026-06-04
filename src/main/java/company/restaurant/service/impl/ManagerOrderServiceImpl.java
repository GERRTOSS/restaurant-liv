package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import company.restaurant.entity.Dish;
import company.restaurant.entity.Order;
import company.restaurant.entity.OrderItem;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.DishMapper;
import company.restaurant.mapper.OrderItemMapper;
import company.restaurant.mapper.OrderMapper;
import company.restaurant.service.ManagerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//大堂经理的方法的实现类
@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerOrderServiceImpl implements ManagerOrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;

    //退菜
    /*
    * 逻辑：
    * 1.验证该用户是否为合法用户以及订单的状态是否为可退菜的状态
    * 2.执行退菜
    * 3.重新计算金额更新订单表
    * 4.返回退菜成功*/
    @Override
    public void cancelItem(Long orderId, Long itemId) {
        //1.订单状态是否可退菜
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("该订单不存在");
        }
        if(order.getOrderStatus()==-1){
            throw new BusinessException("该订单状态为已退菜");
        }
        if(!(order.getOrderStatus()==0 || order.getOrderStatus()==1 || order.getOrderStatus()==2)){
            throw new BusinessException("该订单已经结算或者全部出餐，不可退菜");
        }
        //开始检验任务明细
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        if (orderItem == null) {
            throw new BusinessException("该任务不存在");
        }
        if(!orderItem.getOrderId().equals(orderId)) {
            throw new BusinessException("该菜品不属于该订单");
        }
        if(orderItem.getItemStatus()==-1){
            throw new BusinessException("该菜品已经退菜");
        }
        if(orderItem.getItemStatus()!=0 ){
            throw new BusinessException("该菜品已经在制作或者在配送中，不可退菜");
        }
        //2.开始写入
        int updated = orderItemMapper.update(
                null,new LambdaUpdateWrapper<OrderItem>()
                        .set(OrderItem::getItemStatus,-1)
                        .eq(OrderItem::getId,orderItem.getId())
                        .eq(OrderItem::getItemStatus,0)//乐观锁，保证状态可以回到原来
        );
        if(updated==0){
            throw new BusinessException("退菜失败，状态可能已经被其他员工更改了");
        }
        //计算更新后的总金额
        recalculateActualAmount(orderId);


    }
    //计算总金额
    private void recalculateActualAmount(Long orderId) {
        //获取所有的订单信息，即剩余的菜品信息,即排除状态为-1的
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId,orderId)
                .ne(OrderItem::getItemStatus,-1);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        //如果说该菜品详情里面没有东西，说明已经退光了
        if(orderItems==null || orderItems.isEmpty()){
            orderMapper.update(null,
                    new LambdaUpdateWrapper<Order>()
                            .set(Order::getOrderStatus,-1)
                            .set(Order::getTotalPrice,BigDecimal.ZERO)
                            .set(Order::getActualAmount,BigDecimal.ZERO)
                            .set(Order::getTotalWeight,0)
                            .eq(Order::getId,orderId));
            log.info("菜品已经退光，订单状态已经更改为全部退菜，orderId={}",orderId);
            return;//如果是退光了，就不用计算了，直接退出。
        }
        //在这个任务明细表中查询dish的信息
        List<Long> dishIds = orderItems.stream()
                .map(OrderItem::getDishId)
                .distinct()//去掉查询的时候重复的信息
                .toList();
        //根据dishids查询dish信息
        List<Dish> dishList = dishMapper.selectBatchIds(dishIds);
        //装入MAP中以便操作
        Map<Long,Dish> dishMap = dishList.stream().collect(Collectors.toMap(Dish::getId, dish -> dish));
        //查询所有的金额并且累加
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrderItem orderItem : orderItems) {
            Dish dish = dishMap.get(orderItem.getDishId());
            if (dish != null) {
                totalPrice = totalPrice.add(dish.getPrice());
            }
        }
        //更新订单状态,计算完毕金额后
        orderMapper.update(null,new LambdaUpdateWrapper<Order>()
                .set(Order::getTotalPrice,totalPrice)
                .eq(Order::getId,orderId));
        log.info("订单的金额已经重新计算，orderId={}",orderId);
        }

    //催单
    /*逻辑：1,。验证用户的合法性和订单详情的合法性
    * 2.执行修改订单详情的状态*/
    @Override
    public void urgeOrderItem(Long itemId) {
        //1.检验订单详情
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        if (orderItem == null) {
            throw new BusinessException("您的订单详情信息不存在");
        }
        if(orderItem.getIsPriority()==1){
            throw new BusinessException("您的此单已经加急");
        }
        //updated的意思是更新的行数，如果更新了就是1，而没更新就是0
        int updated = orderItemMapper.update(null,new LambdaUpdateWrapper<OrderItem>()
                .set(OrderItem::getIsPriority,1)
                .eq(OrderItem::getId,orderItem.getId())
                .eq(OrderItem::getIsPriority,0));//乐观锁
        if(updated==0){
            throw new BusinessException("催菜失败，可能由其他经理已经催过了");
        }
        log.info("催单成功，itemId={}",itemId);
    }
}
