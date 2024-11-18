package com.example.demo.response;

import com.example.demo.models.Order;
import com.example.demo.models.OrderDetail;
import com.example.demo.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    @JsonProperty( "order_id")
    private Long orderId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("price")
    private Float price;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("total_money")
    private Float totalMoney;
    public  static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
       return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .totalMoney(orderDetail.getTotalMoney())
                .build();

    }
}
