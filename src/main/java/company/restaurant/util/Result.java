package company.restaurant.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 通用返回类
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    // 快捷方法：操作成功+带数据的操作成功。
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 快捷方法：操作失败 (可以自定义错误码和错误信息)
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
    //无参构造：有些是不需要填充信息的返回类
    public static <T> Result<T> success() {
        return new Result<>(200,"操作成功",null);

    }
    public static <T> Result<T> success(String message) {
        return new Result<>(200,message,null);
    }
}
