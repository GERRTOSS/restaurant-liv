package company.restaurant.service;

import company.restaurant.dto.UserLoginDTO;
import company.restaurant.dto.UserRegisterDTO;
import company.restaurant.vo.UserInfoVO;

//用户登录逻辑层接口
public interface UserService {
    //1.用户注册接口
    UserInfoVO register(UserRegisterDTO registerDTO);
    //2.用户登录接口
    UserInfoVO login(UserLoginDTO loginDTO);
}
