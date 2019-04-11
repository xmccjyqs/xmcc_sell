package com.xmcc.springdemo.service.impl;

import com.google.common.collect.Lists;
import com.xmcc.springdemo.beans.ResultEnums;
import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.ProductCategoryDto;
import com.xmcc.springdemo.dto.ProductInfoDto;
import com.xmcc.springdemo.entity.ProductCategory;
import com.xmcc.springdemo.entity.ProductInfo;
import com.xmcc.springdemo.repository.ProductCategoryRepository;
import com.xmcc.springdemo.repository.ProductInfoRepository;
import com.xmcc.springdemo.service.ProductCategoryService;
import com.xmcc.springdemo.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {


    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private ProductCategoryService productCategoryService;


    @Override
    public ResultResponse<ProductInfo> queryList() {

        List<ProductCategoryDto> categoryDtos = productCategoryService.findAll();
        if(CollectionUtils.isEmpty(categoryDtos)){
            return ResultResponse.fail(ResultEnums.FAIL);
        }
        //类型id集合
        List<Integer> ids = categoryDtos.stream().map(categoryDto -> categoryDto.getCategoryType()).collect(Collectors.toList());

        List<ProductInfo> productInfos = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(),ids);
        if(CollectionUtils.isEmpty(productInfos)){
            return ResultResponse.fail(ResultEnums.FAIL);
        }
        List<ProductCategoryDto> collect = categoryDtos.stream().map(categoryDto -> {
            categoryDto.setProductInfoDtoList(productInfos.stream()
                    .filter(productInfo -> productInfo.getCategoryType() == categoryDto.getCategoryType())
                    .map(productInfo -> ProductInfoDto.adapt(productInfo)).collect(Collectors.toList()));
            return categoryDto;
        }).collect(Collectors.toList());

        System.out.println(collect);
        return ResultResponse.success(collect);
    }

    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {

        //判断id是否为空
        if(StringUtils.isBlank(productId)){
            return ResultResponse.fail(ResultEnums.PARAM.getMsg()+":"+productId);
        }
        //Optional 相当于一个容器，jpa 将我们的ProductInfo 封装在Optional中，提供了一些基本的验证ProductInfo对象的方法
        System.out.println("productId:--------"+productId);
        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        if(!byId.isPresent()){
            return ResultResponse.fail(ResultEnums.NOT_EXITS.getMsg()+":"+productId);
        }
        //将ProductInfo从Optional中取出
        ProductInfo productInfo = byId.get();
        //查看商品状态是否满足条件
        if(productInfo.getProductStatus() == ResultEnums.PRODUCT_DOWN.getCode()){
            return ResultResponse.fail(ResultEnums.PRODUCT_DOWN.getMsg());
        }


        return ResultResponse.success(productInfo);
    }

    @Override
    @Transactional
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepository.save(productInfo);
    }


}
