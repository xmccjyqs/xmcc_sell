package com.xmcc.springdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

//    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello2")
    public String hello(){
//        logger.error("error");
//        logger.info("info");
//        logger.debug("debug");
        log.info("msg {}","hello spring boot");
        return "hello spring boot 2  第一次修改项目";
    }
}
