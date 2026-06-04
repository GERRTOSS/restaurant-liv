package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import company.restaurant.service.KitchenService;
import company.restaurant.context.UserContext;
import company.restaurant.entity.Dish;
import company.restaurant.entity.Order;
import company.restaurant.entity.OrderItem;
import company.restaurant.entity.Table;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.DishMapper;
import company.restaurant.mapper.OrderItemMapper;
import company.restaurant.mapper.OrderMapper;
import company.restaurant.mapper.TableMapper;
import company.restaurant.vo.KitchenTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//后厨操作模块的实现类
@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenServiceImpl implements KitchenService {
    private final OrderItemMapper orderItemMapper;
    private final TableMapper tableMapper;
    private final DishMapper dishMapper;
    private final OrderMapper orderMapper;

    /**
     * 获取待办任务列表
     * 业务逻辑：
     * 1. 查询所有 item_status = 0（待接单）的任务
     * 2. 加急任务（is_priority = 1）置顶
     * 3. 按下单时间排序（先下单的先显示）
     * @return 待办任务列表
     */
    @Override
    public List<KitchenTaskVO> getPendingTasks() {
        List<KitchenTaskVO> tasks = orderItemMapper.kitchenPending();
        return tasks;
    }
    /**
     * 接单
     * 业务逻辑：
     * 1. 验证任务是否存在
     * 2. 验证任务状态是否为待接单（0）
     * 3. 更新 item_status = 1（制作中）
     * 4. 记录 chef_id = 当前登录用户ID
     * 5. 记录 accept_time = NOW()
     * 6. 联动更新订单状态为处理中（1）
     * @param taskId 任务明细ID
     */
    @Override
    public void acceptTask(Long taskId) {
        log.info("厨师接单,taskId:{}", taskId);
        //1.查询任务明细
        OrderItem orderItem = orderItemMapper.selectById(taskId);
        if (orderItem == null) {
            throw new BusinessException("任务不存在");
        }
        //2.验证任务状态
        if (orderItem.getItemStatus() != 0) {
            throw new BusinessException("任务已经被接单了");
        }
        //3.更新菜品状态为制作中
        int updated = orderItemMapper.update(null,
                new LambdaUpdateWrapper<OrderItem>()
                        .set(OrderItem::getItemStatus, 1)//待接单->制作中
                        .set(OrderItem::getChefId, UserContext.getCurrentUserId())//这里注意要修改，应当在员工扩展表中，而不是user表
                        .set(OrderItem::getAcceptTime, LocalDateTime.now())
                        .eq(OrderItem::getOrderId, taskId)
                        .eq(OrderItem::getItemStatus,0));//乐观锁：确保状态仍然为待制作
        if (updated == 0) {
            throw new BusinessException("接单失败，该任务已经被其他厨师接走");
        }
        log.info("接单成功，taskId:{}", taskId);
        //4.联动更新订单状态为处理中
        updateOrderStatus(orderItem.getOrderId());
    }
    /**
     * 完成制作
     * 业务逻辑：
     * 1. 验证任务是否存在
     * 2. 验证是否是当前厨师接的单
     * 3. 验证任务状态是否为制作中（1）
     * 4. 更新 item_status = 2（待配送）
     * 5. 记录 finish_time = NOW()
     * @param taskId 任务明细ID
     */
    @Override
    public void finishTask(Long taskId) {
        log.info("厨师{}完成制作,taskId:{}", UserContext.getCurrentUserId(), taskId);
        OrderItem orderItem = orderItemMapper.selectById(taskId);
        //1.
        if (orderItem == null) {
            throw new BusinessException("任务不存在");
        }
        //2.
        if (! orderItem.getChefId().equals(UserContext.getCurrentUserId())) {
            throw new BusinessException("您无权处理此单");
        }
        //3.验证任务状态是否为制作中
        if (orderItem.getItemStatus() != 1) {
            throw new BusinessException("该任务不在制作状态中");
        }
        //4.制作完毕更新任务状态
        int status = orderItemMapper.update(null,
                new LambdaUpdateWrapper<OrderItem>()
                        .set(OrderItem::getItemStatus, 2)//更新状态为待配送
                        .set(OrderItem::getFinishTime, LocalDateTime.now())//填充完成时间
                        .eq(OrderItem::getOrderId, taskId)
                        .eq(OrderItem::getItemStatus,1)//乐观锁，保证其状态为待制作

        );
        if (status == 0) {
            throw new BusinessException("制作完成失败，可能已经由其他厨师制作完成");
        }
        log.info("完成制作，taskId:{}", taskId);
        //联动更新订单状态
        updateOrderStatus(orderItem.getOrderId());

    }
    /**
     * 获取我的任务列表
     * 业务逻辑：
     * 1. 查询 chef_id = 当前登录用户ID 的任务
     * 2. 只显示 item_status IN (1, 2)（制作中、待配送）
     * 3. 按接单时间倒序排序
     * @return 我的任务列表
     */
    @Override
    public List<KitchenTaskVO> getMyTasks() {
        log.info("查询本人的所有接单情况：chefid:{}", UserContext.getCurrentUserId());
        //1.获取符合状态的数据
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getChefId, UserContext.getCurrentUserId())
                .in(OrderItem::getItemStatus, List.of(1, 2))//只是查看制作中和待配送
                .orderByDesc(OrderItem::getAcceptTime);//按照接单时间查询
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);
        //如果没有任务就这样显示
        if (orderItems == null || orderItems.isEmpty()) {
            log.info("您目前没有处理的任务，chefid:{}", UserContext.getCurrentUserId());
            return List.of();
        }
        //显示个人所有的接单情况
        //2.批量查询菜品信息
        List<Long> dishIds = orderItems.stream()
                .map(OrderItem::getDishId)
                .distinct()
                .toList();
        //根据所有的id查询所有的菜品
        List<Dish> dishList = dishMapper.selectBatchIds(dishIds);
        //封装在Map中将菜品情况
        Map<Long,Dish> dishMap = dishList.stream()
                .collect(Collectors.toMap(Dish::getId, dish -> dish));
        //3.批量查询订单信息
        List<Long> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .toList();
        //4.批量查询订单信息+查询桌位信息
        List<Order> orderList = orderMapper.selectBatchIds(orderIds);
        Map<Long,Order> orderMap = orderList.stream()
                .collect(Collectors.toMap(Order::getId, order -> order));
        //4.批量查询所有桌号信息
        List<Long> tableIds = orderList.stream()
                .map(Order::getTableId)
                .distinct()
                .toList();
        List<Table> tableList = tableMapper.selectBatchIds(tableIds);
        Map<Long,Table> tableMap = tableList.stream()
                .collect(Collectors.toMap(Table::getId, table -> table));
        //5.转换为VO
        return orderItems.stream()
                .map(item -> {
                    Dish dish = dishMap.get(item.getDishId());
                    Order order = orderMap.get(item.getOrderId());
                    Table table = tableMap.get(order.getTableId());
                    //桌位显示名称
                    String tableDisplayName = table.getTableType() == 0
                            ? "桌号"+table.getId() :  table.getTableName();
                    //任务状态文本
                    String itemStatusText = item.getItemStatus() == 1
                            ? "制作中" : "待配送";
                    //装配返回
                    return KitchenTaskVO.builder()
                            .taskId(item.getId())
                            .tableId(table.getId())
                            .tableName(tableDisplayName)
                            .acceptTime(item.getAcceptTime())
                            .finishTime(item.getFinishTime())
                            .tableId(order.getTableId())
                            .isPriority(item.getIsPriority())
                            .itemStatus(item.getItemStatus())
                            .itemStatusText(itemStatusText)
                            .createTime(item.getCreateTime())
                            .dishName(dish.getName())
                            .estTime(order.getTotalCookTime())
                            .orderRemarks(order.getRemarks())
                            .build();

                }).toList();
    }
    /**
     * 联动更新订单状态
     * 业务逻辑：
     * - 如果订单中有任何任务是制作中或待配送，订单状态 = 1（处理中）
     * - 如果所有任务都已制作完成，订单状态 = 3（全部出餐）
     * - 如果部分任务制作完成，订单状态 = 2（部分出餐）
     * - 如果所有任务都是待接单，订单状态 = 0（待接单）
     */
    private void updateOrderStatus(Long orderId) {
        log.info("修改订单状态，修改者为厨师");
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId,orderId)
                .ne(OrderItem::getItemStatus,-1);//排除已退菜
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);
        if (orderItems == null || orderItems.isEmpty()) {
            log.info("订单{}没有任务明细，顾客已经全部退菜", orderId);
            return;
        }
        //2.统计每个状态的任务数量
        long pendingCount = orderItems.stream()
                .filter(orderItem -> orderItem.getItemStatus() == 0)
                .count();
        long processingCount = orderItems.stream()
                .filter(orderItem -> orderItem.getItemStatus()==1
                ||orderItem.getItemStatus()==2)
                .count();
        //3.计算订单状态
        Integer orderStatus=null;
        if(processingCount>0){
            orderStatus=1;//只要有一个在处理中，订单状态就是待处理
        }else if(pendingCount>0){
            orderStatus=1;//还是待接单
        }else {
            return;
        }
        //更新订单状态
        orderMapper.update(null,
                new LambdaUpdateWrapper<Order>()
                        .set(Order::getOrderStatus,orderStatus)
                        .eq(Order::getId,orderId));
        log.info("订单状态已经更新，orderId:{},orderStatus{}", orderId,orderStatus);


    }
}
