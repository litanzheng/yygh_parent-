package com.tencent.yygh.user.controller;

import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.MsmService;
import com.tencent.yygh.user.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/msm")
@Api
public class MsmApiController {

    @Resource
    private MsmService msmService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("发送验证码")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        code = RandomUtil.getSixBitRandom();
        boolean flag = msmService.sendCode(code);
        if (flag){
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            System.out.println(code);
            return Result.ok();
        }else {
            return Result.fail().message("验证码发送失败");
        }
    }
}
