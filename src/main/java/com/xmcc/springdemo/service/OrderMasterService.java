package com.xmcc.springdemo.service;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.OrderMasterDto;
import com.xmcc.springdemo.entity.OrderMaster;

//订单业务开发
public interface OrderMasterService {

    //创建订单
    ResultResponse insertOrder(OrderMasterDto orderMasterDto);
    //查询订单列表
    ResultResponse queryList(String openid, Integer page, Integer size);
    //查看订单
    ResultResponse<OrderMaster> findByOrderIdAndOpenId(String orderId, String openid);
    //取消订单
    ResultResponse cancelOrder(String openid, String orderId);
    //修改订单状态
    ResultResponse updateStatus(OrderMaster orderMaster,int status);

}
