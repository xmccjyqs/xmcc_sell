package com.xmcc.springdemo.controller;

import com.xmcc.springdemo.entity.ProductCategory;
import com.xmcc.springdemo.repository.ProductCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class TestController {

//    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @GetMapping("/hello2")
    public String hello(){
//        logger.error("error");
//        logger.info("info");
//        logger.debug("debug");
        log.info("msg {}","hello spring boot");
        return "hello spring boot 2  第一次修改项目";
    }

    @GetMapping("/test1")
    public String test1(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryId(1);
        productCategory.setCategoryName("手机1");
        productCategory.setCategoryType(1);
        productCategory.setCreateTime(new Date());
        productCategoryRepository.save(productCategory);
        return "success";
    }
}
