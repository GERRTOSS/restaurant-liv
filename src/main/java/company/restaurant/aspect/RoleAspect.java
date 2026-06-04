package company.restaurant.aspect;



import company.restaurant.annotation.RequireRole;
import company.restaurant.context.UserContext;
import company.restaurant.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限切面
 * 用于校验用户角色是否有权限访问接口
 */
@Slf4j
@Aspect
@Component
public class RoleAspect {
    /**
     * 在方法执行前校验角色权限
     */
    @Before("@annotation(company.restaurant.annotation.RequireRole) || @within(company.restaurant.annotation.RequireRole)")
    public void checkRole(JoinPoint joinPoint) {
        log.info("【AOP测试】成功拦截到了方法：{}", joinPoint.getSignature().getName());
        // 1. 获取当前用户的角色ID
        Integer currentRoleId = UserContext.getCurrentRoleId();

        if (currentRoleId == null) {
            log.warn("用户未登录，无法校验角色权限");
            throw new BusinessException(401, "未登录，请先登录");
        }
        // 2. 获取方法上的 @RequireRole 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);

        if (requireRole == null) {
            // 如果方法上没有注解，检查类上是否有注解
            requireRole = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        }

        if (requireRole == null) {
            // 没有注解，直接放行
            return;
        }

        // 3. 获取允许的角色列表
        int[] allowedRoles = requireRole.roles();

        // 4. 校验当前用户角色是否在允许列表中
        boolean hasPermission = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == currentRoleId);

        if (!hasPermission) {
            String username = UserContext.getCurrentUsername();
            log.warn("用户 [{}] (roleId={}) 无权访问接口: {}.{}()",
                    username, currentRoleId,
                    joinPoint.getTarget().getClass().getSimpleName(),
                    method.getName());

            // 根据角色返回友好的错误提示
            String errorMessage = getErrorMessage(currentRoleId, allowedRoles);
            throw new BusinessException(403, errorMessage);
        }

        log.debug("用户 [{}] (roleId={}) 通过角色权限校验",
                UserContext.getCurrentUsername(), currentRoleId);
    }

    /**
     * 生成友好的错误提示信息
     */
    private String getErrorMessage(Integer currentRoleId, int[] allowedRoles) {
        String currentRoleName = getRoleName(currentRoleId);

        // 拼接允许的角色名称
        String allowedRoleNames = Arrays.stream(allowedRoles)
                .mapToObj(this::getRoleName)
                .reduce((a, b) -> a + "、" + b)
                .orElse("未知角色");

        return String.format("权限不足：当前角色为 [%s]，该接口仅允许 [%s] 访问",
                currentRoleName, allowedRoleNames);
    }

    /**
     * 根据角色ID获取角色名称
     */
    private String getRoleName(Integer roleId) {
        if (roleId == null) {
            return "未知";
        }
        return switch (roleId) {
            case 0 -> "顾客";
            case 1 -> "员工";
            case 2 -> "管理员";
            default -> "未知角色";
        };
    }
}
