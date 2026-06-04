package company.restaurant.service;

import company.restaurant.vo.DailyRevenueVO;
import company.restaurant.vo.HotDishVO;
import company.restaurant.vo.OrderItemTimeStatsVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<HotDishVO> getHotDishes(int topN);

    List<DailyRevenueVO> getDailyRevenue(LocalDate startDate, LocalDate endDate);

    List<OrderItemTimeStatsVO> getTimeAnalysis();
}