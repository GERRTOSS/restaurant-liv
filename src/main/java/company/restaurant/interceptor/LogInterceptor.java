//拦截器包
package company.restaurant.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/*日志拦截器：记录每一个到达系统的http请求，
* 记录内容：请求URL，请求方式，客户端IP，接口执行耗时，
* 加速排查bug，只需看日志就知道哪个接口出问题了。
* 写完拦截器之后需要将其注册为配置类，因为只是组件的话spring boot没法直接扫描到*/
//用这个lombok之后就可以不用写private ... logger= ...
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {
    //在请求进入controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler){
        long startTime = System.currentTimeMillis();
        //获取开始时间
        request.setAttribute("startTime", startTime);
        log.info("--->[请求开始]URL: {} , Method:{} , IP:{}",
                request.getRequestURL().toString(),
                request.getMethod(),
                request.getRemoteAddr());
        //流程继续
        return true;

    }
    //在整个请求结束后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        Object startTimeObj = request.getAttribute("startTime");
        // 如果 startTime 为空，说明 preHandle 没执行，直接结束
        if (startTimeObj == null) {
            return;
        }
        long startTime = (long) startTimeObj;
        long endTime = System.currentTimeMillis();
        log.info("--->[请求结束]URL: {}, 耗时:{}ms, 状态码:{} ",
                request.getRequestURL().toString(),
                (endTime-startTime),
                response.getStatus());
    }
}
