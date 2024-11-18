package com.example.demo.services;

import com.example.demo.dto.OderDTO;
import com.example.demo.models.Order;

import java.util.List;

public interface OrderService {
    Order createOder(OderDTO oderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(Long id,OderDTO oderDTO) throws Exception;
    void deleteOder(Long id);
    List<Order> findByUserId(Long userId);
}
