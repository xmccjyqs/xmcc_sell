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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
@Slf4j
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

    @Override
    public ResultResponse queryList(String openid, Integer page, Integer size) {
        if(StringUtils.isBlank(openid)){
            return ResultResponse.fail(OrderEnum.OPENID_ERROR.getMsg());
        }

        PageRequest pageRequest = PageRequest.of(page == null || page-1 < 0 ? 0 : page-1, size == null || size < 3 ? 3 : size);
        Page<OrderMaster> byBuyerOpenid = orderMasterRepository.findByBuyerOpenid(openid, pageRequest);
        return ResultResponse.success(byBuyerOpenid.getContent());
    }

    @Override
    public ResultResponse<OrderMaster> findByOrderIdAndOpenId(String orderId, String openid) {
        if(StringUtils.isBlank(openid)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg()+":"+openid);
        }
        if(StringUtils.isBlank(orderId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg()+":"+orderId);
        }
        //有不有都返回 让调用者根据情况来处理
        OrderMaster orderMaster = orderMasterRepository.findByOrderIdAndBuyerOpenid(orderId, openid);
        return ResultResponse.success(orderMaster);
    }

    /**
     * @param openid ：微信唯一标识号
     * @param orderId :订单id
     * @return
     * 1.判断订单状态 如果是已完成 就不能取消了
     * 2.修改订单状态
     * 3.查询订单关联的订单项，然后获得商品
     * 4.增加商品库存
     * 5.如果已经付款则退款
     */
    @Override
    @Transactional
    public ResultResponse cancelOrder(String openid, String orderId) {
        // 根据上面的方法查询订单
        ResultResponse<OrderMaster> byOrderIdAndOpenId = findByOrderIdAndOpenId(orderId, openid);
        //尽量少抛出异常 因为异常栈的读取是很浪费效率的  所以在没有事务出现之前可以用返回失败来确定
        //但是一旦有了增删改的代码之后 必须抛出异常 来控制事务回滚
        if(byOrderIdAndOpenId.getCode()==ResultEnums.FAIL.getCode()){
            return byOrderIdAndOpenId;
        }
        OrderMaster orderMaster = byOrderIdAndOpenId.getData();
        if(orderMaster==null){
            return ResultResponse.fail(OrderEnum.ORDER_NOT_EXITS.getMsg());
        }
        if(orderMaster.getOrderStatus()==OrderEnum.FINSH.getCode()||orderMaster.getOrderStatus()==OrderEnum.CANCEL.getCode()){
            return ResultResponse.fail(OrderEnum.FINSH_CANCEL.getMsg());
        }
        //2.修改订单状态
        updateStatus(orderMaster,OrderEnum.CANCEL.getCode());
        //3.查询订单关联的订单项，然后获得商品  这里需要把之前偷懒的地方提出来 作为一个方法
        //因为业务层一般不会调用其他模块的dao层
        ResultResponse<List<OrderDetail>> orderDetailListByOrderId = orderDetailService.findOrderDetailListByOrderId(orderId);
        List<OrderDetail> orderDetailList = orderDetailListByOrderId.getData();
        if(CollectionUtils.isEmpty(orderDetailList)){//直接结束了
            return ResultResponse.success();
        }
        //这里用批量修改操作，需要挨个去查询商品
        for (OrderDetail orderDetail : orderDetailList) {
            ResultResponse<Integer> result = productInfoService.incrStockById(orderDetail.getProductQuantity(),orderDetail.getProductId());
            //不在上面的方法抛出异常 因为有的业务没有修改成功 也是可以的
            if(result.getData() < 1){
                //商品下架 也可以修改  只要失败就回滚 不然会丢失商品
                log.error("商品库存增加失败,商品id为:{},商品名称为:{}",orderDetail.getProductId(),orderDetail.getProductName());
                throw new CustomException("商品库存增加失败");//抛出异常事务回滚
            }
        }
        if(orderMaster.getPayStatus()==PayEnum.FINISH.getCode()){
            //TODO :退款操作 之后再完成
        }

        return ResultResponse.success();
    }

    @Override
    @Transactional
    public ResultResponse updateStatus(OrderMaster orderMaster, int status) {
        if(orderMaster==null){
            throw new CustomException(OrderEnum.ORDER_NOT_EXITS.getMsg());
        }
        orderMaster.setOrderStatus(status);
        orderMasterRepository.save(orderMaster);
        return ResultResponse.success();
    }
}
