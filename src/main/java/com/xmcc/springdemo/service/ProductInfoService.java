package com.xmcc.springdemo.service;

import com.xmcc.springdemo.dto.ProductCategoryDto;
import com.xmcc.springdemo.dto.ProductInfoDto;

import java.util.List;

public interface ProductInfoService {

    List<ProductCategoryDto> queryList();
}
