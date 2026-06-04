package company.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import company.restaurant.dto.CreateOrderDTO;
import company.restaurant.dto.PageQueryDTO;
import company.restaurant.vo.OrderDetailVO;


import java.util.List;

//订单相关接口
public interface OrderService {
    //1.创建订单
    OrderDetailVO createOrder(CreateOrderDTO createOrderDTO);
    //2.获取当前订单
    OrderDetailVO getOrderDetailVO(Long orderId);
    //3.获取用户的所有订单
    Page<OrderDetailVO> getMyOrders(Integer orderStatus, PageQueryDTO pageDTO);
}
