package company.restaurant.constant;
//Redis缓存KEY常量类
public class CacheConstants {
    //菜单缓存KEY基础前缀
    public static final String DISH_MENU_KEY_PREFIX = "dish:menu";
    //热销榜KEY
    public static final String HOT_DISHES_KEY = "dish:hot";

    //1.菜单相关
    //菜单缓存基础过期时间(分钟)
    public static final int DISH_CACHE_BASE_TTL = 10;
    //菜单随机时间（防止雪崩）
    public static final int DISH_CACHE_RANDOM_MAX = 5;
    //空结果缓存时间-防止穿透
    public static final int DISH_CACHE_NULL_TTL = 2;
    //2.热销榜相关
    //基础过期时间
     public static final int HOT_CACHE_BASE_TTL = 30;
    //热销榜随机时间
     public static final int HOT_CACHE_RANDOM_MAX = 10;
    //空缓存结果
    public static final int HOT_CACHE_NULL_TTL = 2;
}
