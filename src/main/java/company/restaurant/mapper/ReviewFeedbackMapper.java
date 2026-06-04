package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.ReviewFeedback;
import org.apache.ibatis.annotations.Mapper;
//评价反馈表mapper
@Mapper
public interface ReviewFeedbackMapper extends BaseMapper<ReviewFeedback> {
}
