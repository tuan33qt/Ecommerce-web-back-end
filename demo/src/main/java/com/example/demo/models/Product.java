package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Float price;
    private String url;
    private String description;
    private Long quantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String color;
    private String code;
    private String color2;
//    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
//    private List<Review> reviews=new ArrayList<>();
}
