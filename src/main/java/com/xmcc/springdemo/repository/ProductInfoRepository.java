package com.xmcc.springdemo.repository;

import com.xmcc.springdemo.entity.ProductInfo;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInfoRepository extends JpaRepository<ProductInfo,String> {

    //根据类目编号 查询正常上架的商品
    List<ProductInfo> findByProductStatusAndCategoryTypeIn(Integer status, List<Integer> categoryList);

}
