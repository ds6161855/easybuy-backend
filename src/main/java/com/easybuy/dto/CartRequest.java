package com.easybuy.dto;

import lombok.Data;

@Data
public class CartRequest {

    private String userId;   // ✅ FIX: String (guest + user support)
    private Long productId;
    private int quantity;
    private double price;

}