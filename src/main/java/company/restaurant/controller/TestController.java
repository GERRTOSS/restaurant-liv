package company.restaurant.controller;

import company.restaurant.dto.UserDTO;
import company.restaurant.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "测试接口", description = "用于验收基础设施配置")
@RestController
public class TestController {
    @Operation(summary = "用户注册测试")
    @PostMapping("/register")
    // 注意：必须加 @Valid 注解，校验才会生效！
    public Result<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        // 如果逻辑走到这里，说明校验通过了
        return Result.success(userDTO);
    }
}
