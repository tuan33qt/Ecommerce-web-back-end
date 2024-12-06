package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.models.Order;
import com.example.demo.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO, BindingResult result) {
        try {
            Order order= orderService.createOder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("user/{user_id}")
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId) {
        List<Order> orders=orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existOrder= orderService.getOrder(orderId);
        return ResponseEntity.ok(existOrder);
    }
    @PutMapping("/{id}") //admin
    public  ResponseEntity<?> updateOder(@Valid @PathVariable long id,
                                         @Valid @RequestBody OrderDTO orderDTO) {
        try {
            Order order=orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOder(@Valid @PathVariable Long id){
        orderService.deleteOder(id);
        return ResponseEntity.ok("order delete successfully");
    }
}
