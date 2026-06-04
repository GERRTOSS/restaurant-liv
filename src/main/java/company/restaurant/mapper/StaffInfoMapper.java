package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.StaffInfo;
import org.apache.ibatis.annotations.Mapper;
//员工扩展表
@Mapper
public interface StaffInfoMapper  extends BaseMapper<StaffInfo> {
}
