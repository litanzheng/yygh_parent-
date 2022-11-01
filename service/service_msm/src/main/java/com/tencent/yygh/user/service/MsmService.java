package com.tencent.yygh.user.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface MsmService {

    boolean sendCode(String code);

    boolean send(MsmVo msmVo);
}
