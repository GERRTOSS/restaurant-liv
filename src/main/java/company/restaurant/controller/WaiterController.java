package company.restaurant.controller;

import company.restaurant.dto.ConfirmDeliveryDTO;
import company.restaurant.dto.DeliveryDTO;
import company.restaurant.service.WaiterService;
import company.restaurant.annotation.RequireRole;
import company.restaurant.util.Result;
import company.restaurant.vo.TableDeliveryGroupVO;
import company.restaurant.vo.WaiterDeliveryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//传菜员接口
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/waiter")
@Tag(name = "传菜接口",description = "")
public class WaiterController {
    private final WaiterService waiterService;
    //1.获取待配送的任务
    @RequireRole(roles = {1},description = "员工专用接口：查询传菜任务")
    @Operation(summary = "获取任务")
    @GetMapping("/pending-deliveries")
    public Result<List<TableDeliveryGroupVO>> getPendingDelivery() {
        List<TableDeliveryGroupVO> tableDeliveryGroupVOS = waiterService.getWaiterDeliveries();
        return Result.success(tableDeliveryGroupVOS);

    }
    //2.传菜接单
    @RequireRole(roles = {1},description = "员工专用接口，传菜接单")
    @Operation(summary = "接单任务")
    @PostMapping("/delivery")
    public Result<Void> delivery(@RequestBody DeliveryDTO deliveryDTO) {
        waiterService.delivery(deliveryDTO.getId());
        return Result.success("接单成功");
    }
    //3.确认接口
    @RequireRole(roles = {1},description = "员工专用接口：传菜成功")
    @Operation(summary = "确认接口")
    @PostMapping("/confirm-delivery")
    public Result<Void> confirmDelivery(@RequestBody ConfirmDeliveryDTO confirmDeliveryDTO) {
        waiterService.confirmDelivery(confirmDeliveryDTO.getId());
        return Result.success("确认成功");
    }
    //4.查看个人任务接口
        @RequireRole(roles = {1},description = "员工专用接口，查看个人传菜接单情况")
        @Operation(summary = "查看个人任务接口")
        @GetMapping("/my-deliveries")
    public Result<List<WaiterDeliveryVO>> getMyDeliveries() {
        List<WaiterDeliveryVO> waiterDeliveryVOS = waiterService.getMyDeliveries();
        return Result.success(waiterDeliveryVOS);
    }

}
