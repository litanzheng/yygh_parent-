package com.tencent.yygh.user.api;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.common.utils.AuthContextHolder;
import com.tencent.yygh.user.service.UserInfoApiService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class UserInfoApiController {

    @Resource
    private UserInfoApiService userInfoApiService;

    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String,Object> info =userInfoApiService.loginUser(loginVo);
        return Result.ok(info);
    }

    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        userInfoApiService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoApiService.getById(userId);
        return Result.ok(userInfo);
    }

}
