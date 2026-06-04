package company.restaurant.service;

import company.restaurant.dto.CreateReviewDTO;
import company.restaurant.vo.ReviewFeedbackVO;

import java.util.List;

//评价反馈接口
public interface ReviewFeedbackService {
    //1.顾客提交评价或者反馈
    ReviewFeedbackVO create(CreateReviewDTO createReviewDTO);
    //2.顾客个人查看
    List<ReviewFeedbackVO> getMyReviews();
    //3.管理员查看
    List<ReviewFeedbackVO> getAllReviews(Integer type);
}
