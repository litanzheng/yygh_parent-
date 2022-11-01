package com.tencent.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoApiService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);

    void userAuth(long userId, UserAuthVo userAuthVo);

    void lock(long userId, Integer status);

    Map<String, Object> show(long userId);
}
