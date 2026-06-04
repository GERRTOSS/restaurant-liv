package company.restaurant.service;

import company.restaurant.dto.ProcurementDTO;
import company.restaurant.vo.ProcurementVO;

import java.util.List;

//采购记录接口
public interface ProcurementService {
    //1.员工提交采购记录
    void addProcurement(ProcurementDTO procurementDTO);
    //查询所有的采购记录
    List<ProcurementVO> getProcurements();
}
