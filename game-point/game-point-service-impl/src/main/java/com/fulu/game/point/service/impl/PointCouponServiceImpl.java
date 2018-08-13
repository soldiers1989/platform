package com.fulu.game.point.service.impl;

import com.fulu.game.core.service.impl.CouponServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointCouponServiceImpl extends CouponServiceImpl {

    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;


    public void pushMsgAfterGrantCoupon(int userId, String deduction){
        pointMiniAppPushService.grantCouponMsg(userId,deduction);
    }

}