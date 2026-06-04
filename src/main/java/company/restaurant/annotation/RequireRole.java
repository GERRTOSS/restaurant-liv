package company.restaurant.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 用于标注接口需要的角色权限
 * 角色码说明：
 * 0 = 顾客
 * 1 = 员工（厨师、传菜员、服务员、收银员等）
 * 2 = 管理员
 * 使用示例：
 * &#064;RequireRole(roles  = {0, 2})  // 只有顾客和管理员可以访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})  // 可以用在方法或类上
@Retention(RetentionPolicy.RUNTIME)//运行时候有效
public @interface RequireRole {
    /**
     * 允许访问的角色码数组
     */
    int[] roles();
    /**
     * 权限描述（可选，用于日志记录）
     */
    String description() default "";
}

