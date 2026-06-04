package company.restaurant.controller;

import company.restaurant.service.KitchenService;
import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.AcceptTaskDTO;
import company.restaurant.dto.FinishTaskDTO;
import company.restaurant.util.Result;
import company.restaurant.vo.KitchenTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//后厨模块控制器
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/kitchen")
@Tag(name = "厨师接单接口",description = "接单+制作")
public class KitchenController {
    private final KitchenService kitchenService;

    //1.获取待办列表
    //权限控制，只有员工能访问
    @RequireRole(roles = {1},description = "员工专用接口：查看待办任务")
    @Operation(summary = "查询待办")
    @GetMapping("/pending-task")
    public Result<List<KitchenTaskVO>> getPendingTasks() {
        List<KitchenTaskVO> kitchenTaskVOS=kitchenService.getPendingTasks();
        return Result.success(kitchenTaskVOS);
    }
    //2.接单接口
    @RequireRole(roles = {1},description = "员工专用：接单接口")
    @Operation(summary = "接单接口")
    @PostMapping("/accept-task")
    public Result<Void> acceptTask(@Valid @RequestBody AcceptTaskDTO itemId) {
        kitchenService.acceptTask(itemId.getItemId());
        return Result.success("接单成功");
    }
    //3.完成接口
    @RequireRole(roles = {1},description = "员工专用：完成接口")
    @Operation(summary = "完成接口")
    @PostMapping("/finish-task")
    public Result<Void> cancelTask(@Valid @RequestBody FinishTaskDTO finishTaskDTO) {
        kitchenService.finishTask(finishTaskDTO.getItemId());
        return Result.success("制作完成");
    }
    //4.查看我的任务接口
    @RequireRole(roles = {1},description = "员工接口：查看自己的订单")
    @Operation(summary = "查看自己任务接口")
    @GetMapping("/my-task")
    public Result<List<KitchenTaskVO>> getAllTasks() {
        List<KitchenTaskVO> myTask = kitchenService.getMyTasks();
        return Result.success(myTask);
    }
}
