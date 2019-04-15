package com.xmcc.springdemo.controller;

import com.lly835.bestpay.model.PayResponse;
import com.xmcc.springdemo.entity.OrderMaster;
import com.xmcc.springdemo.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("pay")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @RequestMapping("create")
    /**
     * 根据API文档创建接口
     * orderId: 订单ID 这里只能传递一个ID 防止别人传入非法的金额
     * returnUrl: 回调地址
     */
    public ModelAndView create(@RequestParam("orderId")String orderId,
                               @RequestParam("returnUrl")String returnUrl, Map map){
        //根据id查询订单
        OrderMaster orderMaster = payService.findOrderById(orderId);

        //根据订单创建支付
        PayResponse response = payService.create(orderMaster);
        log.info("appId -> {}", response.getAppId());
        //将参数设置到map中
        map.put("payResponse", response);
        map.put( "returnUrl",returnUrl);
        return new ModelAndView("weixin/pay", map);

    }
    @RequestMapping("test")
    public void test(){
        log.info("测试异步回调OK");
    }

    @RequestMapping("notify")
    public ModelAndView weixin_notify(@RequestBody String notifyData){

        log.info("微信支付，异步回调 notifyData -> {}",notifyData);
        //调用业务层来处理回调验证
        payService.weixin_notify(notifyData);
        //通知微信，我们这边OK
        return new ModelAndView("weixin/success");
    }
}
