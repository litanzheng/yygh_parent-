package com.tencent.yygh.oss.controller;

import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.oss.service.FileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Resource
    private FileService fileService;

    @RequestMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        String url = fileService.upload(file);
        return Result.ok();
    }
}
