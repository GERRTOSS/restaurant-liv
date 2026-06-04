package company.restaurant.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//呼叫记录返回值
@Data
@Builder
public class CallLogVO {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private Long handlerId;
    private String statusText;//待处理、已处理
}
