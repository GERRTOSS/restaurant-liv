package company.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import company.restaurant.entity.StaffInfo;
import company.restaurant.mapper.StaffInfoMapper;
import company.restaurant.service.UserService;
import company.restaurant.dto.UserLoginDTO;
import company.restaurant.dto.UserRegisterDTO;
import company.restaurant.entity.User;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.UserMapper;
import company.restaurant.util.JwtUtil;
import company.restaurant.util.SecurityUtil;
import company.restaurant.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//用户登录的实现类
@Service
@RequiredArgsConstructor//lombok自动生成构造方法注入，也就是不用@Auto了
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StaffInfoMapper  staffInfoMapper;
    //1.用户注册
    @Override
    public UserInfoVO register(UserRegisterDTO registerDTO) {
        //检验是否是用户注册
        if(registerDTO.getRoleId() != null && registerDTO.getRoleId() != 0){
            throw new BusinessException("只能注册普通用户");
        }
        //检验两次密码是否一致
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }
        //2.检查用户名是否已经存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if(existUser != null) {
            throw new BusinessException("用户名已经存在");
        }
        //3.创建用户实体
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(SecurityUtil.encodePassword(registerDTO.getPassword()));
        user.setRoleId(registerDTO.getRoleId() != null ? registerDTO.getRoleId() : 0);
        //4.保存到数据库
        userMapper.insert(user);
        //5.生成 JWT token
        String token = jwtUtil.generateToken(user.getId(),user.getUsername(),user.getRoleId(),null);
        //6.返回信息
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleId(user.getRoleId())
                .token(token).build();
    }
    //用户登录
    @Override
    public UserInfoVO login(UserLoginDTO loginDTO) {
        //1.根据用户名查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        //2.效验用户是否存在
        if(existUser == null) {
            throw new BusinessException("用户名不存在");
        }
        //3.验证密码的正确性
        if(! SecurityUtil.checkPassword(loginDTO.getPassword(), existUser.getPassword())) {
            throw new BusinessException("密码不正确");
        }
        //如果用户是员工，则填入jobType+jobText,以便在前端登录
        Integer jobType = null;
        String jobText="未分配";
        if (existUser.getRoleId() == 1){
            //根据userId查询员工信息
            LambdaQueryWrapper<StaffInfo> staffInfoWrapper = new LambdaQueryWrapper<>();
            staffInfoWrapper.eq(StaffInfo::getUserId,existUser.getId());
            StaffInfo staffInfo = staffInfoMapper.selectOne(staffInfoWrapper);
            if(staffInfo != null) {
                jobType = staffInfo.getJobType();
                //根据查询到的jobType进行转换
                jobText = switch (jobType) {
                    case 1 -> "厨师";
                    case 2 -> "服务员";
                    case 3 -> "大堂经理";
                    case 4 -> "清洁员";
                    case 5 -> "收银员";
                    default -> "外星角色";
                };
            }
        }
        //4.生成JWT token
        String token = jwtUtil.generateToken(existUser.getId(),existUser.getUsername(),existUser.getRoleId(),jobType);

        //5.返回值
        return UserInfoVO.builder()
                .id(existUser.getId())
                .username(existUser.getUsername())
                .roleId(existUser.getRoleId())
                .token(token)
                .jobType(jobType)
                .jobText(jobText)
                .build();
    }

}
