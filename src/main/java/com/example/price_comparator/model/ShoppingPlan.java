package com.example.price_comparator.model;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ShoppingPlan {
    private Map<String, List<BasketProductDetail>> storeProducts; // store -> products to buy there
    private double totalPrice;
}

