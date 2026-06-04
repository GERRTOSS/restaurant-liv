package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.Procurement;
import org.apache.ibatis.annotations.Mapper;
//采购表mapper
@Mapper
public interface ProcurementMapper extends BaseMapper<Procurement> {
}
