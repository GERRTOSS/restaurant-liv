package company.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//用户注册DTO，用来接收用户信息的DTO
@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空且不能重复")
    @Size(min =1 , max = 12,message = "用户名必须在1-12位之间")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min=6,max=10,message = "密码必须在6-10位之间")
    private String password;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    //默认注册为顾客 .只是允许顾客注册
    @Min(value = 0)@Max(value = 0)
    private Integer roleId=0;
}
