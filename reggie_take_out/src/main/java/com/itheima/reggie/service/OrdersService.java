package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);

    //通过订单id查询订单明细，得到一个订单明细的集合
    List<OrderDetail> getOrderDetailListByOrderId(Long orderId);
}
