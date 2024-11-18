package com.example.demo.services;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.models.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail getOrderDetail(Long id) throws Exception;
    OrderDetail updateOrderDetail(Long id,OrderDetailDTO orderDetailDTO) throws Exception;
    void deleteOrderDetail(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
