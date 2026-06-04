package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.CallLog;
import org.apache.ibatis.annotations.Mapper;

//呼叫记录表mapper
@Mapper
public interface CallLogMapper extends BaseMapper<CallLog> {
}
