package com.example.price_comparator.service;

import com.example.price_comparator.model.Product;
import com.example.price_comparator.util.CsvLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class ProductService {
    private List<Product> products;

    @PostConstruct
    public void init() {
        try {
            // Now just use the resource name, not a path!
            products = CsvLoader.loadFromResource("products.csv", Product.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        return products;
    }
}
