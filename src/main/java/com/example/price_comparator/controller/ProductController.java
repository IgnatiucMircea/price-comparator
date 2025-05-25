package com.example.price_comparator.controller;
import java.util.List;

import com.example.price_comparator.model.*;
import com.example.price_comparator.service.ProductService;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/basket/best-split")
    public ShoppingPlan getBestBasketSplit(
            @RequestBody List<BasketItem> basket,
            @RequestParam String date) {
        return productService.getBestBasketSplit(basket, date);
    }

    @GetMapping("/discounts/top")
    public List<Discount> getTopDiscounts(@RequestParam String date) {
        return productService.getTopDiscounts(date);
    }

    @GetMapping("/discounts/new")
    public List<Discount> getNewDiscounts(@RequestParam String date) {
        return productService.getNewDiscounts(date);
    }

    @GetMapping("/products/{productId}/price-history")
    public List<PriceHistoryEntry> getPriceHistory(
            @PathVariable String productId
    ) {
        return productService.getPriceHistoryForProduct(productId);
    }

    @GetMapping("/products/best-value")
    public List<BestValueProduct> getBestValueProducts(
            @RequestParam(required = false) String category,
            @RequestParam String date) {
        return productService.getBestValueProducts(category, date);
    }

    @GetMapping("/products/{productId}/substitutes")
    public List<BestValueProduct> getProductSubstitutes(
            @PathVariable String productId,
            @RequestParam String date) {
        return productService.getProductSubstitutes(productId, date);
    }

    @PostMapping("/products/{productId}/alert")
    public boolean checkPriceAlert(
            @PathVariable String productId,
            @RequestParam String date,
            @RequestBody PriceAlertRequest request
    ) {
        return productService.checkPriceAlert(productId, date, request.getTargetPrice());
    }

    @GetMapping("/products/{store}/{date}")
    public List<Product> getProductsForStoreAndDate(
            @PathVariable String store,
            @PathVariable String date) {
        return productService.getProductsForStoreAndDate(store, date);
    }

}
