package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.Table;
import org.apache.ibatis.annotations.Mapper;
//桌号表mapper
@Mapper
public interface TableMapper extends BaseMapper<Table> {

}
