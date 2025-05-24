package com.example.price_comparator.controller;
import java.util.List;

import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.price_comparator.model.ProductBestPrice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.price_comparator.model.DiscountBestOffer;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/best-price/{productId}")
    public ProductBestPrice getBestPrice(
            @PathVariable String productId,
            @RequestParam String date) {
        return productService.getBestPriceForProduct(productId, date);
    }

    @GetMapping("/best-discount/{productId}")
    public DiscountBestOffer getBestDiscount(
            @PathVariable String productId,
            @RequestParam String date) {
        return productService.getBestDiscountForProduct(productId, date);
    }

    @GetMapping("/discounts/{store}/{date}")
    public List<Discount> getDiscountsForStoreAndDate(
            @PathVariable String store,
            @PathVariable String date) {
        return productService.getAllDiscountsForStoreAndDate(store, date);
    }
}
