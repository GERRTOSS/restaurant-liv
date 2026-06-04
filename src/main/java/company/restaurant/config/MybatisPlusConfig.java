package company.restaurant.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import company.restaurant.entity.Table;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

//注入器，注入MP的真实插入方法
@Configuration
public class MybatisPlusConfig {
    @Bean
    //Default:MP的核心类的默认实现子类型，叫做SQL构建器
    public DefaultSqlInjector sqlInjector() {
        return new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo){
                //获取MP原本自带的所有方法
                List<AbstractMethod> methodList = super.getMethodList(mapperClass,tableInfo);
                //把隐藏的批量插入方法加进去
                methodList.add(new InsertBatchSomeColumn());
                return methodList;

            }
        };
    }
}
