package com.example.demo.controller;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.models.OrderDetail;
import com.example.demo.response.OrderDetailResponse;
import com.example.demo.services.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order_details")
@RequiredArgsConstructor
public class OderDetailController {
    private final OrderDetailService orderDetailService;
    @PostMapping
    public ResponseEntity<?> createOderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail newOrderDetail= orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public  ResponseEntity<?> getOderDetail(@Valid @PathVariable("id") Long id) {
        try {
            OrderDetail orderDetail=orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOderDetails(@Valid @PathVariable("orderId") Long orderId) {
        List<OrderDetail>   orderDetails= orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses=orderDetails
                .stream()
                .map(OrderDetailResponse :: fromOrderDetail)
                .toList();
        return  ResponseEntity.ok(orderDetailResponses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOderDetail(@Valid @PathVariable("id") Long id, @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail= orderDetailService.updateOrderDetail(id,orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOderDetail(@Valid @PathVariable("id") Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok("delete successfully");
    }
}
