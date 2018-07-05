package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PilotOrderDetails;
import com.fulu.game.core.entity.vo.PilotOrderDetailsVO;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 领航订单流水表
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-05 14:31:31
 */
@Mapper
public interface PilotOrderDetailsDao extends ICommonDao<PilotOrderDetails,Integer>{

    List<PilotOrderDetails> findByParameter(PilotOrderDetailsVO pilotOrderDetailsVO);

    /**
     * 找到最近一条表记录
     * @return
     */
    BigDecimal findLastRecordSum();

}
