package company.restaurant.service;

import company.restaurant.dto.CheckoutDTO;
import company.restaurant.entity.Coupon;
import company.restaurant.vo.ChcekoutVO;

import java.util.List;

//支付的接口层
public interface CashierService {
    //1.查询订单账单
    ChcekoutVO getOrderBill(Long orderId);
    //2.查看优惠券
    List<Coupon> getUserCoupons(Long userId);
    //3.结算方法
    ChcekoutVO checkout(CheckoutDTO checkoutDTO);
}
