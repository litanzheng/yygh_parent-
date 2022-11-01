package com.tencent.yygh.task.scheduled;

import com.tencent.common.rabbit.constant.MqConst;
import com.tencent.common.rabbit.service.RabbitService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@EnableScheduling
public class ScheduledTask {

    @Resource
    private RabbitService rabbitService;

    @Scheduled(cron = "")
    public void taskPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");

    }
}
