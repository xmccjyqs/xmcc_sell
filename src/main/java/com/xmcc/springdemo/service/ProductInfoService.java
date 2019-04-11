package com.xmcc.springdemo.service;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.ProductCategoryDto;
import com.xmcc.springdemo.dto.ProductInfoDto;
import com.xmcc.springdemo.entity.ProductInfo;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public interface ProductInfoService {

    //查询商品类型
    ResultResponse queryList();

    //根据商品id查询商品
    ResultResponse<ProductInfo> queryById(String productId);

    //修改商品库存
    void updateProduct(ProductInfo productInfo);

    //
    ResultResponse<Integer> incrStockById(Integer productQuantity, String productId);

}
