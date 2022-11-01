package com.tencent.yygh.user.service.impl;

import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.common.exception.YyghException;
import com.tencent.yygh.common.helper.JwtHelper;
import com.tencent.yygh.common.result.ResultCodeEnum;
import com.tencent.yygh.user.mapper.UserInfoMapper;
import com.tencent.yygh.user.service.PatientService;
import com.tencent.yygh.user.service.UserInfoApiService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoApiServiceImpl extends ServiceImpl<UserInfoMapper,UserInfo> implements UserInfoApiService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private PatientService patientService;
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //todo 检验验证码是否一致
        if (!(code.equals(redisTemplate.opsForValue().get(phone)))){
           throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        //查询用户是否第一次登录 是则注册手机号 不是则
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone,phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (userInfo==null){
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }
        if (userInfo.getStatus()==0){
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //返回用户信息
        Map<String,Object> map = new HashMap<>();
        String name =userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name=userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name=userInfo.getPhone();
        }
        map.put("name",name);
        String token = JwtHelper.createToken(userInfo.getId(),name);
        map.put("token",token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getOpenid,openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }

    @Override
    public void userAuth(long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //todo
        baseMapper.updateById(userInfo);
    }

    @Override
    public void lock(long userId, Integer status) {
        if (status==0||status==1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(long userId) {
        Map<String,Object> map = new HashMap<>();
        UserInfo userInfo = baseMapper.selectById(userId);
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patients",patientList);
        return map;
    }
}
