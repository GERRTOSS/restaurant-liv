package company.restaurant.controller;

import company.restaurant.annotation.RequireRole;
import company.restaurant.dto.CreateReviewDTO;
import company.restaurant.entity.ReviewFeedback;
import company.restaurant.service.ReviewFeedbackService;
import company.restaurant.util.Result;
import company.restaurant.vo.ReviewFeedbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//评价反馈控制层
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/review")
@Tag(name = "评价反馈",description = "")
public class ReviewFeedbackController {
    private final ReviewFeedbackService reviewFeedbackService;

    //1.创建评价
    @PostMapping
    @Operation(summary = "创建评价")
    @RequireRole(roles = {0})
    public Result<ReviewFeedbackVO> create(@Valid @RequestBody CreateReviewDTO createReviewDTO) {
        ReviewFeedbackVO reviewFeedback = reviewFeedbackService.create(createReviewDTO);
        return Result.success(reviewFeedback);
    }
    /**
     * 顾客查看自己的评价/反馈记录
     * GET /api/review/my
     */
    @GetMapping("/my")
    @Operation(summary = "查看自己的评价")
    @RequireRole(roles = {0})
    public Result<List<ReviewFeedbackVO>> getMyReviews() {
        return Result.success(reviewFeedbackService.getMyReviews());
    }

    /**
     * 管理员查看所有评价/反馈（可按类型筛选）
     * GET /api/review/all?type=0
     * type 不传则查全部，0=评价，1=反馈
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有的评价")
    @RequireRole(roles = {2})
    public Result<List<ReviewFeedbackVO>> getAll(
            @RequestParam(required = false) Integer type) {
        return Result.success(reviewFeedbackService.getAllReviews(type));
    }
}
