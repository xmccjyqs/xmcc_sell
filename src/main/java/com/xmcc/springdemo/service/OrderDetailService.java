package com.xmcc.springdemo.service;

import com.xmcc.springdemo.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    //批量插入
    void batchInsert(List<OrderDetail> orderDetailList);
}
