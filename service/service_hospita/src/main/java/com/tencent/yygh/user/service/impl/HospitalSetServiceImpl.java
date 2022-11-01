package com.tencent.yygh.user.service.impl;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.user.mapper.HospitalSetMapper;
import com.tencent.yygh.user.service.HospitalSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Resource
    private HospitalSetMapper hospitalSetMapper;

    @Override
    public String getSignKey(String hoscode) {
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HospitalSet::getHoscode,hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);
        return hospitalSet.getSignKey();
    }
}
