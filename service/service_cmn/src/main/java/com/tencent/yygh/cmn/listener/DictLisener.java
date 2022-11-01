package com.tencent.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.tencent.yygh.cmn.mapper.DictMapper;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

public class DictLisener extends AnalysisEventListener<DictEeVo> {


    public DictLisener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    private DictMapper dictMapper;


    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
