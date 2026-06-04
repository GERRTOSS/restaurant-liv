package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.ProcurementDTO;
import company.restaurant.service.ProcurementService;
import company.restaurant.util.Result;
import company.restaurant.vo.ProcurementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//采购记录模块
@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
@Tag(name = "采购记录",description = "")
@RequireRole(roles = {1,2})
public class ProcurementController {

    private final ProcurementService procurementService;

    /**
     * 员工提交采购记录
     * POST /api/procurement
     */
    @Operation(summary = "提交采购")
    @PostMapping
    @RequireRole(roles = {1},description = "员工专用接口")
    public Result<Void> addProcurement(@Valid @RequestBody ProcurementDTO dto) {
        procurementService.addProcurement(dto);
        return Result.success("采购记录提交成功");
    }

    /**
     * 管理员查询所有采购记录
     * GET /api/procurement/all
     */
    @Operation(summary = "查看采购")
    @GetMapping("/all")
    @RequireRole(roles = {2})
    public Result<List<ProcurementVO>> getAllProcurements() {
        return Result.success(procurementService.getProcurements());
    }
}