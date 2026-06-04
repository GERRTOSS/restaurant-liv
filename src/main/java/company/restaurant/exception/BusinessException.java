package company.restaurant.exception;

import lombok.Getter;

//自定义异常：业务逻辑异常统一处理
@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    public BusinessException(String message) {
        super(message);
        this.code = 400;

    }
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
