package com.easybuy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(name = "product_id", nullable = false)
    public Long productId;

    @Column(nullable = false)
    public int quantity;

    public Cart() {}
}