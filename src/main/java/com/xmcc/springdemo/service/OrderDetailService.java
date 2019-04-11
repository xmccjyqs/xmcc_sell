package com.xmcc.springdemo.service;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    //批量插入
    void batchInsert(List<OrderDetail> orderDetailList);

    //
    ResultResponse queryByOrderIdWithOrderMaster(String openid, String orderId);

    //通过订单id 查询 订单项详情列表
    ResultResponse< List<OrderDetail>> findOrderDetailListByOrderId(String orderId);
}
