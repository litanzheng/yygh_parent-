package com.tencent.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.yygh.common.helper.HttpRequestHelper;
import com.tencent.yygh.order.mapper.PaymentMapper;
import com.tencent.yygh.order.service.OrderService;
import com.tencent.yygh.order.service.PaymentService;
import com.tencent.yygh.user.client.HospitalFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {


    @Resource
    private OrderService orderService;

    @Resource
    private HospitalFeignClient hospitalFeignClient;
    @Override
    public void savePaymentInfo(OrderInfo order, Integer status) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",order.getId());
        queryWrapper.eq("payment_type",status);
        Integer integer = baseMapper.selectCount(queryWrapper);
        if (integer>0){
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(status);
        baseMapper.insert(paymentInfo);
    }

    @Override
    public void PaySuccess(String out_trade_no, Map<String, String> resultMap) {

        //1.根据订单编号得到支付记录
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",out_trade_no);
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);
        //2.更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        baseMapper.updateById(paymentInfo);
        //3.根据订单号得到订单支付信息
        //4.更新订单信息
        Long orderId = paymentInfo.getOrderId();
        OrderInfo orderInfo = orderService.getById(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);
        //5.调用医院接口,更新订单信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String,Object> map= new HashMap<>();
        //设置map参数 ...

        JSONObject jsonObject = HttpRequestHelper.sendRequest(map, signInfoVo.getApiUrl() + "");

    }

    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);
        return paymentInfo;
    }
}
