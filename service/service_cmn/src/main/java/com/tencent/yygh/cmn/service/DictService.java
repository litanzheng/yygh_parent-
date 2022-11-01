package com.tencent.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildrenData(Long id);

    void exportData(HttpServletResponse response);


    void importData(MultipartFile file);

    String getDictName(String dictCode, String value);

    List<Dict> findByDictcode(String dictCode);
}
