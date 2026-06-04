package company.restaurant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("t_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    private Long id;
    private String tableName;//包间名字
    private Integer tableType;//属性码：0大厅 1包间
}
