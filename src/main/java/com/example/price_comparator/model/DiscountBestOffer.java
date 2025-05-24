package com.example.price_comparator.model;

import lombok.Data;

@Data
public class DiscountBestOffer {
    private String productId;
    private String productName;
    private String store;
    private int percentageOfDiscount;
    private String fromDate;
    private String toDate;
}
