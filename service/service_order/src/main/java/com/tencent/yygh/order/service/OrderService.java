package com.tencent.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrders(String scheduleId, Long patientId);

    Boolean cancleOrder(Long orderId);
}
