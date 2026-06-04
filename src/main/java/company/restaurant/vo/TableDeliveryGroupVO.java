package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

//按照桌号分组返回的服务员传菜的VO
@Data
@Builder
public class TableDeliveryGroupVO {
    private Long tableId;
    private String tableName;
    private List<WaiterDeliveryVO> waiters;
}
