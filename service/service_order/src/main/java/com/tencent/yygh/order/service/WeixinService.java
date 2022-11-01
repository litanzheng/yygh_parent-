package com.tencent.yygh.order.service;

import com.tencent.yygh.common.result.Result;

import java.util.Map;

public interface WeixinService {
    Map createNative(Long orderId) throws Exception;

    Map<String, String> queryPayStatus(Long orderId);


    //退款方法

    boolean refund(Long orderId);
}
