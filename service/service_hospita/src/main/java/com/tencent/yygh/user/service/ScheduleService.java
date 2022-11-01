package com.tencent.yygh.user.service;

import com.atguigu.yygh.model.hosp.Schedule;

import java.util.Map;

public interface ScheduleService {
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    Map<String,Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getScheduleById(String scheduleId);
}
