package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.SalaryPayDTO;
import company.restaurant.service.SalaryService;
import company.restaurant.util.Result;
import company.restaurant.vo.SalaryRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//工资管理接口
@RestController
@RequestMapping("/api/admin/salary")
@RequiredArgsConstructor

@Tag(name = "工资管理接口")
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * 发放工资
     * POST /api/admin/salary/pay
     */
    @RequireRole(roles = {2})
    @Operation(summary = "发放工资接口")
    @PostMapping("/pay")
    public Result<Void> paySalary(@Valid @RequestBody SalaryPayDTO dto) {
        salaryService.createSalaryRecord(dto);
        return Result.success("工资发放成功");
    }

    /**
     * 查询某员工工资记录
     * GET /api/admin/salary/staff/{staffId}
     */
    @RequireRole(roles = {1,2})
    @Operation(summary = "查询某个接口")
    @GetMapping("/staff/{staffId}")//staffId就是用户id
    public Result<List<SalaryRecordVO>> getStaffSalaryRecords(@PathVariable Long staffId) {
        return Result.success(salaryService.getStaffSalaryRecords(staffId));
    }

    /**
     * 查询所有工资记录
     * GET /api/admin/salary/all
     */
    @RequireRole(roles = {2})
    @Operation(summary = "查询所有接口")
    @GetMapping("/all")
    public Result<List<SalaryRecordVO>> getAllSalaryRecords() {
        return Result.success(salaryService.getAllSalaryRecords());
    }
}
