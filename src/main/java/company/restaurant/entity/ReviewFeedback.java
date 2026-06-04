package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("t_review_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFeedback {
    private Long id;
    private Long userId;
    private Integer rating;//星级评价
    private String content;
    private Integer type;//0:评价，1 反馈
}
