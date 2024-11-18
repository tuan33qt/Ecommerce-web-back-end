package com.example.demo.dto;

import com.example.demo.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
   @JsonProperty("product_id")
    private Long productId;
    private String url;
}
