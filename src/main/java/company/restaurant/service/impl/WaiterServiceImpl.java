package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import company.restaurant.entity.*;
import company.restaurant.mq.CouponNotifyMessage;
import company.restaurant.service.WaiterService;
import company.restaurant.context.UserContext;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.*;
import company.restaurant.vo.TableDeliveryGroupVO;
import company.restaurant.vo.WaiterDeliveryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//传菜配送的实现类
@Slf4j
@Service
@RequiredArgsConstructor
public class WaiterServiceImpl implements WaiterService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final TableMapper tableMapper;
    private final DishMapper dishMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserMapper userMapper;
    private final StaffInfoMapper staffInfoMapper;

    /**
     * 获取待配送任务列表
     * 业务逻辑：
     * 1. 查询所有 item_status = 2（待配送）的任务
     * 2. 按桌号分组（方便一次送同一桌的多道菜）
     * 3. 按完成时间排序（先做好的先送）
     * @return 待配送任务列表
     */
    @Override
    public List<TableDeliveryGroupVO> getWaiterDeliveries() {
        //1.查询所有待配送的任务列表
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getItemStatus,2)
                .orderByAsc(OrderItem::getFinishTime);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        if (orderItems == null || orderItems.isEmpty()) {
            log.info("当前没有配送任务");
            return List.of();
        }
        //2.查询菜品信息
        //先取这个dishIds集合，可以一次性取出来所有的菜品信息，就不用再一次一次查询了
        List<Long> dishIds = orderItems.stream()
                .map(OrderItem::getDishId)
                .distinct()
                .toList();
        List<Dish> dishes = dishMapper.selectBatchIds(dishIds);
        Map<Long,Dish> dishMap = dishes.stream().collect(Collectors.toMap(Dish::getId, dish -> dish));
        //3.批量查询订单信息
        List<Long> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .toList();
        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        Map<Long,Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getId, order -> order));
        //4.查询桌号信息
        List<Long> tableIds = orders.stream()
                .map(Order::getTableId)
                .distinct()
                .toList();
        List<Table> tables = tableMapper.selectBatchIds(tableIds);
        Map<Long,Table> tableMap = tables.stream().collect(Collectors.toMap(Table::getId, table -> table));
        //查询厨师信息
        List<Long> chefIds = orderItems.stream().map(OrderItem::getChefId).toList();
        List<User> chefUsers = userMapper.selectBatchIds(chefIds);
        Map<Long,User> chefMap = chefUsers.stream().collect(Collectors.toMap(User::getId, user -> user));
        //5.转换为VO平铺列表(即获取全部的待配送任务)
        List<WaiterDeliveryVO> waiterDeliveryVOS = orderItems.stream()
                .map(orderItem -> {
                    Dish dish = dishMap.get(orderItem.getDishId());
                    Order order = orderMap.get(orderItem.getOrderId());
                    Table table = tableMap.get(order.getTableId());
                    User chefUser = chefMap.get(orderItem.getChefId());
                    //桌号显示名称
                    String tableName = table.getTableType()==0
                            ? "桌号"+table.getId():table.getTableName();
                    return WaiterDeliveryVO.builder()
                            .id(orderItem.getId())
                            .orderId(orderItem.getOrderId())
                            .dishId(orderItem.getDishId())
                            .dishName(dish != null ? dish.getName() : "未知菜品")
                            .tableId(order.getTableId())
                            .tableName(tableName)
                            .waiterId(orderItem.getWaiterId())
                            .chefId(orderItem.getChefId())
                            .chefName(chefUser.getUsername())
                            .finishTime(orderItem.getFinishTime())
                            .build();
                }).toList();
        //6.按照桌号分组，将平铺列表转换为table...vo
        //在MAP中根据tableId分组
        Map<Long,List<WaiterDeliveryVO>> grouped = waiterDeliveryVOS.stream()
                .collect(Collectors.groupingBy(WaiterDeliveryVO::getTableId));
        return grouped.entrySet().stream()
                .map(entry->TableDeliveryGroupVO.builder()
                        .tableId(entry.getKey())
                        .tableName(entry.getValue().getFirst().getTableName())
                        .waiters(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(TableDeliveryGroupVO::getTableId))
                .toList();
    }
    /**
     * 接单配送
     * 业务逻辑：
     * 1. 验证任务是否存在
     * 2. 验证任务状态是否为待配送（2）
     * 3. 记录 waiter_id = 当前登录用户ID
     * 4. 状态仍为2（待配送）
     * @param taskId 任务明细ID
     */
    @Override
    public void delivery(Long taskId) {
        log.info("开始接单配送，taskId={}", taskId);
        //1.验证任务是否存在
        OrderItem orderItem = orderItemMapper.selectById(taskId);
        if (orderItem == null) {
            throw new BusinessException("该配送任务去火星了");
        }
        //验证任务状态：是否为配送任务
        if (orderItem.getItemStatus() != 2) {
            throw new BusinessException("该任务还不是配送任务");
        }
        //验证该单是否已经被其他传菜员接单
        if(! orderItem.getWaiterId().equals(UserContext.getCurrentUserId())){
            throw new BusinessException("该任务已经被其他传菜员接走");
        }
        //4.更新任务()
        int updated = orderItemMapper.update(null,
                new LambdaUpdateWrapper<OrderItem>()
                        .set(OrderItem::getWaiterId,UserContext.getCurrentUserId())
                        .eq(OrderItem::getId,taskId)
                        .eq(OrderItem::getItemStatus,2)//乐观锁，确保状态仍然为待配送
                        .isNull(OrderItem::getWaiterId));//确保未被其他传菜员接走
        if(updated==0){
            throw new BusinessException("接单失败，该单已经被其他员工接单");
        }
        log.info("接单配送成功，taskId={}", taskId);
    }
    /**
     * 送达确认
     * 业务逻辑：
     * 1. 验证任务是否存在
     * 2. 验证是否是当前传菜员接的配送任务
     * 3. 验证任务状态是否为待配送（2）
     * 4. 更新 item_status = 3（已送达）
     * 5. 记录 deliver_time = NOW()
     * 6. 联动更新订单状态（如果所有明细都送达 → order_status = 3）
     * @param taskId 任务明细ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDelivery(Long taskId) {
        log.info("传菜员{}确认，taskId={}",UserContext.getCurrentUsername(),taskId);
        //1.验证任务是否存在
        OrderItem orderItem = orderItemMapper.selectById(taskId);
        if (orderItem == null) {
            throw new BusinessException("该任务去水星了");
        }
        //2.验证是否是当前传菜员配送的任务
        if(! orderItem.getWaiterId().equals(UserContext.getCurrentUserId())){
            throw new BusinessException("这不是你的任务，请你不要点击完成！");
        }
        //3.验证任务状态是否为待配送
        if(orderItem.getItemStatus() != 2){
            throw new BusinessException("这个任务还不是待配送，你不能送");
        }
        //4.更新item_status = 3
        int updated = orderItemMapper.update(null,
                new LambdaUpdateWrapper<OrderItem>()
                        .set(OrderItem::getItemStatus,3)//更新状态为配送完成
                        .set(OrderItem::getDeliverTime, LocalDateTime.now())
                        .eq(OrderItem::getId,taskId)
                        .eq(OrderItem::getItemStatus,2)//乐观锁，保证其配送状态
        );
        if(updated==0){
            throw new BusinessException("送达确认失败，任务状态已经更新到从前");
        }
        log.info("送达确认成功，taskId={}", taskId);
        //5.联动更新订单状态
        updateOrderStatus(orderItem.getOrderId());
    }
    /**
     * 获取我的配送任务列表
     * 业务逻辑：
     * 1. 查询 waiter_id = 当前登录用户ID 的任务
     * 2. 显示 item_status IN (2, 3)（待配送、已送达）
     * 3. 按配送时间倒序排序
     * @return 我的配送任务列表
     */
    @Override
    public List<WaiterDeliveryVO> getMyDeliveries() {
        //1.查询所有的接到待配送+配送成功的单子+查询当前用户的单子
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getWaiterId,UserContext.getCurrentUserId())
                .in(OrderItem::getItemStatus,2,3)
                .orderByDesc(OrderItem::getFinishTime);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        if(orderItems==null || orderItems.isEmpty()){
            return List.of();
        }
        //2.批量查询菜品信息
        List<Long> dishIds = orderItems.stream().map(OrderItem::getDishId).toList();
        List<Dish> dishes = dishMapper.selectBatchIds(dishIds);
        Map<Long,Dish> dishMap = dishes.stream().collect(Collectors.toMap(Dish::getId, dish -> dish));
        //3.批量查询订单信息
        List<Long> orderIds = orderItems.stream().map(OrderItem::getOrderId).toList();
        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        Map<Long,Order> orderMap =  orders.stream().collect(Collectors.toMap(Order::getId, order -> order));
        //4.批量查询桌号订单
        List<Long> tableIds = orders.stream().map(Order::getTableId).toList();
        List<Table> tables = tableMapper.selectBatchIds(tableIds);
        Map<Long,Table> tableMap = tables.stream().collect(Collectors.toMap(Table::getId, table -> table));
        //5.批量查询厨师姓名
        List<Long> chefIds = orderItems.stream().map(OrderItem::getChefId).distinct().toList();
        List<User> chefUsers = userMapper.selectBatchIds(chefIds);
        Map<Long,User> chefMap = chefUsers.stream().collect(Collectors.toMap(User::getId, user -> user));
        //返回信息
        return orderItems.stream()
                .map(item -> {
                    Dish dish = dishMap.get(item.getDishId());
                    Order order = orderMap.get(item.getOrderId());
                    Table table = tableMap.get(order.getTableId());
                    User chefUser = chefMap.get(item.getChefId());
                    //tableName
                    String tableName =table.getTableType()==0
                            ? "桌号"+table.getId() :table.getTableName();
                    //任务状态文本
                    String itemStatusText = item.getItemStatus() == 2?
                            "待配送":"已送达";
                    return WaiterDeliveryVO.builder()
                            .id(item.getId())
                            .orderId(item.getOrderId())
                            .dishId(item.getDishId())
                            .dishName(dish.getName())
                            .tableId(order.getTableId())
                            .tableName(tableName)
                            .itemStatus(item.getItemStatus())
                            .itemStatusText(itemStatusText)
                            .waiterId(item.getWaiterId())
                            .waiterName(UserContext.getCurrentUsername())
                            .chefId(item.getChefId())
                            .chefName(chefUser.getUsername())
                            .build();
                }).toList();
    }
    //联动更新ORDER表
    /**
     * 联动更新订单状态
     * 业务逻辑：
     * - 如果所有任务都已送达，订单状态 = 3（全部出餐）
     * - 如果部分任务已送达，订单状态 = 2（部分出餐）
     * - 如果有任务在制作中或待配送，订单状态 = 1（处理中）
     * - 如果所有任务都是待接单，订单状态 = 0（待接单）
     */
    private void updateOrderStatus(Long orderId){
        log.info("联动更新order表，orderId={}", orderId);
        //1.查询订单的所有有效任务
        LambdaUpdateWrapper<OrderItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderItem::getOrderId,orderId)
                .ne(OrderItem::getItemStatus,-1);//排除已经被退菜的任务
        List<OrderItem> orderItems = orderItemMapper.selectList(updateWrapper);
        if(orderItems==null || orderItems.isEmpty()){
            log.info("该订单没有有效任务，应该是已经被全部退菜了");
            return;
        }
        //2.统计各状态的任务数量
        long pendingCount = orderItems.stream().filter(i->i.getItemStatus()==0).count();
        long processedCount = orderItems.stream().filter(i->i.getItemStatus()==1).count();
        long deliveringCount = orderItems.stream().filter(i->i.getItemStatus()==2).count();
        long deliveredCount = orderItems.stream().filter(i->i.getItemStatus()==3).count();
        //计算订单状态
        Integer orderStatus = null ;
        if(deliveredCount==orderItems.size()){
            orderStatus = 3;//全部送达
            //联动RMQ进行发劵
            Order order = orderMapper.selectById(orderId);
            //发布本地事件，只有当整个事件成功提交后才会提交这个MQ，防止幽灵信息
            applicationEventPublisher.publishEvent(new CouponNotifyMessage(orderId,order.getUserId()));
        }else if(deliveringCount>0 || deliveredCount>0){
            orderStatus = 2;//部分送达
        }else if(processedCount>0){
            orderStatus = 1;//处理中
        }else if(pendingCount>0){
            orderStatus = 0;//待接单
        }
        //4.更新订单状态
        orderMapper.update(null,
                new LambdaUpdateWrapper<Order>()
                        .set(Order::getOrderStatus,orderStatus)
                        .eq(Order::getId,orderId)
                );
        log.info("订单状态已经更新，orderId={}", orderId);
    }
}
