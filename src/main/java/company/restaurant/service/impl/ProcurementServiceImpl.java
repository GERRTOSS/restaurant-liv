package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.context.UserContext;
import company.restaurant.dto.ProcurementDTO;
import company.restaurant.entity.Procurement;
import company.restaurant.entity.User;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.ProcurementMapper;
import company.restaurant.mapper.UserMapper;
import company.restaurant.service.ProcurementService;
import company.restaurant.vo.ProcurementVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//采购表Impl
@Service
@RequiredArgsConstructor
@Slf4j
public class ProcurementServiceImpl implements ProcurementService {
    private final UserMapper userMapper;
    private final ProcurementMapper procurementMapper;
    /**
     * 员工提交采购记录
     * staffId 从 UserContext 取，不信任前端传入
     */
    @Override
    public void addProcurement(ProcurementDTO procurementDTO) {
        Long staffId = UserContext.getCurrentUserId();
        log.info("员工提交采购记录，staffId:{}", staffId);
        if(procurementDTO.getTotalCost().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("采购金额必须大于0");
        }
        Procurement procurement = new Procurement();
        procurement.setStaffId(staffId);
        procurement.setContent(procurementDTO.getContent());
        procurement.setTotalCost(procurementDTO.getTotalCost());
        procurement.setCreateTime(LocalDateTime.now());
        procurementMapper.insert(procurement);
        log.info("采购记录提交完毕,staffId:{}", staffId);
    }
    /**
     * 查询所有采购记录，按时间倒序
     */
    @Override
    public List<ProcurementVO> getProcurements() {
        log.info("查询所有采购记录");
        LambdaQueryWrapper<Procurement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Procurement::getCreateTime);
        List<Procurement> procurements = procurementMapper.selectList(wrapper);
        if(procurements.isEmpty()){
            return List.of();
        }
        //批量查询员工姓名，避免N+1
        Set<Long> staffIds = procurements.stream()
                .map(Procurement::getStaffId)
                .collect(Collectors.toSet());
        List<User> userList = userMapper.selectBatchIds(staffIds);
        Map<Long,String> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId,User::getUsername));
        return procurements.stream()
                .map(procurement -> ProcurementVO.builder()
                        .id(procurement.getId())
                        .staffId(procurement.getStaffId())
                        .staffName(userMap.getOrDefault(procurement.getStaffId(),"未知"))
                        .content(procurement.getContent())
                        .createTime(procurement.getCreateTime())
                        .totalCost(procurement.getTotalCost())
                        .build())
                .toList();
    }
}

