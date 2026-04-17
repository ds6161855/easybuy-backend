package com.easybuy.dto;

public class CartDTO {

    public Long productId;
    public String name;
    public Double price;
    public String image;
    public String brand;
    public String category;
    public String color;
    public String description;
    public int quantity;
    public String userName;

    public CartDTO(Long productId, String name, Double price, String image,
                   String brand, String category, String color,
                   String description, int quantity, String userName) {

        this.productId = productId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.brand = brand;
        this.category = category;
        this.color = color;
        this.description = description;
        this.quantity = quantity;
        this.userName = userName;
    }
}