package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_call_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallLog {
    @TableId
    private Long id;
    private Long userId;//关联用户id
    private String content;//请求内容
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private Long handlerId;//关联员工id

}
