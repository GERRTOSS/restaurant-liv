package company.restaurant.service.impl;

import company.restaurant.dto.CheckoutDTO;
import company.restaurant.entity.Coupon;
import company.restaurant.entity.Order;
import company.restaurant.exception.BusinessException;
import company.restaurant.mapper.CashierMapper;
import company.restaurant.mapper.CouponMapper;
import company.restaurant.mapper.OrderMapper;
import company.restaurant.service.CashierService;
import company.restaurant.service.OrderService;
import company.restaurant.vo.ChcekoutVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

//支付接口实现类
@Service
@Slf4j
@RequiredArgsConstructor
public class CashierServiceImpl implements CashierService {
    private final OrderMapper orderMapper;
    private final CashierMapper cashierMapper;
    private final CouponMapper couponMapper;

    /**
     * 查看账单（预览，不修改数据）
     * 业务逻辑：
     * 1. 验证订单存在且未结算
     * 2. 如果传了优惠券ID，预览折扣后金额
     * 3. 返回账单详情
     */
    @Override
    public ChcekoutVO getOrderBill(Long orderId) {
        //获取此订单
        Order order = orderMapper.selectById(orderId);
        //1.验证订单是否存在未结算
        if (order == null) {
            throw new BusinessException("您的订单不存在");
        }
        if (order.getPayStatus() == 1) {
            throw new BusinessException("您的订单已经结算完毕了");
        }
        if (order.getOrderStatus() != 3) {
            throw new BusinessException("此订单不在全部完成的状态，无法支付");
        }
        //2.返回预览账单，原价展示
        return buildCheckoutVO(order,null);
    }
    //查看优惠券
    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        return couponMapper.selectAvailableCouponsByUserId(userId);
    }

    /**
     * 执行结账
     * 业务逻辑：
     * 1. 验证订单状态
     * 2. 如果传了优惠券，验证优惠券是否属于该用户且未使用
     * 3. 调用存储过程完成核销
     * 4. 查询更新后的订单，构建返回结果
     * 5. 判断是否满100，触发自动发券
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChcekoutVO checkout(CheckoutDTO checkoutDTO) {
        log.info("收银员结账，orderId={}", checkoutDTO.getOrderId());
        Long orderId = checkoutDTO.getOrderId();
        Long couponId = checkoutDTO.getCouponId();
        //1.验证订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            throw new BusinessException("该订单已结算，请勿重复操作");
        }
        if (order.getOrderStatus() != 3) {
            throw new BusinessException("该订单不在可以支付的状态中");
        }
        //2.如果使用优惠卷，先验证优惠卷
        Coupon coupon = null;
        if (couponId != null) {
            coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                throw new BusinessException("优惠卷不存在");
            }
            if (!coupon.getUserId().equals(order.getUserId())) {
                throw new BusinessException("该优惠券不属于该用户");
            }
            if (coupon.getIsUsed() != null) {
                throw new BusinessException("该优惠劵已经使用过了，无法再次使用");
            }
        }
        //3.调用存储过程完成核销
        cashierMapper.callCheckoutProcedure(orderId, couponId);
        log.info("储存过程执行完成，orderId={}", orderId);
        //4.重新查询最新的订单数据
        Order updateOrder = orderMapper.selectById(orderId);
        return buildCheckoutVO(updateOrder,coupon);

    }

    //构建结账VO
    private ChcekoutVO buildCheckoutVO(Order order, Coupon coupon) {
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal discountRate = BigDecimal.ONE;
        BigDecimal actualAmount = order.getActualAmount() != null
                ? order.getActualAmount() : totalPrice;
        String couponStatus = "未使用优惠劵";
        if (coupon != null) {
            discountRate = coupon.getDiscountRate();
            couponStatus = "已使用优惠劵（" + discountRate.multiply(new BigDecimal("100")) + "%";
        }
        BigDecimal discountAmount = totalPrice.subtract(actualAmount);
        return ChcekoutVO.builder()
                .orderId(order.getId())
                .totalPrice(totalPrice)
                .discountRate(discountRate)
                .discountAmount(discountAmount.compareTo(BigDecimal.ZERO) < 0
                        ? BigDecimal.ZERO : discountAmount)
                .actualAmount(actualAmount)
                .couponStatus(couponStatus)
                .payStatusText(order.getPayStatus() == 1 ? "已支付" : "未支付")
                .build();
    }
}

