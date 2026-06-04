package company.restaurant.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成、验证和解析 JWT token
 */
@Component
public class JwtUtil {

    // JWT 密钥（生产环境应该放在配置文件中，并且使用更复杂的密钥）
    private static final String SECRET_KEY = "restaurant_system_secret_key_2026_very_long_and_secure_key_here";

    // token 有效期：7天（单位：毫秒）
    private static final long EXPIRATION_TIME = 7L * 24 * 60 * 60 * 1000;

    // 获取密钥：采用hmac-sha进行格式封装，将密钥格式转化为签名算法需要的形式
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token
     * @param userId 用户ID
     * @param username 用户名
     * @param roleId 角色ID
     * @param jobType 员工工种，只有员工有值，别的没值，用户前端区分登录接口
     * @return JWT token
     */
    public String generateToken(Long userId, String username, Integer roleId,Integer jobType) {
        Map<String, Object> claims = new HashMap<>();//荷载payload，存放生成token中的自定义信息
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleId", roleId);
        if(jobType != null){
            claims.put("jobType", jobType);
        }
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())//用密钥签名，保证token不被篡改
                .compact();//将token生成一个字符串
    }

    /**
     * 验证 token 是否有效
     * @param token JWT token
     * @return true=有效, false=无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 token 中解析用户信息
     * @param token JWT token
     * @return Claims（包含 userId, username, roleId）
     * Claims:是JWT中荷载部分的抽象，是token中携带所有声明系的集合，本质上就是Map<String,Object>
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();//获取存入的所有东西转换为Claims
    }

    /**
     * 从 token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从 token 中获取角色ID
     */
    public Integer getRoleIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("roleId", Integer.class);
    }
}