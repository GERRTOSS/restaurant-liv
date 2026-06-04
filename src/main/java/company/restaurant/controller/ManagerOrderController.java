package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.service.ManagerOrderService;
import company.restaurant.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//大堂经理的两个方法
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/orders")
@Tag(name = "经理方法",description = "催单+退菜")
public class ManagerOrderController {
    private final ManagerOrderService managerOrderService;
    //退菜
    @RequireRole(roles = {1},description = "员工专用接口")
    @Operation(summary = "退菜接口")
    @PostMapping("/{orderId}/items/{itemId}/cancel")
    public Result<String> cancelOrder(@PathVariable Long orderId,@PathVariable Long itemId) {
        managerOrderService.cancelItem(orderId, itemId);
        return Result.success("退菜成功");

    }
    //催单
    @RequireRole(roles = {1},description = "员工专用接口")
    @Operation(summary = "催单接口")
    @PostMapping("/items/{itemId}/urge")
    public Result<String> urgeOrderItem(@PathVariable Long itemId) {
        managerOrderService.urgeOrderItem(itemId);
        return Result.success("催单成功");
    }

}
