package company.restaurant.service;

import company.restaurant.vo.DishListVO;

import java.util.List;

//菜品服务接口

public interface DishService {
    /**
     * 获取菜品列表（支持分类筛选和关键词搜索）
     * @param categoryCode 分类（可选）：0主食/1果蔬/2鱼肉/3酒水
     * @param keyword 关键词（可选）：按菜品名称模糊查询
     * @return 菜品列表
     */
    List<DishListVO> getDishList(Integer categoryCode, String keyword);
}
