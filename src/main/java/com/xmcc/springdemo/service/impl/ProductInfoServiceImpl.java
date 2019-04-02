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
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {


    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private ProductCategoryService productCategoryService;


    @Override
    public List<ProductCategoryDto> queryList() {

        List<ProductCategoryDto> categoryDtos = productCategoryService.findAll();
        if(CollectionUtils.isEmpty(categoryDtos)){
            return Lists.newArrayList();
        }
        //类型id集合
        List<Integer> ids = categoryDtos.stream().map(categoryDto -> categoryDto.getCategoryType()).collect(Collectors.toList());

        List<ProductInfo> productInfos = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(),ids);

//        categoryDtos.stream().map(categoryDto -> categoryDto.setProductInfoDtoList(
//                productInfos.stream()
//                        .filter(productInfo -> productInfo.getCategoryType() == categoryDto.getCategoryType())
//                        .map(productInfo -> ProductInfoDto.adapt(productInfo))
//                        .collect(Collectors.toList());
//
//        ));

        List<ProductCategoryDto> collect = categoryDtos.stream().map(categoryDto -> {
            categoryDto.setProductInfoDtoList(productInfos.stream()
                    .filter(productInfo -> productInfo.getCategoryType() == categoryDto.getCategoryType())
                    .map(productInfo -> ProductInfoDto.adapt(productInfo)).collect(Collectors.toList()));
            return categoryDto;
        }).collect(Collectors.toList());


        return collect;
    }
}
