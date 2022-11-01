package com.tencent.yygh.cmn.controller;

import com.atguigu.yygh.model.cmn.Dict;
import com.tencent.yygh.cmn.service.DictService;
import com.tencent.yygh.common.result.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @GetMapping("findChildrenData/{id}")
    public Result findChildrenData(@PathVariable Long id){
         List<Dict> list = dictService.findChildrenData(id);
         return Result.ok(list);
    }

    //导出数据
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }

    //导入数据
    @PostMapping("importData")
    public Result importData(MultipartFile file){
        dictService.importData(file);
        return Result.ok();
    }

    //根据dictcode和value值进行查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    //根据dictcode和value值进行查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName("",value);
        return dictName;
    }

    //根据dictCode获取下级节点
    @GetMapping("findByDictcode/{dictCode}")
    public Result findByDictcode(@PathVariable String dictCode){
        List<Dict> list =dictService.findByDictcode(dictCode);
        return Result.ok(list);
    }
}
