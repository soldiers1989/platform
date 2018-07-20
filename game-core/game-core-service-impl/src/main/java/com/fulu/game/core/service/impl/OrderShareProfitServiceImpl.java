package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.dao.ArbitrationDetailsDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderShareProfitDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
@Slf4j
public class OrderShareProfitServiceImpl extends AbsCommonService<OrderShareProfit,Integer> implements OrderShareProfitService {

    @Autowired
	private OrderShareProfitDao orderShareProfitDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private PayService payService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Autowired
    private ArbitrationDetailsDao arbitrationDetailsDao;

    @Override
    public ICommonDao<OrderShareProfit, Integer> getDao() {
        return orderShareProfitDao;
    }

    /**
     * 订单正常完成状态分润
     * @param order
     */
    @Override
    public void shareProfit(Order order) {
        BigDecimal totalMoney = order.getTotalMoney();
        BigDecimal charges = order.getCharges();
        if(charges==null){
            Category category = categoryService.findById(order.getCategoryId());
            charges = category.getCharges();
        }
        BigDecimal commissionMoney = totalMoney.multiply(charges);
        BigDecimal serverMoney = order.getTotalMoney().subtract(commissionMoney);
        //如果是领航订单则用原始的订单金额给打手分润
        PilotOrder pilotOrder = pilotOrderService.findByOrderNo(order.getOrderNo());
        if (pilotOrder != null) {
            serverMoney = pilotOrder.getTotalMoney().subtract(commissionMoney);
            pilotOrder.setIsComplete(true);
            pilotOrderService.update(pilotOrder);
        }
        //记录分润表
        OrderShareProfit orderShareProfit = new OrderShareProfit();
        orderShareProfit.setCommissionMoney(commissionMoney);
        orderShareProfit.setServerMoney(serverMoney);
        orderShareProfit.setUserMoney(new BigDecimal(0));
        orderShareProfit.setOrderNo(order.getOrderNo());
        orderShareProfit.setCreateTime(new Date());
        orderShareProfit.setUpdateTime(new Date());
        create(orderShareProfit);
        //记录用户加零钱
        moneyDetailsService.orderSave(serverMoney, order.getServiceUserId(), order.getOrderNo());
        //平台记录支付打手流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_SHARE_PROFIT, order.getOrderNo(), serverMoney.negate());
    }



    /**
     * 订单发生退款
     * @param order
     */
    @Override
    public void orderRefund(Order order,BigDecimal refundMoney) {
        if (!order.getIsPay()) {
            throw new OrderException(order.getOrderNo(), "未支付订单不允许退款!");
        }
        if(order.getActualMoney().compareTo(refundMoney)<0){
            throw new OrderException(order.getOrderNo(), "退款金额不能大于用户实付金额!");
        }
        BigDecimal charges = order.getCharges();
        if(charges==null){
            Category category = categoryService.findById(order.getCategoryId());
            charges = category.getCharges();
        }
        BigDecimal surplusMoney = order.getActualMoney().subtract(refundMoney);
        BigDecimal commissionMoney = surplusMoney.multiply(charges);
        BigDecimal serverMoney = surplusMoney.subtract(commissionMoney);
        //记录分润表
        OrderShareProfit orderShareProfit = new OrderShareProfit();
        orderShareProfit.setCommissionMoney(commissionMoney);
        orderShareProfit.setServerMoney(serverMoney);
        orderShareProfit.setUserMoney(refundMoney);
        orderShareProfit.setOrderNo(order.getOrderNo());
        orderShareProfit.setCreateTime(new Date());
        orderShareProfit.setUpdateTime(new Date());
        create(orderShareProfit);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND,
                order.getOrderNo(), refundMoney.negate());
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND,
                order.getOrderNo(), serverMoney.negate());
        //记录用户（陪玩师）加零钱
        moneyDetailsService.orderSave(serverMoney, order.getServiceUserId(), order.getOrderNo());
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_USER_CANCEL, refundMoney.negate());
        try {
            payService.refund(order.getOrderNo(), order.getActualMoney(),refundMoney);
        } catch (Exception e) {
            log.error("退款失败{}", order.getOrderNo(), e.getMessage());
            throw new OrderException(order.getOrderNo(), "订单退款失败!");
        }
    }

    /**
     * 订单金额部分退款给用户，部分退款给陪玩师
     * @param order
     * @param details
     */
    @Override
    public void orderRefundToUserAndServiceUser(Order order, ArbitrationDetails details) {
        if (!order.getIsPay()) {
            throw new OrderException(order.getOrderNo(), "未支付订单不允许退款!");
        }

        String orderNo = order.getOrderNo();
        BigDecimal refundUserMoney = details.getRefundUserMoney();
        BigDecimal refundServiceUserMoney = details.getRefundServiceUserMoney();
        BigDecimal actualMoney = order.getActualMoney();

        if(actualMoney.compareTo(refundUserMoney.add(refundServiceUserMoney)) < 0){
            throw new OrderException(orderNo, "退款金额不能大于用户实付金额!");
        }

        BigDecimal commissionMoney = actualMoney.subtract(refundUserMoney.add(refundServiceUserMoney));

        //记录分润表
        OrderShareProfit profit = new OrderShareProfit();
        profit.setOrderNo(orderNo);
        profit.setServerMoney(refundServiceUserMoney);
        profit.setCommissionMoney(commissionMoney);
        profit.setUserMoney(refundUserMoney);
        profit.setUpdateTime(DateUtil.date());
        profit.setCreateTime(DateUtil.date());
        create(profit);

        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND,
                orderNo, refundUserMoney.negate());
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND,
                orderNo, refundServiceUserMoney.negate());

        //记录仲裁结果流水表
        ArbitrationDetails arbitrationDetails = new ArbitrationDetails();
        arbitrationDetails.setOrderNo(orderNo);
        arbitrationDetails.setUserId(details.getUserId());
        arbitrationDetails.setServiceUserId(details.getServiceUserId());
        arbitrationDetails.setRefundUserMoney(refundUserMoney);
        arbitrationDetails.setRefundServiceUserMoney(refundServiceUserMoney);
        arbitrationDetails.setCommissionMoney(commissionMoney);
        arbitrationDetails.setRemark(details.getRemark());
        arbitrationDetails.setUpdateTime(DateUtil.date());
        arbitrationDetails.setCreateTime(DateUtil.date());
        arbitrationDetailsDao.create(arbitrationDetails);

        //记录陪玩师加零钱
        moneyDetailsService.orderSave(refundServiceUserMoney, order.getServiceUserId(), orderNo);

        //微信退款给用户
        try {
//            payService.refund(orderNo, order.getActualMoney(), refundUserMoney);
        } catch (Exception e) {
            log.error("退款失败{}", orderNo, e.getMessage());
            throw new OrderException(orderNo, "订单退款失败!");
        }
    }

}
