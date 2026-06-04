package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import company.restaurant.constant.CacheConstants;
import company.restaurant.dto.CreatTableDTO;
import company.restaurant.dto.DishManageDTO;
import company.restaurant.dto.EmployeeManageDTO;
import company.restaurant.entity.Dish;
import company.restaurant.entity.StaffInfo;
import company.restaurant.entity.Table;
import company.restaurant.entity.User;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.DishMapper;
import company.restaurant.mapper.StaffInfoMapper;
import company.restaurant.mapper.TableMapper;
import company.restaurant.mapper.UserMapper;
import company.restaurant.service.AdminService;
import company.restaurant.util.CacheTool;
import company.restaurant.util.SecurityUtil;
import company.restaurant.vo.EmployeeVO;
import company.restaurant.vo.TableListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//员工接口实现类
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final DishMapper dishMapper;
    private final UserMapper userMapper;
    private final StaffInfoMapper staffInfoMapper;
    private final CacheTool cacheTool;
    private final TableMapper tableMapper;
    private final StringRedisTemplate stringRedisTemplate;
    //菜品管理
    //1.创建菜品
    @Override
    public void addDish(DishManageDTO dishManageDTO) {
        //1.检测传入的内容是否为空
        if (dishManageDTO == null) {
            throw new BusinessException("传入的信息为空");
        }
        //2.检测该菜品是否已经存在
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, dishManageDTO.getName());
        Long count = dishMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new BusinessException("菜品信息已经存在");
        }
        //3.创建菜品
        Dish dish = Dish.builder()
                .name(dishManageDTO.getName())
                .price(dishManageDTO.getPrice())
                .estWeight(dishManageDTO.getEstWeight())
                .cookTime(dishManageDTO.getCookTime())
                .stock(dishManageDTO.getStock())
                .attributeCode(dishManageDTO.getAttributeCode())
                .categoryCode(dishManageDTO.getCategoryCode())
                .expireTime(dishManageDTO.getExpireTime())
                .description(dishManageDTO.getDescription())
                .imageUrl(dishManageDTO.getImageUrl())
                .build();
        dishMapper.insert(dish);
        log.info("菜品创建成功,dishName={}",dish.getName());
        //使用redis删除缓存，以便更改用户看到的缓存的菜单
        cacheTool.evictByPrefix(CacheConstants.DISH_MENU_KEY_PREFIX);
        //将限时菜品库存写入redis
        log.info("将限时菜品数量写入redis");
        writeRedis(dishManageDTO.getAttributeCode(),dishManageDTO.getStock(),dish.getId());
    }
    //因为增加和更新都需要这个写入redis的逻辑，所以直接写成一个方法
    private void writeRedis(Integer code,Integer stocks,Long dishId) {
        //判断如果是限时菜品，将其写入redis中
        if(code==1){
            String stockKey = "dish:stock:"+dishId;
            //执行写入逻辑
            stringRedisTemplate.opsForValue().set(stockKey,String.valueOf(stocks));
        }
    }
    //2.更新菜品
    @Override
    public void updateDish(DishManageDTO dishManageDTO) {
        if (dishManageDTO == null) {
            throw new BusinessException("更新信息为空");
        }
        //1.查询该菜品是否存在
        Dish existingDish = dishMapper.selectById(dishManageDTO.getId());
        if (existingDish == null) {
            throw new BusinessException("菜品信息不存在");
        }
        //2.如果修改了菜名，先查询菜品是否存在
        if(! existingDish.getName().equals(dishManageDTO.getName())) {
            LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Dish::getName, dishManageDTO.getName())
                    .ne(Dish::getId, dishManageDTO.getId());
            Long count = dishMapper.selectCount(queryWrapper1);
            if(count > 0) {
                throw new BusinessException("您要修改的菜名已经存在");
            }
        }
        //更新菜品
        dishMapper.update(null,
                new LambdaUpdateWrapper<Dish>()
                        .set(Dish::getName, dishManageDTO.getName())
                        .set(Dish::getPrice, dishManageDTO.getPrice())
                        .set(Dish::getEstWeight, dishManageDTO.getEstWeight())
                        .set(Dish::getCookTime, dishManageDTO.getCookTime())
                        .set(Dish::getStock, dishManageDTO.getStock())
                        .set(Dish::getAttributeCode, dishManageDTO.getAttributeCode())
                        .set(Dish::getCategoryCode, dishManageDTO.getCategoryCode())
                        .set(Dish::getExpireTime, dishManageDTO.getExpireTime())
                        .set(Dish::getDescription, dishManageDTO.getDescription())
                        .set(Dish::getImageUrl, dishManageDTO.getImageUrl())
                        .eq(Dish::getId, dishManageDTO.getId())
        );
        log.info("菜品更新成功，dishId={}",dishManageDTO.getId());
        cacheTool.evictByPrefix(CacheConstants.DISH_MENU_KEY_PREFIX);
        //调用写入redis的逻辑
        log.info("更新时候将限时菜品数量写入redis");
        writeRedis(dishManageDTO.getAttributeCode(),dishManageDTO.getStock(),dishManageDTO.getId());
    }
    //3.删除菜品
    @Override
    public void deleteDish(DishManageDTO dishManageDTO) {
        if (dishManageDTO == null) {
            throw new BusinessException("参数错误");
        }
        //1.验证菜品是否存在
        Dish dish = dishMapper.selectById(dishManageDTO.getId());
        if (dish == null) {
            throw new BusinessException("菜品信息不存在");
        }
        dishMapper.deleteById(dish.getId());
        log.info("删除菜品成功，dishId={}",dishManageDTO.getId());
        cacheTool.evictByPrefix(CacheConstants.DISH_MENU_KEY_PREFIX);
    }
    //4.获取菜单列表
    @Override
    public List<Dish> getDishList() {
        log.info("获取菜品列表");
        return dishMapper.selectList(null);
    }
    //员工管理
    //1.新增员工
    @Override
    public void addEmployee(EmployeeManageDTO employeeManageDTO) {
        if (employeeManageDTO == null) {
            throw new BusinessException("员工插入内容为空");
        }
        //1.查询该员工是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, employeeManageDTO.getUsername())
                .ne(User::getRoleId, 0)
                .ne(User::getRoleId,2);
        Long count = userMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new BusinessException("员工已经存在，请另起名称");
        }
        //2.验证密码是否提供
        if(employeeManageDTO.getPassword()==null || employeeManageDTO.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        //3.创建员工
        User user = User.builder()
                .username(employeeManageDTO.getUsername())
                .password(SecurityUtil.encodePassword(employeeManageDTO.getPassword()))
                .roleId(employeeManageDTO.getRoleId()).build();
        userMapper.insert(user);
        //查询到创建的这个员工信息，然后联动更新插入到员工扩展表中
        User userInfo = userMapper.selectById(user.getId());
        log.info("员工创建成功，userId={}",user.getId());
        //联动更新员工扩展表
        updateStaffInfo(userInfo,employeeManageDTO);

    }
    //2.更新员工
    @Override
    public void updateEmployee(EmployeeManageDTO employeeManageDTO) {
        //1.验证该员工信息是否存在
        User user = userMapper.selectById(employeeManageDTO.getId());
        if (user == null) {
            throw new BusinessException("该员工不存在");
        }
        //2.如果修改员工姓名，检测员工姓名是否重叠
        if(! user.getUsername().equals(employeeManageDTO.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, employeeManageDTO.getUsername())
                    .ne(User::getId, user.getId());
            Long count = userMapper.selectCount(queryWrapper);
            if(count > 0) {
                throw new BusinessException("员工姓名重复");
            }
        }
        //3.更新员工信息
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getUsername,employeeManageDTO.getUsername())
                .set(User::getRoleId,employeeManageDTO.getRoleId())
                .eq(User::getId,employeeManageDTO.getId());
        if(employeeManageDTO.getPassword()!=null && ! employeeManageDTO.getPassword().trim().isEmpty()) {
            updateWrapper.set(User::getPassword,SecurityUtil.encodePassword(employeeManageDTO.getPassword()));
        }
        userMapper.update(null,updateWrapper);
        log.info("员工更新成功，userId={}",employeeManageDTO.getId());


    }
    //3.删除员工
    @Override
    public void deleteEmployee(EmployeeManageDTO employeeManageDTO) {
        log.info("删除员工");
        User user = userMapper.selectById(employeeManageDTO.getId());
        if (user == null) {
            throw new BusinessException("员工不存在");
        }
        userMapper.deleteById(user.getId());
    }
    //4.获取员工列表
    @Override
    public List<EmployeeVO> getEmployeeList() {
        //1.查询员工扩展表
        List<StaffInfo> staffInfos = staffInfoMapper.selectList(null);
        if (staffInfos == null || staffInfos.isEmpty()) {
            throw new BusinessException("员工信息为空");
        }
        //2.将这个信息装入MAP中,key是uesrId,value是所有的员工扩展表的信息
        Map<Long,StaffInfo> staffInfoMap = staffInfos.stream()
                .collect(Collectors
                        .toMap(StaffInfo::getUserId, staffInfo -> staffInfo));
        //3.获取所有员工对应的用户ID的集合
        Set<Long> staffIds = staffInfoMap.keySet();
        //2.从用户表查询员工的信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //批量取出所有对应的用户id的所有信息
        queryWrapper.in(User::getId, staffIds);
        List<User> users = userMapper.selectList(queryWrapper);
        //5.组装流水线，此时可以随意调用staffInfo的任意字段
        return users.stream()
                .map(user ->{
                    //拿到这个人的扩展信息
                    StaffInfo staffInfo = staffInfoMap.get(user.getId());
                    //提取所有需要的信息
                    Integer jobType = staffInfo.getJobType();
                    BigDecimal baseSalary = staffInfo.getBaseSalary();
                    return EmployeeVO.builder()
                            .id(user.getId())
                            .baseSalary(baseSalary)
                            .username(user.getUsername())
                            .roleId(user.getRoleId())
                            .jobType(jobType)
                            .createTime(user.getCreateTime())
                            .updateTime(user.getUpdateTime())
                            .build();
                        }).toList();

    }
    //桌位管理
    //1.新增桌位
    @Override
    public void addTable(CreatTableDTO creatTableDTO) {
        //1.验证桌名是否存在
        if (creatTableDTO == null) {
            throw new BusinessException("桌位信息不能为空");
        }
        if (creatTableDTO.getTableType()==1) {
            if(creatTableDTO.getTableName()==null) {
                throw new BusinessException("包间名称不能为空");
            }
        }
        //直接更新Table表
        Table table = new Table();
        table.setTableName(creatTableDTO.getTableName());
        table.setTableType(creatTableDTO.getTableType());
        tableMapper.insert(table);
        log.info("桌位插入成功");
    }
    //2.更新桌位
    @Override
    public void updateTable(Long id,CreatTableDTO creatTableDTO) {
        //1.验证桌位是否存在
        if (creatTableDTO == null) {
            throw new BusinessException("更新信息不能为空");
        }
        Table table = tableMapper.selectById(id);
        if (table == null) {
            throw new BusinessException("桌位信息不存在");
        }
        if (creatTableDTO.getTableType()==1) {
            if(creatTableDTO.getTableName()==null) {
                throw new BusinessException("包间名不可为空");
            }
        }
        table.setTableName(creatTableDTO.getTableName());
        table.setTableType(creatTableDTO.getTableType());
        tableMapper.updateById(table);

    }
    //3.删除桌位
    @Override
    public void deleteTable(Long id) {
        Table table = tableMapper.selectById(id);
        if (table == null) {
            throw new BusinessException("桌位信息为空");
        }
        tableMapper.deleteById(id);
    }
    //4.查看桌位
    @Override
    public List<TableListVO> getTableList() {
        //查询所有的桌子
        List<Table> tableList = tableMapper.selectList(null);
        return tableList.stream()
                .map(table -> {
                    //桌位显示名称
                    String displayName="";
                    String tableTypeText="";
                    if (table.getTableType()==0) {
                        displayName="桌号"+table.getId();
                        tableTypeText="大厅";
                    }else if (table.getTableType()==1) {
                        displayName=table.getTableName();
                        tableTypeText="包间";

                    }else {
                        displayName="未知桌名";
                        tableTypeText="未知区域";
                    }
                    return TableListVO.builder()
                            .id(table.getId())
                            .tableName(displayName)
                            .tableType(table.getTableType())
                            .tableTypeText(tableTypeText)
                            .build();
                }).toList();
    }
    //联动更新员工列表
    private void updateStaffInfo(User user,EmployeeManageDTO employeeManageDTO) {
        //1.先查询插入的这个员工信息的roleId是否为1
        User userInfo = userMapper.selectById(user.getId());
        if(userInfo.getRoleId() != 1) {
            throw new BusinessException("插入员工扩展表失败，该员工不是员工");
        }
        //2.将其信息插入员工扩展表
        StaffInfo staffInfo = StaffInfo.builder()
                .userId(user.getId())
                .jobType(employeeManageDTO.getJobType())
                .hireDate(LocalDateTime.now())
                .baseSalary(employeeManageDTO.getBaseSalary())
                .build();
        staffInfoMapper.insert(staffInfo);
        log.info("插入员工扩展表成功");
    }
}
/*你好，我刚刚完成我的餐厅管理系统的后端，技术栈是spring boot+mp+mysql+redis+rmq。现在我想匹配一个前端，并且前端还有一个用户点餐可以通知后厨的一个实时通知的MQ。因此说还需要匹配一个webstocket。我的系统分为7个端，管理员端+大堂经理端+厨师端+传菜员端+收银员端+服务员端+用户端。现在我想让你帮我写一个前端好不好？咱们就一个端口一个端口的开始写行不行，你只需要告诉我你需要什么东西？接口文档+通用返回类+我的检验拦截器是JWT，你还需要什么东西，我可以一部分一部分的给你。*/
