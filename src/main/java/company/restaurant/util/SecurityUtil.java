package company.restaurant.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


//BCrypt加密技术
public class SecurityUtil {
    //新增一个私有属性的无参构造方法，防止别的地方使用new来创建无所谓的对象
    private SecurityUtil(){
        throw new IllegalStateException("Utility class");
    }
    //实例化官方提供的加密器
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    /*
    * 加密：将明文转换为密文
    * 参数：传入的原始密码
    * 返回值：经过加密之后的密文*/
    public static String encodePassword(String password) {
        return ENCODER.encode(password);
    }
    /*
    * 效验密码：经过对比用户输入的密码是否和数据库中的密文一致。
    * 参数rawPassword:用户登录时输入的明文，
    * 参数encodePassword:数据库中存储的密文，
    * 返回值：匹配则为true，不匹配则为false*/
    public static boolean checkPassword(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
