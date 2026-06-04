package company.restaurant.controller;

import company.restaurant.service.TableService;
import company.restaurant.util.Result;
import company.restaurant.vo.TableListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//桌位控制器
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/table")
@Tag(name = "桌位查询",description = "")
public class TableController {
    private final TableService tableService;
    @Operation(summary = "查询桌位")
    @GetMapping("/list")
    public Result<List<TableListVO>> getTables() {
        List<TableListVO> tableList = tableService.getTableList();
        return Result.success(tableList);
    }

}
