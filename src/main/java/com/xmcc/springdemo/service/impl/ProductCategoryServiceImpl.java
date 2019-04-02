package com.xmcc.springdemo.service.impl;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.ProductCategoryDto;
import com.xmcc.springdemo.dto.ProductInfoDto;
import com.xmcc.springdemo.entity.ProductCategory;
import com.xmcc.springdemo.repository.ProductCategoryRepository;
import com.xmcc.springdemo.service.ProductCategoryService;
import com.xmcc.springdemo.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategoryDto> findAll() {
        //获得所有类型
        List<ProductCategory> all = productCategoryRepository.findAll();
        //封装后得到流
        Stream<ProductCategoryDto> productCategoryDtoStream = all.stream().map(productCategory -> ProductCategoryDto.adapt(productCategory));
        //把流转换成list
        List<ProductCategoryDto> collect = productCategoryDtoStream.collect(Collectors.toList());
        return collect;
    }


}
