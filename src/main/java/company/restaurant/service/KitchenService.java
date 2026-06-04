package company.restaurant.service;

import company.restaurant.vo.KitchenTaskVO;

import java.util.List;

//后厨模块的方法接口
public interface KitchenService {
    //1.查看厨师待看面板
    List<KitchenTaskVO> getPendingTasks();
    //2.接单操作
    //Prams:任务明细ID
    void acceptTask(Long taskId);
    //3.制作完成操作
    void finishTask(Long taskId);
    //4.查询自己的所有接单任务
    List<KitchenTaskVO> getMyTasks();

}
