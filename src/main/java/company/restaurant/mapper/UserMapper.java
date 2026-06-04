package company.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import company.restaurant.entity.User;
import org.apache.ibatis.annotations.Mapper;

//用户表Mapper接口
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper后，基础的CRUD方法都自动拥有了：
    // - insert(User user)
    // - deleteById(Long id)
    // - updateById(User user)
    // - selectById(Long id)
    // - selectList(Wrapper<User> wrapper)
    // 等等...

    // 如果需要自定义SQL，可以在这里声明方法，然后去XML里写SQL
    // 例如：User findByPhone(String phone);
}
