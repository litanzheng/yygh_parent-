package com.tencent.yygh.order.api;

import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.order.service.PaymentService;
import com.tencent.yygh.order.service.WeixinService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Resource
    private WeixinService weixinService;
    @Resource
    private PaymentService paymentService;

    //生成微信支付二维码
    @RequestMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId) throws Exception {
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }

    //查询微信支付状态
    @RequestMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        //调用微信接口查询支付状态
        Map<String,String> resultMap = weixinService.queryPayStatus(orderId);
        if (resultMap==null){
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))){ //支付成功
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.PaySuccess(out_trade_no,resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }
}
