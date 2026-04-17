package com.easybuy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.easybuy.enums.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "product_id", nullable = false)
    public Long productId;

    @Column(nullable = false)
    public int quantity;

    @Column(nullable = false)
    public double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus status;

    @Column(name = "cancel_date")
    public LocalDateTime cancelDate;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(nullable = false)
    public String location;

    // ✅ Added fields
    @Column(nullable = true)
    public String name;

    @Column(nullable = true)
    public String mobile;

    @Column(nullable = true)
    public String address;

    @Column(name = "delivery_date", nullable = true)
    public LocalDateTime deliveryDate;

    public Order() {
        this.status = OrderStatus.PLACED;
        this.createdAt = LocalDateTime.now();
        this.location = "India";
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}