package com.example.demo.response;

import com.example.demo.models.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private List<Product> products;
    private int totalPages;

    public ProductResponse(List<Product> products, int totalPages) {
        this.products = products;
        this.totalPages = totalPages;
    }

}
