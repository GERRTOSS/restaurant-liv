package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
@Builder
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;//用户id
    private String username;//用户名
    private String password;//密码
    @TableField("role_id")
    private Integer roleId;//角色码
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
