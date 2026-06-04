package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.CreateCallDTO;
import company.restaurant.service.CallLogService;
import company.restaurant.util.Result;
import company.restaurant.vo.CallLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//呼叫处理接口
@RestController
@Tag(name="呼叫处理")
@RequiredArgsConstructor
@RequestMapping("/api/call-logs")
public class CallLogController {
    private final CallLogService callLogService;
    @Operation(summary = "创建呼叫")
    @RequireRole(roles = {0})
    @PostMapping
    public Result<CallLogVO> createCallLog(@RequestBody CreateCallDTO createCallDTO) {
        CallLogVO  callLogVO = callLogService.createCallLog(createCallDTO);
        return Result.success(callLogVO);
    }
    @GetMapping("/my")
    @RequireRole(roles = {0,1})
    @Operation(summary = "查询自己呼叫")
    public Result<List<CallLogVO>> listCallLog() {
        List<CallLogVO> callLogVOS = callLogService.getMyCallLog();
        return Result.success(callLogVOS);
    }
    @GetMapping("/pending")
    @RequireRole(roles = {1})
    @Operation(summary = "查询所有待处理的呼叫")
    public Result<List<CallLogVO>> getAllCallLog() {
        List<CallLogVO> callLogVOS = callLogService.getAllCallLog();
        return Result.success(callLogVOS);
    }
    @PostMapping("/{callLogId}/finish")
    @RequireRole(roles = {1})
    @Operation(summary = "处理用户呼叫接口")
    public Result<Void> finishCallLog(@PathVariable("callLogId") Long collLogId) {
        callLogService.finishCallLog(collLogId);
        return Result.success("处理用户呼叫成功");
    }
}

