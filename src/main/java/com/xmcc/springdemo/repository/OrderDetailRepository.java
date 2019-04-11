package com.xmcc.springdemo.repository;

import com.xmcc.springdemo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

}
