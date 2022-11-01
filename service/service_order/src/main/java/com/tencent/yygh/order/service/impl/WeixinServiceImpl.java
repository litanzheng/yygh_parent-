package com.tencent.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.order.service.OrderService;
import com.tencent.yygh.order.service.PaymentService;
import com.tencent.yygh.order.service.RefundInfoService;
import com.tencent.yygh.order.service.WeixinService;
import com.tencent.yygh.order.utils.ConstantPropertiesUtils;
import com.tencent.yygh.order.utils.HttpClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Resource
    OrderService orderService;
    @Resource
    PaymentService paymentService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    RefundInfoService refundInfoService;

    @Override
    public Map createNative(Long orderId) throws Exception {

        Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
        if (payMap != null) {
            return payMap;
        }
        //1.获取订单信息
        OrderInfo order = orderService.getById(orderId);
        //2.向支付记录表添加信息
        paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
        //3.设置参数 调用微信接口
        Map parmaMap = new HashMap();
        //4.调用微信生成二维码httpClient进行调用
        HttpClient client = new HttpClient("");
        //client设置参数
        client.setXmlParam(WXPayUtil.generateSignedXml(parmaMap, ConstantPropertiesUtils.PARTNERKEY));
        client.setHttps(true);
        client.post();
        //5.微信返回相关的数据
        String xml = client.getContent();
        //转换成map集合
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
        //6.封装返回的结果集
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("totalFee", order.getAmount());
        map.put("result_code", resultMap.get("result_code"));
        map.put("codeUrl", resultMap.get("code_Url"));//二维码地址
        if (resultMap.get("result_code") != null) {
            redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
        }
        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        //1.根据orderId获取订单信息
        OrderInfo orderInfo = orderService.getById(orderId);

        //2.封装提交参数
        Map map = new HashMap();
        map.put("", "");
        //3.设置请求内容
        try {
            HttpClient client = new HttpClient("");
            client.setXmlParam(WXPayUtil.generateSignedXml(map, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //4.得到微信接口返回的数据
            String xml = client.getContent();
            //转换成map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //5.把接口数据返回
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //微信退款操作
    @Override
    public boolean refund(Long orderId) {
        //1.获取支付信息记录
        PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
        //2.添加信息到退款记录表
        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        //3.判断当前是否已经退款
        if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
            return true;
        }
        //4.调用微信接口实现退款
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("...", "...");
        HttpClient client = new HttpClient("");
        //client设置参数
        try {
            String paramXml = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY);
            client.setXmlParam(paramXml);
            client.setHttps(true);
            //设置证书的信息、
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();

            //接收返回的数据
            String xml = client.getContent();
            //转换成map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());
                //todo...
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
