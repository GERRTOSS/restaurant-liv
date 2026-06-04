package company.restaurant.service;

import company.restaurant.dto.SalaryPayDTO;
import company.restaurant.vo.SalaryRecordVO;

import java.util.List;

//员工发放记录接口
public interface SalaryService {
    //1.创建发送
    void createSalaryRecord(SalaryPayDTO salaryPayDTO);
    //2.查询某个员工
    List<SalaryRecordVO> getStaffSalaryRecords(Long staffId);
    //3.管理员查看所有
    List<SalaryRecordVO> getAllSalaryRecords();
}
