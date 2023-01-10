package com.itheima.reggie.dto;


import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author LJM
 * @create 2023/1/10
 */
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
