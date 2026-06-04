package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

//评价反馈返回VO
@Data
@Builder
public class ReviewFeedbackVO {
    private Long id;
    private Long userId;
    private Integer rating;
    private String ratingText;
    private String content;
    private Integer type;
    private String typeText;//评价。反馈

}
