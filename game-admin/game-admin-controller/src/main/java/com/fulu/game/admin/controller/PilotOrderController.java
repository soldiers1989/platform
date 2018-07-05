package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.PilotOrderDetailsService;
import com.fulu.game.core.service.PilotOrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 领航订单Controller
 * @Author: Yan Biao/Gong Zechun
 * @Date: 2018/7/5 10:59
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/pilotorder")
public class PilotOrderController extends BaseController {

    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private PilotOrderDetailsService pilotOrderDetailsService;

    /**
     * 领航订单列表
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @param orderSearchVO
     * @return
     */
    @RequestMapping("/list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       String orderBy,
                       OrderSearchVO orderSearchVO) {
        PageInfo pageInfo = pilotOrderService.findVoList(pageNum,pageSize,orderBy,orderSearchVO);
        return Result.success().data(pageInfo).msg("查询列表成功！");
    }

    /**
     * 订单获利金额
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping("/amount")
    public Result amountOfProfit(Date startTime,
                                 Date endTime){
        BigDecimal amount = pilotOrderService.amountOfProfit(startTime,endTime);
        return Result.success().data(amount);
    }

    /**
     * 修改备注（管理员备注）
     * @param orderId
     * @param adminRemark
     * @return
     */
    @PostMapping("/adminRemark")
    public Result alterAdminRemark(@RequestParam Integer orderId, @RequestParam String adminRemark) {
        if(StringUtils.isBlank(adminRemark)) {
            return Result.error().msg("备注不能为空");
        }
        boolean flag = pilotOrderService.alterAdminRemark(orderId, adminRemark);
        if(!flag) {
            return Result.error().msg("修改备注失败");
        }
        return Result.success().msg("修改备注成功");
    }

    /**
     * 对领航订单进行打款
     * @param money
     * @param remark
     * @return
     */
    @PostMapping("/remit")
    public Result remit(@RequestParam BigDecimal money, @RequestParam String remark) {
        boolean flag = pilotOrderDetailsService.remit(money, remark);
        if(!flag) {
            return Result.error().msg("打款失败");
        }
        return Result.success().msg("打款成功");
    }
}
