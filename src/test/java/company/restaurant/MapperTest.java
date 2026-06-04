package company.restaurant;

import company.restaurant.entity.User;
import company.restaurant.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    private UserMapper um;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testMapperAll(){
        List<User> users=userMapper.selectList(null);
        users.forEach(System.out::println);
    }
    @Test
    public void testInsert(){
        User user=new User();
        user.setUsername("gerrard");
        user.setPassword("123456");
        user.setRoleId(0);
        System.out.println("插入数据结果：");
        System.out.println(userMapper.insert(user));
    }

}
