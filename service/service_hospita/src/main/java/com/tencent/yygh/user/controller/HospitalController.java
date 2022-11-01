package com.tencent.yygh.user.controller;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.HospitalService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/hosp/hosptial")
@CrossOrigin
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
    Page<Hospital> pageModle = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
    return Result.ok(pageModle);
    }

    //更新医院上线状态
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    //医院详情信息
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Hospital hospital=hospitalService.getHospById(id);
        return Result.ok(hospital);
    }

}
