package company.restaurant.util;

import company.restaurant.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

// 全局异常处理 (@RestControllerAdvice 已经包含了 @ResponseBody，不用再挨个写了)
@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    // 500错误码: 服务器内部未捕获的异常 (兜底异常)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 实际开发中，这里通常会用 log.error("系统异常", e) 记录一下日志
        e.printStackTrace();
        return Result.error(500, "系统繁忙，请稍后再试");
    }

    // 404错误码: 访问的URL链接资源不存在
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return Result.error(404, "您访问的接口地址不存在");
    }
    //处理自定义异常业务逻辑异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务逻辑异常{}",e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    // 400错误码: Validation 参数校验错误 (这才是咱们昨天配的主角！)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        // 从异常中提取出具体的校验失败信息 (比如: "年龄不能小于18岁")
        // 1. 获取“错误报告单”中的第一条具体错误
        FieldError fieldError = e.getBindingResult().getFieldError();
        /*2. 安全地提取错误信息 (三元运算符)如果不是null,则取fielderror 如果是null，则为请求参数效验失败
        * e.getBindingResult()：拿到本次请求所有的校验结果（哪怕有 10 个参数错了，都在这里面）。
        * .getFieldError()：因为咱们昨天在 properties 里配置了 hibernate.validator.fail_fast=true（快速失败，错一个就立马返回），所以这里拿第一个具体的字段错误就行了。
        * .getDefaultMessage()：这就是最精髓的一步！它提取出来的，就是你昨天在 messages.properties 里配置的中文，或者是你直接写在注解 message 属性里的那句大白话。*/
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "请求参数校验失败";
        return Result.error(400, errorMsg);
    }

    // 405错误码: 请求的方式错误 (比如该用 POST 却用了 GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.error(405, "请求方式不支持: " + e.getMethod());
    }
}
