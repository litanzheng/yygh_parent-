package com.tencent.yygh.user.service.impl;


import com.atguigu.yygh.vo.msm.MsmVo;
import com.tencent.yygh.user.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean sendCode(String code) {
        return true;
    }

    @Override
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())){
            return true;
        }
        return false;
    }
}
