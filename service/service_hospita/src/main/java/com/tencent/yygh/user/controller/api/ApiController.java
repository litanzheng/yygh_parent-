package com.tencent.yygh.user.controller.api;

import com.atguigu.yygh.model.hosp.Hospital;
import com.tencent.yygh.common.exception.YyghException;
import com.tencent.yygh.common.helper.HttpRequestHelper;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.common.result.ResultCodeEnum;
import com.tencent.yygh.common.util.MD5;
import com.tencent.yygh.user.service.HospitalService;
import com.tencent.yygh.user.service.HospitalSetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private HospitalSetService hospitalSetService;

    @RequestMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hospSign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");

        String signKey = hospitalSetService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if (hospSign.equals(signKeyMD5)){
          throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        String logoData =(String)paramMap.get("logoData");
        logoData.replaceAll(" ","+");
        paramMap.put("logoData",logoData);
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @RequestMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String)paramMap.get("hoscode");
        String hospSign = (String)paramMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if (hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

}
