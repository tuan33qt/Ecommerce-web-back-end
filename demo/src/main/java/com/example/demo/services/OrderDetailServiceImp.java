package com.example.demo.services;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Order;
import com.example.demo.models.OrderDetail;
import com.example.demo.models.Product;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImp implements OrderDetailService {
    private  final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception{
        Order order=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("can not find order"));
        Product product=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("can not find product"));
        OrderDetail orderDetail=OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(orderDetailDTO.getQuantity())
                .price(orderDetailDTO.getPrice())
                .build();
        orderDetail.setTotalMoney(orderDetail.getPrice()*orderDetail.getQuantity());
        return  orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("can not find order detail")
        );
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception {
        OrderDetail existOrderDetail=orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(" can not find orderDetail"));
        Order existOrder=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(" can not find order"));
        Product existProduct=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(" can not find product"));
        existOrderDetail.setPrice(orderDetailDTO.getPrice());
        existOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existOrderDetail.setOrder(existOrder);
        existOrderDetail.setProduct(existProduct);
        return orderDetailRepository.save(existOrderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
