package company.restaurant.service.impl;

import company.restaurant.constant.CacheConstants;
import company.restaurant.mapper.ReportMapper;
import company.restaurant.service.ReportService;
import company.restaurant.util.CacheTool;
import company.restaurant.vo.DailyRevenueVO;
import company.restaurant.vo.HotDishVO;
import company.restaurant.vo.OrderItemTimeStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
//管理员三个数据流水的实现类
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final CacheTool cacheTool;
    //热销榜+Redis版本
    @Override
    public List<HotDishVO> getHotDishes(int topN) {
        //这里暂时用已经定义好的DISH的获取缓存的方式，稍后自己更换新的
        String key = CacheConstants.HOT_DISHES_KEY+":"+topN;
        String lockKey = "lock:"+key;
        return cacheTool.getWithLock(
                key,lockKey,()->{
                    log.info("缓存未命中，从数据库查询热销榜");
                    log.info("查询热销榜 TOP {}", topN);
                    return reportMapper.getHotDishes(topN);
                },List.class);
    }

    @Override
    public List<DailyRevenueVO> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        log.info("查询每日流水，startDate={}, endDate={}", startDate, endDate);
        return reportMapper.getDailyRevenue(startDate, endDate);
    }

    @Override
    public List<OrderItemTimeStatsVO> getTimeAnalysis() {
        log.info("查询员工效能分析");
        return reportMapper.getTimeAnalysis();
    }
}
