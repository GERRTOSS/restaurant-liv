package company.restaurant.service;

import company.restaurant.vo.TableDeliveryGroupVO;
import company.restaurant.vo.WaiterDeliveryVO;

import java.util.List;

//传菜配送接口
public interface WaiterService {
    //1.查询传送单
    List<TableDeliveryGroupVO> getWaiterDeliveries();
    //2.接单配送
    void delivery(Long taskId);
    //3.确认送达
    void confirmDelivery(Long taskId);
    //4.查看自己的接单情况
    List<WaiterDeliveryVO> getMyDeliveries();
}
