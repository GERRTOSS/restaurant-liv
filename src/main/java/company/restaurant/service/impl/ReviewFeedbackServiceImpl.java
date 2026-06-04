package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.context.UserContext;
import company.restaurant.dto.CreateReviewDTO;
import company.restaurant.entity.ReviewFeedback;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.ReviewFeedbackMapper;
import company.restaurant.service.ReviewFeedbackService;
import company.restaurant.vo.ReviewFeedbackVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//顾客反馈实现类
@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewFeedbackServiceImpl implements ReviewFeedbackService {
    private final ReviewFeedbackMapper reviewFeedbackMapper;
    /**
     * 顾客提交评价或反馈
     * 业务逻辑：
     * 1. 如果是评价（type=0），rating 必须填写
     * 2. 如果是反馈（type=1），rating 可以不填
     * 3. 写入数据库
     */
    @Override
    public ReviewFeedbackVO create(CreateReviewDTO createReviewDTO) {
        //1.先获取type
        Integer type = createReviewDTO.getType();
        //2.判断是否是评价，如果是评价，就必须要星级，如果不是，就直接填写
        if(type == 0){
            if(createReviewDTO.getRating() == null){
                throw new BusinessException("评价必须需要星级");
            }
        }
        //3.填充实体，最后调用mapper装入数据库
        ReviewFeedback reviewFeedback = new ReviewFeedback();
        reviewFeedback.setType(type);
        reviewFeedback.setUserId(UserContext.getCurrentUserId());
        reviewFeedback.setRating(createReviewDTO.getRating());
        reviewFeedback.setContent(createReviewDTO.getContent());
        reviewFeedbackMapper.insert(reviewFeedback);
        return toVO(reviewFeedback);
    }

    @Override
    public List<ReviewFeedbackVO> getMyReviews() {
        //1.获取userId
        Long userId = UserContext.getCurrentUserId();
        //2.使用MP提供的删选条件方法进行删选，而后装入List中
        LambdaQueryWrapper<ReviewFeedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewFeedback::getUserId, userId)
                .orderByDesc(ReviewFeedback::getRating);
        List<ReviewFeedback> reviewFeedbacks = reviewFeedbackMapper.selectList(queryWrapper);
        //装入VO
        return reviewFeedbacks
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public List<ReviewFeedbackVO> getAllReviews(Integer type) {
        LambdaQueryWrapper<ReviewFeedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewFeedback::getType, type);
        List<ReviewFeedback> reviewFeedbacks = reviewFeedbackMapper.selectList(queryWrapper);
        //返回VO
        return reviewFeedbacks
                .stream()
                .map(this::toVO)
                .toList();
    }
    //转换为VO
    private ReviewFeedbackVO toVO(ReviewFeedback reviewFeedback) {
        String ratingText = reviewFeedback.getType() == 0 ?
                reviewFeedback.getRating()+"星级" : "此为反馈无星级";
        return ReviewFeedbackVO.builder()
                .type(reviewFeedback.getType())
                .rating(reviewFeedback.getRating())
                .ratingText(ratingText)
                .content(reviewFeedback.getContent())
                .build();
    }
}
