package com.tencent.yygh.user.controller;

import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.user.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
@CrossOrigin
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @GetMapping("getDeptList/{hospCode}")
    public Result getDeptList(@PathVariable String hospCode){
        List<DepartmentVo> list=departmentService.findDeptTree(hospCode);
        return Result.ok(list);
    }

}
