package company.restaurant.service;

import company.restaurant.dto.CreatTableDTO;
import company.restaurant.dto.DishManageDTO;
import company.restaurant.dto.EmployeeManageDTO;
import company.restaurant.entity.Dish;
import company.restaurant.vo.EmployeeVO;
import company.restaurant.vo.TableListVO;

import java.util.List;

//管理员接口
public interface AdminService {
    //菜品管理
    //1.添加菜品信息
    void addDish(DishManageDTO dishManageDTO);
    //2.更新菜品信息
    void updateDish(DishManageDTO dishManageDTO);
    //3.删除菜品信息
    void deleteDish(DishManageDTO dishManageDTO);
    //4.获取菜品列表
    List<Dish> getDishList();
    //员工管理
    //1.添加员工
    void addEmployee(EmployeeManageDTO employeeManageDTO);
    void updateEmployee(EmployeeManageDTO employeeManageDTO);
    void deleteEmployee(EmployeeManageDTO employeeManageDTO);
    //2.查看员工信息
    List<EmployeeVO> getEmployeeList();
    //桌位管理
    void addTable(CreatTableDTO creatTableDTO);
    void updateTable(Long id,CreatTableDTO creatTableDTO);
    void deleteTable(Long id);
    List<TableListVO> getTableList();
}
