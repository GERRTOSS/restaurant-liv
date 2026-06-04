package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.SalaryRecord;
import org.apache.ibatis.annotations.Mapper;
//工资发放记录表mapper
@Mapper
public interface SalaryRecordMapper extends BaseMapper<SalaryRecord> {
}
