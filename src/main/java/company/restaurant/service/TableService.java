package company.restaurant.service;

import company.restaurant.vo.TableListVO;

import java.util.List;

//查看桌子接口
public interface TableService {
    //获取所有桌子列表
    List<TableListVO> getTableList();
}
