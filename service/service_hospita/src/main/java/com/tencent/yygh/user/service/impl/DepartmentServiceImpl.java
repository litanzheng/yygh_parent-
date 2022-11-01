package com.tencent.yygh.user.service.impl;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.tencent.yygh.user.repository.DepartmentRepository;
import com.tencent.yygh.user.service.DepartmentService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;
    @Override
    public List<DepartmentVo> findDeptTree(String hospCode) {
        List<DepartmentVo> result = new ArrayList<>();

        Department departmentQuery = new Department();
        Example example = Example.of(departmentQuery);
        //所有科室列表
        List<Department> departmentList = departmentRepository.findAll(example);
        //根据大科室编号,获取所有下面小科室
        Map<String, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            //大科室下面小科室
            List<Department> departmentList1 = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigCode);
            departmentVo1.setDepname(departmentList1.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children= new ArrayList<>();
            for (Department department : departmentList1) {
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                children.add(departmentVo2);
            }
            departmentVo1.setChildren(children);
            result.add(departmentVo1);
        }
        return result;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return null;
    }
}
