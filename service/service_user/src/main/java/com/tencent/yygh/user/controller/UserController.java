package com.tencent.yygh.user.controller;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.acl.UserQueryVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.UserInfoApiService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    UserInfoApiService userInfoApiService;


    @GetMapping("{page}/limit")
    public Result list(@PathVariable long page,
                       @PathVariable long limit,
                       UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page1 = new Page<>(page,limit);
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(userInfoQueryVo.getKeyword()),UserInfo::getName,userInfoQueryVo.getKeyword());
        userInfoApiService.page(page1,queryWrapper);
        return Result.ok(page1);
    }

    //用户锁定
    @GetMapping("lock/{userId}/{status}")
    public Result lock(@PathVariable long userId,
                       @PathVariable Integer status){
        userInfoApiService.lock(userId,status);
        return Result.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public Result show(@PathVariable long userId
                       ){
        Map<String,Object> map = userInfoApiService.show(userId);
        return Result.ok();
    }

}
