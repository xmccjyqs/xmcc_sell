package com.xmcc.springdemo.config;

import com.lly835.bestpay.config.WxPayH5Config;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.xmcc.springdemo.properties.WxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class PayConfig {
    @Autowired
    private WxProperties wxProperties;

    @Bean
    public WxPayH5Config wxPayH5Config() {
        WxPayH5Config wxPayH5Config = new WxPayH5Config();
        wxPayH5Config.setAppId(wxProperties.getAppid());
        wxPayH5Config.setMchId(wxProperties.getMchId());
        wxPayH5Config.setMchKey(wxProperties.getMchKey());
        wxPayH5Config.setKeyPath(wxProperties.getKeyPath());
        wxPayH5Config.setNotifyUrl(wxProperties.getNotifyUrl());
        return wxPayH5Config;
    }

    @Bean
    public BestPayServiceImpl bestPayService(WxPayH5Config wxPayH5Config) {
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayH5Config(wxPayH5Config);
        return bestPayService;
    }
}
