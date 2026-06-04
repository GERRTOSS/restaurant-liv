package company.restaurant.mapper;

import company.restaurant.vo.DailyRevenueVO;
import company.restaurant.vo.HotDishVO;
import company.restaurant.vo.OrderItemTimeStatsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 热销榜（v_dish_popularity_rank）
     * 列名对应: dish_id, dish_name, sales_volume
     */
    @Select("SELECT dish_id AS dishId, dish_name AS dishName, sales_volume AS salesVolume " +
            "FROM v_dish_popularity_rank LIMIT #{topN}")
    List<HotDishVO> getHotDishes(@Param("topN") int topN);

    /**
     * 每日营收流水（v_daily_sales_performance）
     * 列名对应: sales_date, sales_hour, hourly_revenue, hourly_weight, order_count
     */
    @Select("<script>" +
            "SELECT sales_date AS salesDate, sales_hour AS salesHour, " +
            "       hourly_revenue AS hourlyRevenue, hourly_weight AS hourlyWeight, " +
            "       order_count AS orderCount " +
            "FROM v_daily_sales_performance " +
            "<where>" +
            "  <if test='startDate != null'>AND sales_date &gt;= #{startDate}</if>" +
            "  <if test='endDate != null'>AND sales_date &lt;= #{endDate}</if>" +
            "</where>" +
            "ORDER BY salesDate DESC, salesHour DESC" +
            "</script>")
    List<DailyRevenueVO> getDailyRevenue(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 员工效能分析（v_order_item_time_stats）
     * 列名对应: task_id, order_id, dish_id, chef_id, waiter_id,
     *          wait_accept_minutes, cook_minutes, deliver_minutes, total_minutes
     */
    @Select("SELECT task_id AS taskId, order_id AS orderId, dish_id AS dishId, " +
            "       chef_id AS chefId, waiter_id AS waiterId, " +
            "       wait_accept_minutes AS waitAcceptMinutes, " +
            "       cook_minutes AS cookMinutes, " +
            "       deliver_minutes AS deliverMinutes, " +
            "       total_minutes AS totalMinutes " +
            "FROM v_order_item_time_stats " +
            "ORDER BY taskId")
    List<OrderItemTimeStatsVO> getTimeAnalysis();
}