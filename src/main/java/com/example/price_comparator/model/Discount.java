package com.example.price_comparator.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Discount {
    private String productId;
    private String productName;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String productCategory;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int percentageOfDiscount;
}
