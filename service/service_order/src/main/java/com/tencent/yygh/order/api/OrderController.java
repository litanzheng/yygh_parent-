package com.tencent.yygh.order.api;

import com.tencent.yygh.common.result.Result;
import com.tencent.yygh.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/order/orderInfo")
public class OrderController {

    @Resource
    private OrderService orderService;

    //生成挂号订单
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrders(@PathVariable String scheduleId,
                              @PathVariable Long patientId){
        Long orderId = orderService.saveOrders(scheduleId,patientId);
        return Result.ok(orderId);

    }

    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        Boolean isOrder = orderService.cancleOrder(orderId);
        return Result.ok(isOrder);
    }

}
