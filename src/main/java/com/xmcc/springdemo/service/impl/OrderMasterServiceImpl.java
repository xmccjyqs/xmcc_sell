package com.xmcc.springdemo.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.springdemo.beans.*;
import com.xmcc.springdemo.dto.OrderDetailDto;
import com.xmcc.springdemo.dto.OrderMasterDto;
import com.xmcc.springdemo.entity.OrderDetail;
import com.xmcc.springdemo.entity.OrderMaster;
import com.xmcc.springdemo.entity.ProductInfo;
import com.xmcc.springdemo.exception.CustomException;
import com.xmcc.springdemo.repository.OrderMasterRepository;
import com.xmcc.springdemo.service.OrderDetailService;
import com.xmcc.springdemo.service.OrderMasterService;
import com.xmcc.springdemo.service.ProductInfoService;
import com.xmcc.springdemo.util.BigDecimalUtil;
import com.xmcc.springdemo.util.IDUtils;
import com.xmcc.springdemo.util.JsonUtil;
import io.swagger.annotations.ApiParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import sun.net.www.MimeTable;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    //生成订单 插入数据库
    @Override
    @Transactional
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {

        /**
         * 1.根据购物车(订单项) 传来的商品id 查询对应的商品 取得价格等相关信息 如果没查到 订单生成失败
         * 2.比较库存 ，库存不足 订单生成失败
         * 3.生成订单项OrderDetail信息
         * 4.减少商品库存
         * 5.算出总价格 ，组装订单信息 插入数据库得到订单号
         * 6.批量插入订单项
         *
         * 注意:1.生成订单就会减少库存 加入购物车不会  所有的网站基本都是这么设计的
         *      2.商品价格以生成订单时候为准，后面商品价格改变不影响已经生成的订单
         */

        //取出订单项
        //这个地方我们在Controller层进行验证
//        @NotEmpty(message = "订单项不能为空") @Valid List<OrderDetailDto> items = orderMasterDto.getItems();
        List<OrderDetailDto> items = orderMasterDto.getItems();
        //创建OrderDetail集合 封装订单项数据
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        //创建订单总金额为0  涉及到钱的都用 高精度计算 java math下给我们提供的方法
        BigDecimal totalPrice = new BigDecimal("0");

        //遍历订单项，获取商品详情
        for (OrderDetailDto item : items) {
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());
            //说明该商品未查询到 生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
            if (resultResponse.getCode() == ResultEnums.FAIL.getCode()) {
                throw new CustomException(resultResponse.getMsg());
            }

            //获得查询的商品
            ProductInfo productInfo = resultResponse.getData();

            //比较库存 库存不足 订单生成失败 直接抛出异常 事务才会回滚
            if(productInfo.getProductStock()<item.getProductQuantity()){
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //创建订单项
            OrderDetail orderDetail = OrderDetail.builder().detailId(IDUtils.createIdbyUUID()).productIcon(productInfo.getProductIcon())
                    .productId(item.getProductId()).productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice()).productQuantity(item.getProductQuantity())
                    .build();
            //加入订单项集合
            orderDetailList.add(orderDetail);
            //减少商品库存
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            //更新商品库存
            productInfoService.updateProduct(productInfo);
            //计算价格
            totalPrice = BigDecimalUtil.add(totalPrice,BigDecimalUtil.multi(productInfo.getProductPrice(),item.getProductQuantity()));
        }

        //生成订单id
        String orderId = IDUtils.createIdbyUUID();
        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder().buyerAddress(orderMasterDto.getAddress()).buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid()).orderStatus(OrderEnum.NEW.getCode())
                .payStatus(PayEnum.WAIT.getCode()).buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId).orderAmount(totalPrice).build();
        //将生成的订单id，设置到订单项中
        List<OrderDetail> detailList = orderDetailList.stream().map(orderDetail -> {
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());

        //插入订单项
        orderDetailService.batchInsert(detailList);
        //插入订单
        orderMasterRepository.save(orderMaster);

        HashMap<String, String> successMap = Maps.newHashMap();
        //按照前台要求的数据结构传入
        successMap.put("orderId",orderId);
        return ResultResponse.success(successMap);
    }
}
