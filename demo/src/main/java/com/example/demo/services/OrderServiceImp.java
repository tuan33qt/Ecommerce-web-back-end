package com.example.demo.services;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.*;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    public Order createOder(OrderDTO orderDTO) throws Exception {
        User existUser=userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("can not fine user id"));
        // dùng thư viện Model Mapper
        // tạo 1 luồng bằng ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // cập nhật các trường từ orderDTO
        Order order=new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(existUser);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.DANGXULI);
        order.setActive(true);
        orderRepository.save(order);
        // Tạo danh sách các đối tượng OrderDetail từ cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalMoney = 0.0f; // Khởi tạo totalMoney cho Order
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            // Tạo một đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ cơ sở dữ liệu (hoặc sử dụng cache nếu cần)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            // Các trường khác của OrderDetail nếu cần
            orderDetail.setPrice(product.getPrice());
            orderDetail.setStatus("chưa thanh toán");
            // Tính tiền cho mỗi sản phẩm và cộng vào tổng tiền của đơn hàng
            float orderDetailTotalMoney = product.getPrice() * quantity;
            orderDetail.setTotalMoney(orderDetailTotalMoney);  // Cập nhật tổng tiền cho OrderDetail
            totalMoney += orderDetailTotalMoney;  // Cộng dồn tổng tiền


            // Thêm OrderDetail vào danh sách
            orderDetails.add(orderDetail);
        }
        // Kiểm tra phương thức vận chuyển và thêm phí vận chuyển nếu cần
        if ("express".equalsIgnoreCase(orderDTO.getPaymentShipping())) {
            totalMoney += 25000; // Thêm phí vận chuyển 25,000 nếu là "express"
        }
        // Cập nhật tổng tiền cho Order
        order.setTotalMoney(totalMoney);

        // Lưu danh sách OrderDetail vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        return order;

    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order existOrder=orderRepository.findById(id).orElseThrow(
                () ->  new DataNotFoundException("can not find order"));
        User existUser=userRepository.findById(orderDTO.getUserId()).orElseThrow(
                () ->  new DataNotFoundException("can not find user"));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO,existOrder);
        existOrder.setUser(existUser);
        return orderRepository.save(existOrder);
    }

    @Override
    public void deleteOder(Long id) {
        Order order=orderRepository.findById(id).orElse(null);
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
       return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findAllOrder() {
        return orderRepository.findAll();
    }
}
