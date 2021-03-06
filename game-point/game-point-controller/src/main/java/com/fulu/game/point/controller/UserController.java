package com.fulu.game.point.controller;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.entity.vo.WxUserInfo;
import com.fulu.game.core.service.AdviceService;
import com.fulu.game.core.service.ImService;
import com.fulu.game.core.service.UserCommentService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户个人中心Controller
 *
 * @author Gong ZeChun
 * @date 2018/7/27 11:51
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    private final UserService userService;
    private final WxMaServiceSupply wxMaServiceSupply;
    private final RedisOpenServiceImpl redisOpenService;
    private final AdviceService adviceService;
    private final OssUtil ossUtil;
    private final UserCommentService userCommentService;
    private final ImService imService;

    @Autowired
    public UserController(WxMaServiceSupply wxMaServiceSupply, RedisOpenServiceImpl redisOpenService,
                          UserService userService, AdviceService adviceService, OssUtil ossUtil,
                          UserCommentService userCommentService,
                          ImService imService) {
        this.userService = userService;
        this.wxMaServiceSupply = wxMaServiceSupply;
        this.redisOpenService = redisOpenService;
        this.adviceService = adviceService;
        this.ossUtil = ossUtil;
        this.userCommentService = userCommentService;
        this.imService = imService;
    }

    /**
     * 用户-我的钱包-查询余额
     * 账户金额不能从缓存取，因为存在管理员给用户加零钱缓存并未更新
     *
     * @return 封装结果集
     */
    @PostMapping("/balance/get")
    public Result getBalance() {
        User user = userService.findById(userService.getCurrentUser().getId());
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }


    /**
     * 用户-进入我的页面
     *
     * @return
     */
    @PostMapping("/get")
    public Result get() {
        User user = userService.findById(userService.getCurrentUser().getId());
        return Result.success().data(user).msg("查询信息成功！");
    }

    /**
     * 获取用户微信手机号
     *
     * @param encryptedData
     * @param iv
     * @return
     */
    @PostMapping("/wxinfo/mobile")
    public Result getWinfoMobile(String encryptedData,
                                 String iv) {

        WxMaPhoneNumberInfo phoneNoInfo = null;
        String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(SubjectUtil.getToken()));
        try {
            phoneNoInfo = wxMaServiceSupply.playWxMaService().getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        } catch (Exception e) {
            log.error("获取用户微信异常:encryptedData:{};iv:{};sessionKey:{};{}", encryptedData, iv, sessionKey, e.getMessage());
            throw new UserException(UserException.ExceptionCode.SESSION_KEY_DISABLE_EXCEPTION);
        }
        if (phoneNoInfo == null && phoneNoInfo.getPurePhoneNumber() == null) {
            log.error("未获取用户微信手机号:phoneNoInfo:{};encryptedData:{};iv:{};sessionKey:{};", phoneNoInfo, encryptedData, iv, sessionKey);
            throw new UserException(UserException.ExceptionCode.WX_PHONE_NOT_EXIST_EXCEPTION);
        }
        User user = userService.findById(userService.getCurrentUser().getId());
        log.info("获取用户微信手机号,原用户信息:{}", user);
        user.setMobile(phoneNoInfo.getPurePhoneNumber());
        user.setUpdateTime(new Date());
        userService.update(user);
        userService.updateRedisUser(user);
        log.info("获取用户微信手机号,更新后用户信息:{};手机号:{};", user, user.getMobile());
        return Result.success().data(user);

    }

    /**
     * 用户-更新个人信息
     *
     * @param userVO
     * @return
     */
    @RequestMapping("/update")
    public Result update(UserVO userVO) {
        User user = userService.findById(userService.getCurrentUser().getId());
        user.setAge(userVO.getAge());
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        userService.updateRedisUser(user);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }


    /**
     * 保存微信信息
     *
     * @param wxUserInfo
     * @return
     */
    @PostMapping("/wxinfo/save")
    public Result saveWxUserInfo(WxUserInfo wxUserInfo) {
        User user = userService.getCurrentUser();
        WxMaUserInfo wxMaUserInfo = null;
        try {
            String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(SubjectUtil.getToken()));
            wxMaUserInfo = wxMaServiceSupply.pointWxMaService().getUserService().getUserInfo(sessionKey, wxUserInfo.getEncryptedData(), wxUserInfo.getIv());
        } catch (Exception e) {
            log.error("获取用户微信异常:wxUserInfo:{};{}", wxUserInfo, e.getMessage());
            throw new UserException(UserException.ExceptionCode.SESSION_KEY_DISABLE_EXCEPTION);
        }
        user.setUnionId(wxMaUserInfo.getUnionId());
        if (user.getGender() == null) {
            user.setGender(wxUserInfo.getGender());
        }
        if (user.getNickname() == null) {
            user.setNickname(wxUserInfo.getNickName());
        }
        if (user.getHeadPortraitsUrl() == null) {
            user.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
        }
        if (user.getCity() == null) {
            user.setCity(wxUserInfo.getCity());
        }
        if (user.getProvince() == null) {
            user.setProvince(wxUserInfo.getProvince());
        }
        if (user.getCountry() == null) {
            user.setCountry(wxUserInfo.getCountry());
        }
        user.setUpdateTime(new Date());

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        User resultUser = userService.updateUnionUser(userVO, PlatformEcoEnum.POINT, null);
        return Result.success().data(resultUser);
    }

    /**
     * 查询用户是否需要领取新用户优惠券
     *
     * @return 封装结果集
     */
    @PostMapping("/coupon/status")
    public Result userCouponStatus() {
        User user = userService.getCurrentUser();
        boolean flag = userService.getUserCouponStatus(user);
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("newUserCouponStatus", flag ? 1 : 0);
        return Result.success().data(resultMap).msg("获取状态成功！");
    }

    /**
     * 用户添加意见反馈
     *
     * @param content       建议内容
     * @param contact       联系方式
     * @param advicePicUrls 反馈图片
     * @return 封装结果集
     */
    @PostMapping("/advice/add")
    public Result addAdvice(@RequestParam("content") String content,
                            @RequestParam(value = "contact", required = false) String contact,
                            @RequestParam(value = "advicePicUrls", required = false) String[] advicePicUrls) {
        if (content == null) {
            return Result.error().msg("请填写建议内容");
        }
        Advice advice = adviceService.addAdvice(content, contact, advicePicUrls);
        return Result.success().data(advice).msg("提交成功");
    }




    @PostMapping("/im/save")
    public Result imSave(@RequestParam("status") Integer status,
                         @RequestParam("imId") String imId,
                         @RequestParam("imPsw") String imPsw,
                         @RequestParam(value = "errorMsg", required = false) String errorMsg) {
        User user = userService.findById(userService.getCurrentUser().getId());
        if (null == user) {
            log.info("当前用户id={}查询数据库不存在，无法绑定", userService.getCurrentUser().getId());
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        log.info("IM注册请求开始,请求参数status:{},user:{},imId={},imPsw={},errorMsg={}", user, status, imId, imPsw, errorMsg);
        if (user.getImId() != null) {
            log.info("用户IM信息已经存在:user:{};", user);
            return Result.success().data(user).msg("已存在IM账号");
        }
        if (status == 200) {
            user.setImId(imId);
            user.setImPsw(imPsw);
            user.setUpdateTime(new Date());
            userService.update(user);
            userService.updateRedisUser(user);
            log.info("用户:{}绑定IM信息成功:user:{};", user.getId(), user);
        } else if (status == 500) {
            String newIMId = "s" + imId;
            ImUser imUser = imService.registerUser(newIMId, imPsw);
            user.setImId(imUser.getUsername());
            user.setImPsw(imUser.getImPsw());
            user.setUpdateTime(new Date());
            userService.update(user);
            userService.updateRedisUser(user);
            log.error("用户:{}绑定IM失败,失败原因:{}", user, errorMsg);
        }
        return Result.success().data(user).msg("IM用户信息保存成功！");
    }

    /**
     * 用户-根据imId查询聊天对象User
     *
     * @return
     */
    @PostMapping("/im/get")
    public Result getImUser(@RequestParam("imId") String imId,
                            String content) {
        log.info("根据imId获取用户信息:imId:{};content:{}", imId, content);
        if (StringUtils.isEmpty(imId)) {
            throw new UserException(UserException.ExceptionCode.IllEGAL_IMID_EXCEPTION);
        }
        User user = userService.findByImId(imId);
        if (null == user) {
            return Result.error().msg("未查询到该用户或尚未注册IM");
        }
        log.info("根据imId获取用户信息:imId:{};content:{};user:{}", imId, content, user);
        return Result.success().data(user).msg("查询IM用户成功");
    }


    /**
     * 用户-添加评价
     *
     * @return
     */
    @RequestMapping("/comment/save")
    public Result save(UserCommentVO commentVO) {
        userCommentService.save(commentVO);
        return Result.success().msg("添加成功！");
    }
}
