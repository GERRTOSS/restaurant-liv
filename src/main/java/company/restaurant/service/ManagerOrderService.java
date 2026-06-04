package company.restaurant.service;
//大堂经理的两个方法的接口
public interface ManagerOrderService {
    //1.退菜
    void cancelItem(Long orderId,Long itemId);

    //2.催单
    void urgeOrderItem(Long itemId);
}
