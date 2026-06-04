package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.context.UserContext;
import company.restaurant.dto.SalaryPayDTO;
import company.restaurant.entity.SalaryRecord;
import company.restaurant.entity.StaffInfo;
import company.restaurant.entity.User;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.SalaryRecordMapper;
import company.restaurant.mapper.StaffInfoMapper;
import company.restaurant.mapper.UserMapper;
import company.restaurant.service.SalaryService;
import company.restaurant.vo.SalaryRecordVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

//发放记录的实现类
@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRecordMapper salaryRecordMapper;
    private final StaffInfoMapper staffInfoMapper;
    private final UserMapper userMapper;
    /**
     * 管理员发放工资
     * 业务逻辑：
     * 1. 验证员工是否存在
     * 2. 验证金额是否合法
     * 3. 写入工资记录
     */
    @Override
    public void createSalaryRecord(SalaryPayDTO salaryPayDTO) {
        //1.验证员工是否存在
        LambdaQueryWrapper<StaffInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StaffInfo::getUserId,salaryPayDTO.getStaffId());
        StaffInfo st = staffInfoMapper.selectOne(queryWrapper);
        if(st == null){
            throw new BusinessException("员工不存在");
        }
        //2.验证金额是否合法
        BigDecimal amount = salaryPayDTO.getAmount();
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("发放的金额不能为负数");
        }
        //3.写入
        SalaryRecord salaryRecord = new SalaryRecord();
        salaryRecord.setStaffId(salaryPayDTO.getStaffId());
        salaryRecord.setAdminId(UserContext.getCurrentUserId());
        salaryRecord.setAmount(amount);
        salaryRecord.setPayDate(LocalDateTime.now());
        salaryRecord.setRemark(salaryPayDTO.getRemark());
        salaryRecordMapper.insert(salaryRecord);
    }
    /**
     * 查询某员工的工资记录
     */
    @Override
    public List<SalaryRecordVO> getStaffSalaryRecords(Long staffId) {
        LambdaQueryWrapper<SalaryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryRecord::getStaffId, staffId)
                .orderByDesc(SalaryRecord::getPayDate);
        List<SalaryRecord> salaryRecords = salaryRecordMapper.selectList(queryWrapper);
        return buildVOList(salaryRecords);
    }
    /**
     * 查询所有工资记录
     */
    @Override
    public List<SalaryRecordVO> getAllSalaryRecords() {
        log.info("查询所有工资记录");
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SalaryRecord::getPayDate);
        List<SalaryRecord> records = salaryRecordMapper.selectList(wrapper);
        return buildVOList(records);
    }
    //装配为VO
    private List<SalaryRecordVO> buildVOList(List<SalaryRecord> records) {
        if(records == null || records.isEmpty()){
            return new ArrayList<>();
        }
        //批量查询用户名
        Set<Long> staffIds = records.stream()
                .map(SalaryRecord::getStaffId)
                .collect(Collectors.toSet());
        List<User> users = userMapper.selectBatchIds(staffIds);
        Map<Long,String> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        return records.stream()
                .map(r-> SalaryRecordVO.builder()
                        .id(r.getId())
                        .staffId(r.getStaffId())
                        .staffName(userMap.getOrDefault(r.getStaffId(),"未知"))
                        .amount(r.getAmount())
                        .payDate(r.getPayDate())
                        .adminId(r.getAdminId())
                        .remark(r.getRemark())
                        .createTime(r.getCreateTime())
                        .build())
                .toList();
    }
}
