package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.CreatTableDTO;
import company.restaurant.dto.DishManageDTO;
import company.restaurant.dto.EmployeeManageDTO;
import company.restaurant.entity.Dish;
import company.restaurant.service.AdminService;
import company.restaurant.util.Result;
import company.restaurant.vo.EmployeeVO;
import company.restaurant.vo.TableListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireRole(roles = {2})
@Tag(name = "管理员接口", description = "增删改查")
public class AdminController {

    private final AdminService adminService;

    // ==================== 菜品管理 ====================

    /**
     * 新增菜品
     * POST /api/admin/dish
     */
    @Operation(summary = "新增菜品")
    @PostMapping("/dish")
    public Result<Void> addDish(@Valid @RequestBody DishManageDTO dto) {
        adminService.addDish(dto);
        return Result.success("菜品创建成功");
    }

    /**
     * 更新菜品
     * PUT /api/admin/dish
     */
    @Operation(summary = "更新菜品")
    @PutMapping("/dish")
    public Result<Void> updateDish(@Valid @RequestBody DishManageDTO dto) {
        adminService.updateDish(dto);
        return Result.success("菜品更新成功");
    }

    /**
     * 删除菜品
     * DELETE /api/admin/dish/{id}
     */
    @Operation(summary = "删除菜品")
    @DeleteMapping("/dish/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        DishManageDTO dto = new DishManageDTO();
        dto.setId(id);
        adminService.deleteDish(dto);
        return Result.success("菜品删除成功");
    }

    /**
     * 获取所有菜品列表
     * GET /api/admin/dish/list
     */
    @Operation(summary = "获取菜品列表")
    @GetMapping("/dish/list")
    public Result<List<Dish>> getDishList() {
        log.info("发送菜单列表数据：result={}", Result.success(adminService.getDishList()));
        return Result.success(adminService.getDishList());
    }

    // ==================== 员工管理 ====================

    /**
     * 新增员工
     * POST /api/admin/employee
     */
    @Operation(summary = "新增员工")
    @PostMapping("/employee")
    public Result<Void> addEmployee(@Valid @RequestBody EmployeeManageDTO dto) {
        adminService.addEmployee(dto);
        return Result.success("员工创建成功");
    }

    /**
     * 更新员工
     * PUT /api/admin/employee
     */
    @Operation(summary = "更新员工")
    @PutMapping("/employee")
    public Result<Void> updateEmployee(@Valid @RequestBody EmployeeManageDTO dto) {
        adminService.updateEmployee(dto);
        return Result.success("员工更新成功");
    }

    /**
     * 删除员工
     * DELETE /api/admin/employee/{id}
     */
    @Operation(summary = "删除员工")
    @DeleteMapping("/employee/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        EmployeeManageDTO dto = new EmployeeManageDTO();
        dto.setId(id);
        adminService.deleteEmployee(dto);
        return Result.success("员工删除成功");
    }

    /**
     * 获取员工列表
     * GET /api/admin/employee/list
     */
    @Operation(summary = "获取员工列表")
    @GetMapping("/employee/list")
    public Result<List<EmployeeVO>> getEmployeeList() {
        return Result.success(adminService.getEmployeeList());
    }

    /**
     * 新增桌位
     * POST /api/admin/table
     */
    @Operation(summary = "新增桌位")
    @PostMapping("/table")
    public Result<Void> addTable(@RequestBody @Valid CreatTableDTO creatTableDTO) {
        adminService.addTable(creatTableDTO);
        return Result.success("桌位新增成功");
    }

    /**
     * 更新桌位
     * PUT /api/admin/table/{id}
     */
    @Operation(summary = "更新桌位")
    @PutMapping("/table/{id}")
    public Result<Void> updateTable(@PathVariable Long id, @RequestBody @Valid CreatTableDTO creatTableDTO) {
        adminService.updateTable(id, creatTableDTO);
        return Result.success("桌位更新成功");
    }

    /**
     * 删除桌位
     * DELETE /api/admin/table/{id}
     */
    @Operation(summary = "删除桌位")
    @DeleteMapping("/table/{id}")
    public Result<Void> deleteTable(@PathVariable Long id) {
        adminService.deleteTable(id);
        return Result.success("桌位删除成功");
    }

    /**
     * 查看桌位列表
     * GET /api/admin/table
     */
    @Operation(summary = "查看桌位列表")
    @GetMapping("/table")
    public Result<List<TableListVO>> getTableList() {
        List<TableListVO> tableList = adminService.getTableList();
        return Result.success(tableList);
    }
}