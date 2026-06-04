package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
//任务明细表
@TableName("t_order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long dishId;
    private Long chefId;//关联厨师id
    private Long waiterId;//服务员id
    private Integer itemStatus;//任务明细状态
    private Integer isPriority;//是否加急
    @TableField(fill = FieldFill.INSERT)//自动更新
    private LocalDateTime createTime;//创建时间
    private LocalDateTime acceptTime;//接单时间
    private LocalDateTime finishTime;//完成时间
    private LocalDateTime deliverTime;//送达时间
    @TableField(fill = FieldFill.INSERT_UPDATE)//自动更新
    private LocalDateTime updateTime;//最后的更新时间
}
