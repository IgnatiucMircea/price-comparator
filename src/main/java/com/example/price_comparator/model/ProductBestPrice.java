package com.example.price_comparator.model;

import lombok.Data;

@Data
public class ProductBestPrice {
    private String productId;
    private String productName;
    private String store;
    private double price;
    private String currency;
}
