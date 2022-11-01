package com.tencent.yygh.user.controller;

import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


@RestController
@RequestMapping("/admin/hosp/schedule")
@CrossOrigin
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
    Map<String,Object> map = scheduleService.getRuleSchedule(page,limit,hoscode,depcode);
    return Result.ok(map);
    }

}
