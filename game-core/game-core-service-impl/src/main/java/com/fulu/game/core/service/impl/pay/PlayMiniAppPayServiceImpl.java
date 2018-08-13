package com.fulu.game.core.service.impl.pay;

import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.order.PlayMiniAppOrderServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PlayMiniAppPayServiceImpl extends MiniAppPayServiceImpl {

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;

    @Qualifier(value = "playMiniAppOrderServiceImpl")
    @Autowired
    private PlayMiniAppOrderServiceImpl playMiniAppOrderService;

    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        playMiniAppOrderService.payOrder(orderNo, actualMoney);
    }

    @Override
    public WxPayMpOrderResult pay(Order order, User user, String requestIp) {
        WxPayUnifiedOrderRequest orderRequest = buildWxPayRequest(order, requestIp);
        try {
            orderRequest.setOpenid(user.getOpenId());
            return wxMaServiceSupply.playWxPayService().createOrder(orderRequest);
        } catch (Exception e) {
            log.error("陪玩订单支付错误", e);
            throw new OrderException(orderRequest.getOutTradeNo(), "陪玩订单无法支付!");
        }
    }

    @Override
    public WxPayOrderNotifyResult parseResult(String xmlResult) throws WxPayException {
        return wxMaServiceSupply.playWxPayService().parseOrderNotifyResult(xmlResult);
    }

    @Override
    public WxPayRefundResult refund(WxPayRefundRequest wxPayRefundRequest) throws WxPayException {
        return wxMaServiceSupply.playWxPayService().refund(wxPayRefundRequest);
    }

}
