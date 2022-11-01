package com.tencent.yygh.user.controller;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.HospitalSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Api(tags="医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hosptialSet")
@Slf4j
public class HospitalSetController {
    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findALL")
    public Result list(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result deletHosp(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

//    @GetMapping("findPageHospSet")  @RequestBody 需要post请求
    @PostMapping("findPageHospSet")
    public Result<Page> findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> page = new Page<>(current,limit);
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isEmpty(hospitalSetQueryVo.getHosname()),HospitalSet::getHosname,hospitalSetQueryVo.getHosname());
        queryWrapper.eq(StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()),HospitalSet::getHoscode,hospitalSetQueryVo.getHoscode());
        Page<HospitalSet> pageHosp = hospitalSetService.page(page, queryWrapper);
        return Result.ok(pageHosp);
    }

    //保存医院设置
    @PostMapping("saveHospSet")
    public Result saveHospSet(@RequestBody HospitalSet hospitalSet){
        //0表示不可用 1表示可用
        hospitalSet.setStatus(1);
        Random random = new Random();
        String s = String.valueOf(random.nextInt(1000))+" "+System.currentTimeMillis();
        String signKey = DigestUtils.md5DigestAsHex(s.getBytes());
        hospitalSet.setSignKey(signKey);
        boolean save = hospitalSetService.save(hospitalSet);
        if (save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @GetMapping("getHospSetByid/{id}")
    public Result getHospSetByid(@PathVariable long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet!=null){
            return Result.ok(hospitalSet);
        }else {
            return Result.fail();
        }
    }

    @PostMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @DeleteMapping("deleteHospSet")
    public Result batchRemoveHospSet(@RequestBody List<Long> list){
        boolean flag = hospitalSetService.removeByIds(list);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @PostMapping("lockHospSet/{id}/{status}")
    public Result lockHospSet(@PathVariable Long id,
                              @PathVariable Integer status){
        HospitalSet hosp = hospitalSetService.getById(id);
        hosp.setStatus(status);
        hospitalSetService.updateById(hosp);
        return Result.ok();
    }

    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        return Result.ok();
    }


}
