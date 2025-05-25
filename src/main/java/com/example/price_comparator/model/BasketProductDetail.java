package com.example.price_comparator.model;

import lombok.Data;

@Data
public class BasketProductDetail {
    private String productId;
    private String productName;
    private int quantity;
    private double pricePerUnit;
    private double totalPrice;
}
