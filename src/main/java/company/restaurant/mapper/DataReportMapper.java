package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.DataReport;
import org.apache.ibatis.annotations.Mapper;
//每日数据汇总表mapper
@Mapper
public interface DataReportMapper extends BaseMapper<DataReport> {
}
