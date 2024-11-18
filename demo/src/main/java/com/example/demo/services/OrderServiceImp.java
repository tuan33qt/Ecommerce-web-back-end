package com.example.demo.services;

import com.example.demo.dto.OderDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Order;
import com.example.demo.models.OrderStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order createOder(OderDTO oderDTO) throws Exception {
        User existUser=userRepository.findById(oderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("can not fine user id"));
        // dùng thư viện Model Mapper
        // tạo 1 luồng bằng ánh xạ
        modelMapper.typeMap(OderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // cập nhật các trường từ orderDTO
        Order order=new Order();
        modelMapper.map(oderDTO,order);
        order.setUser(existUser);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.DANGXULI);
        order.setActive(true);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order updateOrder(Long id, OderDTO oderDTO) throws Exception {
        Order existOrder=orderRepository.findById(id).orElseThrow(
                () ->  new DataNotFoundException("can not find order"));
        User existUser=userRepository.findById(oderDTO.getUserId()).orElseThrow(
                () ->  new DataNotFoundException("can not find user"));
        modelMapper.typeMap(OderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(oderDTO,existOrder);
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
}
