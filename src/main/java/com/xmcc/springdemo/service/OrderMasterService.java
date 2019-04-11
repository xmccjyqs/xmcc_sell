package com.xmcc.springdemo.service;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.OrderMasterDto;

//订单业务开发
public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);

}
