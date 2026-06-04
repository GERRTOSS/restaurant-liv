package company.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    // 这里的 message 内容要对应你 messages.properties 里的 key
    // 或者直接写中文也可以测试：message = "用户名不能为空"
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 10, message = "用户名长度必须在2-10个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
