package com.xmcc.springdemo.service;

import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;
import com.xmcc.springdemo.entity.OrderMaster;
import org.springframework.stereotype.Service;

/**
 * 支付业务
 */
@Service
public interface PayService {
    //根据订单id查询订单
    OrderMaster findOrderById(String orderId);

    //根据订单创建支付
    PayResponse create(OrderMaster orderMaster);

    //创建回调方法
    void weixin_notify(String notifyData);

    //微信退款
    RefundResponse refund(OrderMaster orderMaster);
}
