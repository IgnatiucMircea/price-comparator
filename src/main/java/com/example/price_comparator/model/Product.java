package com.example.price_comparator.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Product {
    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "price")
    private double price;

    @CsvBindByName(column = "currency")
    private String currency;
}
