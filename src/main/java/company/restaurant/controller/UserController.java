package company.restaurant.controller;

import company.restaurant.service.UserService;
import company.restaurant.dto.UserLoginDTO;
import company.restaurant.dto.UserRegisterDTO;
import company.restaurant.util.Result;
import company.restaurant.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//用户登录和注册接口
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户登录注册接口", description = "用户登录+注册接口")
public class UserController {
    private final UserService userService;
    //用户注册
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserInfoVO userInfoVO = userService.register(registerDTO);
        return Result.success(userInfoVO);
    }
    //用户登录
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfoVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserInfoVO userInfoVO = userService.login(userLoginDTO);
        return Result.success(userInfoVO);
    }
}
