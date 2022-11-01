package com.tencent.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.cmn.listener.DictLisener;
import com.tencent.yygh.cmn.mapper.DictMapper;
import com.tencent.yygh.cmn.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private DictMapper dictMapper;

    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildrenData(Long id) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isEmpty(id),Dict::getParentId,id);
        List<Dict> list = dictMapper.selectList(queryWrapper);
        for (Dict dict : list) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
//            System.out.println(this);
            dict.setHasChildren(isChild);
        }
        return list;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        //1.设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName="dict";
        response.setHeader("Content-disposition","attachment:filename="+fileName+".xlsx");

        //2.数据库进行查询
        List<Dict> list = dictMapper.selectList(null);
//        List<DictEeVo> dictEeVoList = list.stream().map((item) -> {
//            DictEeVo dictEeVo = new DictEeVo();
//            BeanUtils.copyProperties(item, dictEeVo);
//            return dictEeVo;
//        }).collect(Collectors.toList());
        List<DictEeVo> dictEeVoList = new ArrayList<>();
        for (Dict dict : list) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        }

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVoList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),Dict.class, new DictLisener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        if (StringUtils.isEmpty(dictCode)){
            LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dict::getValue,value);
            Dict dict = dictMapper.selectOne(queryWrapper);
            return dict.getName();
        }else {
            Dict dictByDiceCode = this.getDictByDiceCode(dictCode);
            Long id = dictByDiceCode.getId();
            LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dict::getId,id)
                        .eq(Dict::getValue,value);
            Dict dict = dictMapper.selectOne(queryWrapper);
            return dict.getName();
        }

    }

    @Override
    public List<Dict> findByDictcode(String dictCode) {
        Dict dictByDiceCode = this.getDictByDiceCode(dictCode);
        Long id = dictByDiceCode.getId();
        List<Dict> childrenData = this.findChildrenData(id);
        return childrenData;
    }

    public Dict getDictByDiceCode(String dictCode){
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictCode,dictCode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        return dict;
    }


    public boolean isChildren(Long id){
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isEmpty(id),Dict::getParentId,id);
        Integer integer = dictMapper.selectCount(queryWrapper);
        return integer>0;
    }
}
