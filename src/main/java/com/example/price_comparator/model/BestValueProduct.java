package com.example.price_comparator.model;

import lombok.Data;

@Data
public class BestValueProduct {
    private String productId;
    private String productName;
    private String brand;
    private String store;
    private double pricePerUnit;
    private String unit; // kg, l, etc.
    private double packageQuantity;
    private double price;
    private String currency;
}
