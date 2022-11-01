package com.tencent.yygh.user.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;

import java.util.List;

public interface DepartmentService {
    List<DepartmentVo> findDeptTree(String hospCode);

    Department getDepartment(String hoscode, String depcode);
}
