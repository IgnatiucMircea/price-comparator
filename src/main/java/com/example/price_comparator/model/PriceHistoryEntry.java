package com.example.price_comparator.model;

import lombok.Data;

@Data
public class PriceHistoryEntry {
    private String date;
    private String store;
    private double price;
    private String currency;
}