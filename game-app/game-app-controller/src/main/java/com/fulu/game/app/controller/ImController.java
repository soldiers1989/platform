package com.fulu.game.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/im")
public class ImController extends BaseController {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    
    @Autowired
    private UserService userService;

    
    //增加陪玩师未读消息数量
    @RequestMapping("/send")
    public Result sendMessage(@RequestParam(value = "targetImId", required = false) String targetImId) {

        //获取目标用户信息
        User u = userService.findByImId(targetImId);

        UserVO targetUser = new UserVO();
        
        BeanUtil.copyProperties(u,targetUser);

        //判断im目标用户是否为代聊用户
        if (targetUser.getImSubstituteId() != null) {

            //判断目标用户是否在线
            String onlineStatus = redisOpenService.get(RedisKeyEnum.USER_ONLINE_KEY.generateKey(targetUser.getId()));

            if (StringUtils.isNotBlank(onlineStatus)) {
                //删除目标用户的未读信息
                redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()));

            } else {
                //增加未读消息数量+1
                Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()));
                
                
                if(map == null || map.size() == 0){
                    map = new HashMap();
                    targetUser.setUnreadCount(1);
                }else{
                    if(map.get(targetImId)!=null){
                        UserVO temp = (UserVO)map.get(targetImId);
                        targetUser.setUnreadCount(temp.getUnreadCount() + 1);
                    }
                }
                map.put(targetImId, targetUser);
                //更新未读消息数
                redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()), map, Constant.ONE_DAY * 3);
            }

        }

        return Result.success().msg("操作成功");

    }

}