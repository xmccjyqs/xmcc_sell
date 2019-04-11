package com.xmcc.springdemo.repository;

import com.xmcc.springdemo.entity.OrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
}
