package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.CheckoutDTO;
import company.restaurant.entity.Coupon;
import company.restaurant.service.CashierService;
import company.restaurant.util.Result;
import company.restaurant.vo.ChcekoutVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//结账用的controller
@RestController
@Slf4j
@RequiredArgsConstructor
@RequireRole(roles = 1)
@RequestMapping("api/cashier")
@Tag(name = "结账接口",description = "用于结算")
public class CashierController {
    private final CashierService cashierService;

    //1.查询账单
    @GetMapping("/orders/{orderId}/bill")
    @Operation(summary = "查询账单")
    public Result<ChcekoutVO> getOrderBill(@PathVariable("orderId") Long orderId) {
        ChcekoutVO ch=cashierService.getOrderBill(orderId);
        return Result.success(ch);
    }
    //2.查看优惠券
    @PostMapping("/coupon/{userId}")
    @Operation(summary = "查询优惠券")
    public Result<List<Coupon>> getUserCoupons(@PathVariable("userId") Long userId) {
        List<Coupon> coupons=cashierService.getUserCoupons(userId);
        return Result.success(coupons);
    }
    //3.结算账单
    @PostMapping("/orders/checkout")
    @Operation(summary = "结算账单")
    public Result<ChcekoutVO> checkout(@RequestBody CheckoutDTO checkoutDTO) {
        ChcekoutVO ch=cashierService.checkout(checkoutDTO);
        return Result.success(ch);
    }
}
