package company.restaurant.context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户上下文 - 存储当前登录用户信息
 * 使用 ThreadLocal 保证线程安全
 */
public class UserContext {
    //私有构造方法
    private UserContext(){
        throw new IllegalStateException("Utility class");
    }
    //安全线程，保存用户的信息。
    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String username, Integer roleId, Integer jobType) {
        CONTEXT.set(new CurrentUser(userId, username, roleId,jobType));
    }

    /**
     * 获取当前用户信息
     */
    public static CurrentUser getCurrentUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        CurrentUser user = CONTEXT.get();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        CurrentUser user = CONTEXT.get();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户角色ID
     */
    public static Integer getCurrentRoleId() {
        CurrentUser user = CONTEXT.get();
        return user != null ? user.getRoleId() : null;
    }
    //获取员工jobType
    public static Integer getCurrentJobType() {
        CurrentUser user = CONTEXT.get();
        return user != null ? user.getJobType() : null;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 当前用户信息实体
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUser {
        private Long userId;
        private String username;
        private Integer roleId;
        private Integer jobType;
    }
}