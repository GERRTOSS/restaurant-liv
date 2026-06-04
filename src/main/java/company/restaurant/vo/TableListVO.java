package company.restaurant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//桌号返回值VO ：LIST形式返回
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableListVO {
    //桌号id
    private Long id;
    //包间名称
    private String tableName;
    //识别码 0大厅 1包间
    private Integer tableType;
    //桌位类型文本
    private String tableTypeText;
}
