package com.tencent.yygh.user.controller.api;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.HospitalService;
import com.tencent.yygh.user.service.ScheduleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/hosp/hosptial")
public class HospApiController {

    @Resource
    private HospitalService hospitalService;
    @Resource
    private ScheduleService scheduleService;

    @ApiOperation(value = "查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitalPage);
    }

    @GetMapping("findByHostName/{hosname}")
    public Result findByHostName(@PathVariable String hosname){
        List<Hospital> list=hospitalService.findByHostName(hosname);
        return Result.ok(list);
    }

    @ApiOperation(value = "获取可预约的排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingScheduleRule(@PathVariable Integer page,
                                         @PathVariable Integer limit,
                                         @PathVariable String hoscode,
                                         @PathVariable String depcode){
        return Result.ok(scheduleService.getBookingScheduleRule(page,limit,hoscode,depcode));
    }

    @ApiOperation(value = "根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable String scheduleId){
        Schedule schedule =scheduleService.getScheduleById(scheduleId);
        return Result.ok(schedule);
    }




}

