package company.restaurant.service;

import company.restaurant.dto.CreateCallDTO;
import company.restaurant.vo.CallLogVO;

import java.util.List;

//顾客呼叫方法接口
public interface CallLogService {
    //创建呼叫记录
    CallLogVO createCallLog(CreateCallDTO createCallDTO);
    //顾客查询
    List<CallLogVO> getMyCallLog();
    //员工查询
    List<CallLogVO> getAllCallLog();
    //员工完成
    void finishCallLog(Long callId);
}
