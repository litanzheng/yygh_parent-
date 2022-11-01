package com.tencent.yygh.user.service.impl;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.user.mapper.PatientMapper;
import com.tencent.yygh.user.service.PatientService;

import java.util.List;

public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient>implements PatientService {
    @Override
    public List<Patient> findAllUserId(long userId) {
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getUserId,userId);
        List<Patient> patients = baseMapper.selectList(queryWrapper);
        return patients;
    }
}
