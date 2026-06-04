package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.service.ReportService;
import company.restaurant.util.Result;
import company.restaurant.vo.DailyRevenueVO;
import company.restaurant.vo.HotDishVO;
import company.restaurant.vo.OrderItemTimeStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
//数据分析接口
@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
@Tag(name = "数据分析",description = "热销榜+营收+效能")
public class ReportController {

    private final ReportService reportService;

    /**
     * 热销榜
     * GET /api/admin/report/hot?topN=10
     */
    @Operation(summary = "热销榜查询")
    @GetMapping("/hot")
    public Result<List<HotDishVO>> getHotDishes(
            @RequestParam(defaultValue = "10") int topN) {
        return Result.success(reportService.getHotDishes(topN));
    }

    /**
     * 每日营收流水
     * GET /api/admin/report/revenue?startDate=2025-01-01&endDate=2025-12-31
     */
    @Operation(summary = "营收流水查询")
    @GetMapping("/revenue")
    @RequireRole(roles = {1,2})
    public Result<List<DailyRevenueVO>> getDailyRevenue(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(reportService.getDailyRevenue(startDate, endDate));
    }

    /**
     * 员工效能分析
     * GET /api/admin/report/time
     */
    @Operation(summary = "员工效能分析")
    @GetMapping("/time")
    @RequireRole(roles = {2})
    public Result<List<OrderItemTimeStatsVO>> getTimeAnalysis() {
        return Result.success(reportService.getTimeAnalysis());
    }
}
