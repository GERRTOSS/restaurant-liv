package company.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import company.restaurant.dto.PageQueryDTO;
import company.restaurant.service.OrderService;
import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.CreateOrderDTO;
import company.restaurant.util.Result;
import company.restaurant.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
//订单接口
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Tag(name = "订单接口")
public class OrderController {
    private final OrderService orderService;
    //创建订单
    //权限控制：只能顾客下单
    @RequireRole(roles = {0},description = "顾客专用接口：创建订单")
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderDetailVO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO){
        OrderDetailVO orderDetailVO = orderService.createOrder(createOrderDTO);
        return Result.success(orderDetailVO);

    }
    //查询当前订单
    @RequireRole(roles = {0})
    @Operation(summary = "查看当前订单")
    @PostMapping("/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId){
        log.info("查询订单详情，orderId={}",orderId);
        OrderDetailVO orderDetailVO = orderService.getOrderDetailVO(orderId);
        return Result.success(orderDetailVO);
    }
    //查询所有订单
    @RequireRole(roles = {0,1,2},description = "查看订单")
    @Operation(summary = "查看所有订单")
    @PostMapping("/orders")
    public Result<Page<OrderDetailVO>> getMyOrders(@RequestParam(required = false) Integer orderStatus , @Valid PageQueryDTO pageQueryDTO){
        log.info("查询我的订单列表，orderStatus={}",orderStatus);
        Page<OrderDetailVO> orderDetailVOList = orderService.getMyOrders(orderStatus,pageQueryDTO);
        return Result.success(orderDetailVOList);

    }
}