package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//优惠劵表
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    //查询可用的优惠劵
    @Select("SELECT * FROM t_coupon WHERE user_id = #{userId} AND is_used = 0")
    List<Coupon> selectAvailableCouponsByUserId(Long userId);
    //调用存储过程发放优惠券
    @Select("CALL sp_issue_coupon_for_order(#{orderId},#{userId})")
    void issueCoupon(@Param("orderId")Long orderId, @Param("userId")Long userId);

}
