package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "fullname")
    private String fullName;
    private String email;
    @Column(name = "phone_number",nullable = false)
    private String phoneNumber;
    @Column(name = "address",nullable = false)
    private String address;
    private String note;
    @Column(name = "order_date")
    private Date orderDate;
    @Column(name = "status")
    private String status;
    @Column(name = "total_money")
    private Float totalMoney;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "active")
    private Boolean active;
}
