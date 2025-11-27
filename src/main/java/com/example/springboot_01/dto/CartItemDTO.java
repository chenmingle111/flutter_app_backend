package com.example.springboot_01.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id; // Product ID
    private String name;
    private Double price;
    private String image;
    private Integer item; // Quantity
    private Double totalPrice;
}
