package company.restaurant.vo;

import lombok.*;

//注册成功给用户返回信息
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserInfoVO {
    private Long id;
    private String username;
    private Integer roleId;
    private String token;//jwt的token
    private Integer jobType;
    private String jobText;
}
