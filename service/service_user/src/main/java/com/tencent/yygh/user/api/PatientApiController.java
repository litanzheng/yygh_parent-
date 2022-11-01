package com.tencent.yygh.user.api;

import com.atguigu.yygh.model.user.Patient;
import com.tencent.yygh.cmn.client.DictFeignClient;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.common.utils.AuthContextHolder;
import com.tencent.yygh.user.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Resource
    private PatientService patientService;

    @Resource
    DictFeignClient dictFeignClient;

    //获取就诊人列表
    @GetMapping("auth/findAll")
    public Result findAllPatient(HttpServletRequest request){
        long userId = AuthContextHolder.getUserId(request);
        List<Patient> patientList =patientService.findAllUserId(userId);
//        patientList.stream().forEach(item->{
//            this.packPatient(item);
//        });
        return Result.ok(patientList);
    }

//    private void packPatient(Patient item) {
//        dictFeignClient.getName()
//    }

    //添加就诊人
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient,HttpServletRequest request){
        long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //修改就诊人
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    //删除就诊人
    @PostMapping("auth/remove/{id}")
    public Result deletePatient(@PathVariable long id){
        patientService.removeById(id);
        return Result.ok();
    }

    //根据就诊人id获取信息
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable Long id){
        Patient patient = patientService.getById(id);
        return patient;
    }
}
