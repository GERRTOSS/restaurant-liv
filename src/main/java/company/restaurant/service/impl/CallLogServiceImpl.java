package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.context.UserContext;
import company.restaurant.dto.CreateCallDTO;
import company.restaurant.entity.CallLog;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.CallLogMapper;
import company.restaurant.service.CallLogService;
import company.restaurant.vo.CallLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
//顾客呼叫实现类
public class CallLogServiceImpl implements CallLogService {
    private final CallLogMapper callLogMapper;
    /**
     * 顾客发起呼叫
     * 业务逻辑：从 UserContext 取当前用户ID，写入 t_call_log，finishTime 和 handlerId 留空
     */
    @Override
    public CallLogVO createCallLog(CreateCallDTO createCallDTO) {
        Long userId = UserContext.getCurrentUserId();
        CallLog callLog = new CallLog();
        callLog.setUserId(userId);
        callLog.setContent(createCallDTO.getContent());
        callLog.setCreateTime(LocalDateTime.now());
        callLogMapper.insert(callLog);
        return toCallLogVO(callLog);
    }
    /**
     * 顾客查看自己的呼叫记录（按时间倒序）
     */
    @Override
    public List<CallLogVO> getMyCallLog() {
        //获取当前用户的信息
        Long userId = UserContext.getCurrentUserId();
        Integer roleId = UserContext.getCurrentRoleId();
        LambdaQueryWrapper<CallLog> queryWrapper = new LambdaQueryWrapper<>();
        if (roleId == 0) {
            queryWrapper.eq(CallLog::getUserId, userId)
                    .orderByDesc(CallLog::getCreateTime);
        }
        List<CallLog> callLogs = callLogMapper.selectList(queryWrapper);
        return callLogs.stream()
                .map(this::toCallLogVO)
                .toList();
    }
    /**
     * 员工查看所有待处理的呼叫（finishTime 为 null 即待处理）
     */
    @Override
    public List<CallLogVO> getAllCallLog() {
        LambdaQueryWrapper<CallLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(CallLog::getFinishTime)
                .orderByAsc(CallLog::getCreateTime);
        return callLogMapper.selectList(queryWrapper).stream()
                //这里的this还是代替的是本类，也就是类：：方法名，参数是被隐式传入，原来的写法应当是：callLog->this.toCallLog(callLog)
                .map(this::toCallLogVO)
                .toList();
    }
    /**
     * 员工处理完成某条呼叫
     * 业务逻辑：
     * 1. 验证呼叫记录存在
     * 2. 验证是否已处理（防止重复操作）
     * 3. 写入 handlerId 和 finishTime
     */
    @Override
    public void finishCallLog(Long callId) {
        //1.
        CallLog callLog = callLogMapper.selectById(callId);
        if (callLog == null) {
            throw new BusinessException("该记录不存在");
        }
        //2.
        if(callLog.getFinishTime() != null) {
            throw new BusinessException("该单子已经被接了");
        }
        //3.
        callLog.setFinishTime(LocalDateTime.now());
        callLog.setHandlerId(UserContext.getCurrentUserId());
        callLogMapper.updateById(callLog);
    }

    //写入逻辑，写入VO
    private CallLogVO toCallLogVO(CallLog callLog) {
        return CallLogVO.builder()
                .id(callLog.getId())
                .userId(UserContext.getCurrentUserId())
                .content(callLog.getContent())
                .createTime(callLog.getCreateTime())
                .finishTime(callLog.getFinishTime())
                .handlerId(callLog.getHandlerId())
                .statusText(callLog.getFinishTime() ==null ? "待接单":"已完成")
                .build();
    }
}
