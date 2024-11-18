package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("product_id")
    private Long productId;
    @Min(value = 0,message = "price must be greater or equal to 0")
    private Float price;
    @Min(value = 0,message = "quantity must be greater or equal to 0")
    private Integer quantity;
    @JsonProperty("total_money")
    @Min(value = 0,message = "total money must be greater or equal to 0")
    private Float totalMoney;
}
