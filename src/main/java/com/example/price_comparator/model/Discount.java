package com.example.price_comparator.model;

import com.example.price_comparator.util.LocalDateConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Discount {
    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvCustomBindByName(column = "from_date", converter = LocalDateConverter.class)
    private LocalDate fromDate;

    @CsvCustomBindByName(column = "to_date", converter = LocalDateConverter.class)
    private LocalDate toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private int percentageOfDiscount;
}
