package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import company.restaurant.dto.PageQueryDTO;
import company.restaurant.mq.KitchenNotifyMessage;
import company.restaurant.mq.MQProducer;
import company.restaurant.service.OrderService;
import company.restaurant.context.UserContext;
import company.restaurant.dto.CreateOrderDTO;
import company.restaurant.dto.OrderItemDTO;
import company.restaurant.entity.Dish;
import company.restaurant.entity.Order;
import company.restaurant.entity.OrderItem;
import company.restaurant.entity.Table;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.*;
import company.restaurant.vo.OrderDetailVO;
import company.restaurant.vo.OrderItemDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//订单接口实现类
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final TableMapper tableMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;
    private final MQProducer mqProducer;//注入MQ，用于通知后厨
    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> stockDeductScript;
    private final ApplicationEventPublisher applicationEventPublisher;
    //1.将接收到的用户信息写入订单实体中，并向用户返回其点的所有菜
    /**
     * 创建订单（含拆分任务明细）
     * 业务逻辑：
     * 1. 验证桌位是否存在
     * 2. 验证菜品是否可用
     * 3. 计算订单总价、总时间
     * 4. 插入订单主表
     * 5. 拆分并批量插入任务明细表（一份菜品 = 一条明细）
     * 6. 触发器自动计算total_weight
     * 7. 返回订单详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO createOrder(CreateOrderDTO createOrderDTO) {
        //1.验证桌位是否存在
        Table table=tableMapper.selectById(createOrderDTO.getTableId());
        if (table==null) {
            throw new BusinessException("请先选择桌位再点菜");
        }
        //2.验证菜品的可用性
        //获取用户点菜单中的所有菜品id，用于后续的验证可用性
        List<Long> dishIds=createOrderDTO.getItems().stream()
                .map(OrderItemDTO::getDishId)
                .distinct()
                .toList();
        List<Dish> dishList = dishMapper.selectBatchIds(dishIds);
        Map<Long, Dish> dishMap = dishList.stream()
                .collect(Collectors.toMap(Dish::getId, dish -> dish));
        //3.验证所有菜品是否存在，并计算总价和总时间
        BigDecimal totalPrice=BigDecimal.ZERO;
        Integer maxCookTime = 0;
        for(OrderItemDTO itemDTO:createOrderDTO.getItems()) {
            Dish dish=dishMap.get(itemDTO.getDishId());
            if(dish==null) {
                throw new BusinessException("这个菜品我们还没有学会制作：dishId"+itemDTO.getDishId());
            }
            //检查菜品可用性
            Boolean isAvailable = dishMapper.checkDishAvailable(dish.getId());
            if(!isAvailable) {
                throw new BusinessException("不好意思，该菜品暂时不可以点单"+dish.getName());
            }
            //如果是限时菜品，执行redis的库存扣减
            if(dish.getAttributeCode()==1){
                String stockKey = "dish:stock:"+dish.getId();
                //准备Lua脚本参数
                Long now = System.currentTimeMillis()/1000;//
                Long expireTime= 0L;
                if(dish.getExpireTime()!=null) {
                    expireTime = dish.getExpireTime()
                            .atZone(ZoneId.systemDefault())//设置时区，中国时区
                            .toInstant()//转换为计算机通用时间格式
                            .getEpochSecond();//转换成秒数，以便和now比较
                }
                //执行Lua脚本
                Long result =stringRedisTemplate.execute(
                        stockDeductScript,
                        Collections.singletonList(stockKey),//KEY[1]
                        String.valueOf(itemDTO.getQuantity()),//ARGV[1]
                        String.valueOf(now),//ARGV[2]
                        String.valueOf(expireTime)//ARGV[3]
                );
                //根据返回值判断扣减结果
                if(result == null) {
                    throw new BusinessException("系统异常，Redis扣减失败");
                }else if(result==-3) {
                    throw new BusinessException("该限时菜品已经过期："+dish.getName());
                }else if(result==-1) {
                    throw new BusinessException("Redis中没有该菜品的库存，请联系管理员");
                }else if(result==0) {
                    throw new BusinessException("抱歉，【"+dish.getName()+"】库存不足");
                }
                log.info("扣减成功");
            }
            //累加总价
            BigDecimal itemTotal= dish.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            totalPrice=totalPrice.add(itemTotal);
            //更新最大制作时间
            if(dish.getCookTime()>maxCookTime) {
                maxCookTime=dish.getCookTime();
            }
        }
        //计算总重量
        int totalWeight = dishList.stream().filter(dish1 -> dish1.getEstWeight() != null)
                .mapToInt(Dish::getEstWeight).sum();
        log.info("订单总价：{}，订单总时间：{}",totalPrice,maxCookTime);
        //插入订单主表+总重量这里触发器直接自己计算了，不用再插入
        Order order= Order.builder()
                .userId(UserContext.getCurrentUserId())
                .tableId(createOrderDTO.getTableId())
                .totalCookTime(maxCookTime)
                .totalWeight(totalWeight)
                .totalPrice(totalPrice)
                .actualAmount(totalPrice)
                .orderStatus(0)
                .payStatus(0)
                .remarks(createOrderDTO.getRemarks())
                .build();
        orderMapper.insert(order);
        log.info("订单创建成功");
        //5.拆分并批量插入任务明细表中
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemDTO itemDTO:createOrderDTO.getItems()) {
            //每个菜品按照数量拆分成多个明细
            for(int i=0;i<itemDTO.getQuantity();i++) {
                OrderItem orderItem = OrderItem.builder()
                        .orderId(order.getId())
                        .dishId(itemDTO.getDishId())
                        .itemStatus(0)
                        .isPriority(0).build();
                orderItems.add(orderItem);
            }
        }
        //批量插入所有明细，提高性能
        orderItemMapper.insertBatchSomeColumn(orderItems);
        // ========== 新增：发 MQ 消息通知后厨 ==========
        sendMQ(order,table,orderItems,dishMap);
        //返回订单详情
        return getOrderDetailVO(order.getId());
    }
    //将MQ从主流程中剥离出来，以便对主流程进行瘦身
    private void sendMQ(Order order,Table table,List<OrderItem> orderItems,Map<Long,Dish> dishMap) {
        try {
            String tableName = table.getTableType()==0 ? "桌号"+table.getId():table.getTableName();
            List<KitchenNotifyMessage.KitchenDishItem> items = new ArrayList<>();
            for(OrderItem orderItem:orderItems) {
                Dish dish=dishMap.get(orderItem.getDishId());
                if(dish!=null) {
                    items.add(
                            KitchenNotifyMessage.KitchenDishItem.builder()
                                    .taskId(orderItem.getId())
                                    .dishName(dish.getName())
                                    .isPriority(orderItem.getIsPriority())
                                    .estTime(dish.getCookTime()).build()
                    );
                }
            }
            KitchenNotifyMessage notifyMessage = KitchenNotifyMessage.builder()
                    .orderId(order.getId())
                    .tableDisplayName(tableName)
                    .dishes(items)
                    .createTime(order.getCreateTime())
                    .remarks(order.getRemarks()).build();
            //使用本地事务发布消息
            applicationEventPublisher.publishEvent(notifyMessage);
            //将消息发入队列
            //mqProducer.sendKitchenNotify(notifyMessage);
            log.info("订单消息监听器发送消息成功：messageId={}",notifyMessage.getOrderId());
        }catch (Exception e) {
            log.info("后厨发送失败");
        }

    }
    //2.查询当前订单
    @Override
    public OrderDetailVO getOrderDetailVO(Long orderId) {
        log.info("查询订单详情，orederId={}",orderId);
        //1.查询订单主表
        Order order=orderMapper.selectById(orderId);
        if(order==null) {
            throw new BusinessException("订单不存在");
        }
        //越权访问检验
        //获取用户信息
        Long userId = UserContext.getCurrentUserId();
        Integer roleId = UserContext.getCurrentRoleId();
        /*判断该用户是否为顾客：如果顾客越权访问不属于自己的订单，
        则拦截，如果是管理员和员工，则通过，即可以查询所有订单*/
        boolean isNormalCustomer = (roleId == null || roleId == 0);
        if(isNormalCustomer && !order.getUserId().equals(userId)) {
            throw new BusinessException("该订单不属于你！");
        }
        //查询桌位信息
        Table table=tableMapper.selectById(order.getTableId());
        String tableDisplayName = table.getTableType()==0
                ? "桌号"+table.getId():table.getTableName();
        //3.查询订单明细列表
        LambdaQueryWrapper<OrderItem> itemWrapper=new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId,orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        //获取所有订单的订单详情数量
        int dishCount = orderItemMapper.selectList(itemWrapper).size();
        //4.查询所有菜品信息
        List<Long> dishIds = orderItems.stream()
                .map(OrderItem::getDishId)
                .distinct()
                .toList();
        List<Dish> dishList = dishMapper.selectBatchIds(dishIds);
        Map<Long,Dish> dishMap = dishList.stream()
                .collect(Collectors.toMap(Dish::getId, dish -> dish));
        //5.转换为VO,订单明细表的vo
        List<OrderItemDetailVO> itemVOList= orderItems.stream()
                .map(item -> {
                    Dish dish=dishMap.get(item.getDishId());
                    return OrderItemDetailVO.builder()
                            .id(item.getId())
                            .orderId(item.getOrderId())
                            .dishId(dish.getId())
                            .dishName(dish.getName())
                            .itemStatus(item.getItemStatus())
                            .itemStatusText(getItemStatusText(item.getItemStatus()))
                            .isPriority(item.getIsPriority())
                            .isPriorityText(item.getIsPriority()==0?"普通":"加急")
                            .createTime(item.getCreateTime())
                            .build();
                }).toList();
        //6.计算预计完成时间
        LocalDateTime estimatedFinishTime = order.getCreateTime() != null ? order.getCreateTime().plusMinutes(order.getTotalCookTime()) : LocalDateTime.now();
        //装入最后的OrderVO中
        return OrderDetailVO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .tableId(order.getTableId())
                .tableDisplayName(tableDisplayName)
                .totalPrice(order.getTotalPrice())
                .totalWeight(order.getTotalWeight())
                .totalCookTime(order.getTotalCookTime())
                .orderStatus(order.getOrderStatus())
                .createTime(order.getCreateTime())
                .orderStatusText(getOrderStatusText(order.getOrderStatus()))
                .payStatus(order.getPayStatus())//支付状态
                //支付状态文本
                .payStatusText(order.getPayStatus()==1 ? "已支付": "未支付")
                .dishCount(dishCount)
                .actualAmount(order.getActualAmount())
                //预计完成时间：总时间+下单时间
                .estimatedFinishTime(estimatedFinishTime)
                .remark(order.getRemarks())//备注信息
                //订单详情
                .orderItem(itemVOList)
                .build();

    }
    //查询某个用户所有订单、员工查询所有订单
    @Override
    public Page<OrderDetailVO> getMyOrders(Integer orderStatus, PageQueryDTO pageDTO) {
        log.info("查询当前用户订单列表，userId={},orderStatus={}", UserContext.getCurrentUserId(),orderStatus);
        //获取用户的userid+roleId来判断用户的身份
        Long userId = UserContext.getCurrentUserId();
        Integer roleId = UserContext.getCurrentRoleId();
        LambdaQueryWrapper<Order> orderWrapper=new LambdaQueryWrapper<>();
        //普通用户只能自己查询自己的单子
        if(roleId==null || roleId==0) {
            orderWrapper.eq(Order::getUserId,userId);
        }
        //如果指定了状态，就按照状态筛选
        if(orderStatus != null) {
            orderWrapper.eq(Order::getOrderStatus,orderStatus);

        }
        //结果按照倒序排列
        orderWrapper.orderByDesc(Order::getCreateTime);
        //用Page来分页查询
        Page<Order> orderPage = new Page<>(pageDTO.getPage(),pageDTO.getPageSize());
        orderMapper.selectPage(orderPage,orderWrapper);
        //只是针对这一页的订单做装配
        List<OrderDetailVO> listVO = orderPage.getRecords().stream()
                .map(order -> getOrderDetailVO(order.getId()))
                .toList();
        //封装结果
        Page<OrderDetailVO> orderDetailVOPage = new Page<>();
        orderDetailVOPage.setRecords(listVO);//给这个records来装配新的VO
        orderDetailVOPage.setTotal(orderPage.getTotal());//自动查询到的数据总数
        orderDetailVOPage.setSize(orderPage.getSize());//每页的大小
        orderDetailVOPage.setCurrent(orderPage.getCurrent());//当前页面
        return orderDetailVOPage;
    }
    //订单状态文本转换
    private String getOrderStatusText(Integer orderStatus) {
        if(orderStatus == null) {
            return "未知";
        }
        return switch (orderStatus){
            case -1 -> "已取消";
            case 0 -> "待接单";
            case 1 -> "处理中";
            case 2 -> "部分出餐";
            case 3 -> "全部出餐";
            case 4 -> "已结算";
            default -> "外星状态";
        };
    }

    //任务状态文本转换
    private String getItemStatusText(Integer itemStatus) {
        if(itemStatus == null) {
            return "未知";
        }
        return switch (itemStatus){
            case 0 -> "待接单";
            case 1 -> "制作中";
            case 2 -> "待配送";
            case 3 -> "已送达";
            case -1 -> "已退菜";
            default -> "外星状态";

        };
    }
}
