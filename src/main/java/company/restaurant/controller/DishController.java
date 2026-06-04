package company.restaurant.controller;

import company.restaurant.service.DishService;
import company.restaurant.util.Result;
import company.restaurant.vo.DishListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//菜品查询接口
@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
@Tag(name = "菜品查询",description = "主要是查询菜品")
public class DishController {
    private final DishService dishService;
    @Operation(summary = "菜品查询")
    @GetMapping("/list")
    public Result<List<DishListVO>> getDishList(@RequestParam(required = false)Integer categoryCode,
                                          @RequestParam(required = false) String keyword) {
        List<DishListVO> dishList = dishService.getDishList(categoryCode, keyword);
        return Result.success(dishList);


    }
}
