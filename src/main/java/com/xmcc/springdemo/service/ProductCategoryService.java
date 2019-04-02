package com.xmcc.springdemo.service;

import com.xmcc.springdemo.beans.ResultResponse;
import com.xmcc.springdemo.dto.ProductCategoryDto;

import java.util.List;

public interface ProductCategoryService {

    //查询所有分类
    List<ProductCategoryDto> findAll();

}
