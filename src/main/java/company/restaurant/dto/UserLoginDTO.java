package company.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//用户登录传入的数据
@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名错误")
    private String username;
    @NotBlank(message = "用户密码错误")
    private String password;

}
