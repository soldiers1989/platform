package com.fulu.game.play.aop;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.queue.UserScoreQueue;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: 切面--用户积分
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 15:47
 */
@Aspect
@Component
public class UserScoreAspect {
    @Autowired
    private UserService userService;
    @Autowired
    private UserScoreQueue userScoreQueue;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 用户积分--切入点
     */
    @Pointcut("execution(* com.fulu.game.play.controller.*.*(..))")
    public void annotationPointCut() {
    }

    @AfterReturning(value = "annotationPointCut() && @annotation(score)", argNames = "joinPoint, score")
    public void processScore(JoinPoint joinPoint, UserScore score) {
        UserScoreEnum userScoreEnum = score.type();
        UserScoreDetails details = new UserScoreDetails();
        if(userScoreEnum.getDescription().equals(Constant.USER_LOGIN)) {
            User user = userService.getCurrentUser();
            details.setUserId(user.getId());
            details.setScore(userScoreEnum.getScore());
            details.setDescription(userScoreEnum.getDescription());
        }else if(userScoreEnum.getDescription().equals(Constant.IM_REPLY)) {
            Object[] array = joinPoint.getArgs();
            String acceptImId = (String)array[1];
            String imId = (String)array[2];
            String result = redisOpenService.get(imId + "+" + acceptImId);
            if(StringUtils.isNotBlank(result) && result.equals(Constant.IM_DELAY_CALCULATED)) {
                return;
            }
            if(StringUtils.isBlank(result)) {
                redisOpenService.set(imId + "+" + acceptImId,
                        DateUtil.formatDateTime(new Date()), Constant.ONE_DAY);
            }

            String value = redisOpenService.get(acceptImId + "+" + imId);
            long minutes;
            if(StringUtils.isNotBlank(value) && !value.equals(Constant.IM_DELAY_CALCULATED)) {
                minutes = DateUtil.between(DateUtil.parseDateTime(value), new Date(), DateUnit.MINUTE);
                redisOpenService.set(acceptImId + "+" + imId, Constant.IM_DELAY_CALCULATED);
                User user = userService.findByImId(acceptImId);
                details.setUserId(user.getId());
                modifyUserScoreByImReply(details, minutes);
            }
        }else if(userScoreEnum.getDescription().equals(Constant.ACCEPT_ORDER)) {
            Object[] array = joinPoint.getArgs();
            String orderNo = (String)array[0];
            Order order = orderService.findByOrderNo(orderNo);
            details.setUserId(order.getServiceUserId());
            long minutes = DateUtil.between(order.getCreateTime(), new Date(), DateUnit.MINUTE);
            modifyUserScoreByAcceptOrder(details, minutes, orderNo);
        }else if(userScoreEnum.getDescription().equals(Constant.USER_COMMENT)) {
            Object[] array = joinPoint.getArgs();
            UserCommentVO vo = (UserCommentVO)array[0];
            Order order = orderService.findByOrderNo(vo.getOrderNo());
            details.setUserId(order.getServiceUserId());
            Integer commentScore = vo.getScore();
            modifyUserScoreByUserComment(details, commentScore, order.getOrderNo());
        }else if(userScoreEnum.getDescription().equals(Constant.FULL_RESTITUTION)) {
            details.setScore(UserScoreEnum.FULL_RESTITUTION.getScore());
            details.setDescription(Constant.FULL_RESTITUTION);
        }else if(userScoreEnum.getDescription().equals(Constant.CONFER)) {
            details.setScore(UserScoreEnum.CONFER.getScore());
            details.setDescription(Constant.CONFER);
        }

        if(details.getUserId() != null) {
            userScoreQueue.addUserScoreQueue(details);
        }
    }

    /**
     * 根据用户im回复的延迟分钟数，修改用户积分
     * @param details
     * @param minutes
     */
    public void modifyUserScoreByImReply(UserScoreDetails details, long minutes) {
        if(minutes >= 0 && minutes <= 5) {
            details.setScore(UserScoreEnum.IM_REPLY_DELAY_0_TO_5.getScore());
            details.setDescription(Constant.IM_REPLY_DELAY_0_TO_5);
        }else if(minutes > 5 && minutes <= 60) {
//            details.setScore(UserScoreEnum.IM_REPLY_DELAY_5_TO_60.getScore());
//            details.setDescription(Constant.IM_REPLY_DELAY_5_TO_60);
        }else if(minutes > 60 && minutes <= 120) {
            details.setScore(UserScoreEnum.IM_REPLY_DELAY_60_TO_120.getScore());
            details.setDescription(Constant.IM_REPLY_DELAY_60_TO_120);
        }else if(minutes > 120 && minutes <= 360) {
            details.setScore(UserScoreEnum.IM_REPLY_DELAY_120_TO_360.getScore());
            details.setDescription(Constant.IM_REPLY_DELAY_120_TO_360);
        }else if(minutes > 360 && minutes <= 1440) {
            details.setScore(UserScoreEnum.IM_REPLY_DELAY_360_TO_1440.getScore());
            details.setDescription(Constant.IM_REPLY_DELAY_360_TO_1440);
        }else if(minutes > 1440) {
            details.setScore(UserScoreEnum.IM_REPLY_DELAY_LONGGER_1440.getScore());
            details.setDescription(Constant.IM_REPLY_DELAY_LONGGER_1440);
        }
    }

    /**
     * 根据用户给的星级评价，修改用户（陪玩师）积分
     * @param details
     * @param commentScore
     */
    public void modifyUserScoreByUserComment(UserScoreDetails details, Integer commentScore, String orderNo) {
        if(commentScore.equals(1)) {
            details.setScore(UserScoreEnum.USER_COMMENT_1_STAR.getScore());
            details.setDescription(Constant.USER_COMMENT_1_STAR + "，orderNo: " + orderNo);
        }else if(commentScore.equals(2)) {
            details.setScore(UserScoreEnum.USER_COMMENT_2_STAR.getScore());
            details.setDescription(Constant.USER_COMMENT_2_STAR + "，orderNo: " + orderNo);
        }else if(commentScore.equals(3)) {
//            details.setScore(UserScoreEnum.USER_COMMENT_3_STAR.getScore());
//            details.setDescription(UserScoreEnum.USER_COMMENT_3_STAR.getDescription());
        }else if(commentScore.equals(4)) {
            details.setScore(UserScoreEnum.USER_COMMENT_4_STAR.getScore());
            details.setDescription(Constant.USER_COMMENT_4_STAR + "，orderNo: " + orderNo);
        }
        else if(commentScore.equals(5)) {
            details.setScore(UserScoreEnum.USER_COMMENT_5_STAR.getScore());
            details.setDescription(Constant.USER_COMMENT_5_STAR + "，orderNo: " + orderNo);
        }
    }

    /**
     * 根据接单延迟时间，修改用户积分
     * @param details
     * @param minutes
     * @param orderNo
     */
    public void modifyUserScoreByAcceptOrder(UserScoreDetails details, long minutes, String orderNo) {
        if(minutes > 30L && minutes <= 60L) {
            details.setScore(UserScoreEnum.ACCEPT_ORDER_DELAY_30_TO_60.getScore());
            details.setDescription(Constant.ACCEPT_ORDER_DELAY_30_TO_60 + "，orderNo: " + orderNo);
        }else if(minutes > 60L && minutes <= 360L) {
            details.setScore(UserScoreEnum.ACCEPT_ORDER_DELAY_60_TO_360.getScore());
            details.setDescription(Constant.ACCEPT_ORDER_DELAY_60_TO_360 + "，orderNo: " + orderNo);
        }else if(minutes > 360L && minutes <= 1440L) {
            details.setScore(UserScoreEnum.ACCEPT_ORDER_DELAY_360_TO_1440.getScore());
            details.setDescription(Constant.ACCEPT_ORDER_DELAY_360_TO_1440 + "，orderNo: " + orderNo);
        }else if(minutes > 1440L) {
            details.setScore(UserScoreEnum.ACCEPT_ORDER_DELAY_LONGGER_1440.getScore());
            details.setDescription(Constant.ACCEPT_ORDER_DELAY_LONGGER_1440 + "，orderNo: " + orderNo);
        }
    }
}