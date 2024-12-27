package com.example.demo.services;

import com.example.demo.dto.OrderDTO;
import com.example.demo.models.Order;

import java.util.List;

public interface OrderService {
    Order createOder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(Long id, OrderDTO orderDTO) throws Exception;
    void deleteOder(Long id);
    List<Order> findByUserId(Long userId);
    List<Order> findAllOrder();
}
