package com.example.demo.repositories;

import com.example.demo.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // tìm 1 đơn hàng của user
    List<Order> findByUserId(Long userId);
}
