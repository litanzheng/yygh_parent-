package com.tencent.yygh.order.service.impl;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.order.mapper.RefundInfoMapper;
import com.tencent.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",paymentInfo.getOrderId());
        queryWrapper.eq("payment_type",paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);
        if (refundInfo!=null){  //有相同的数据
            return refundInfo;
        }
        //添加记录
        refundInfo = new RefundInfo();
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
