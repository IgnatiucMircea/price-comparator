package com.example.price_comparator.service;

import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.DiscountBestOffer;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.ProductBestPrice;
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

    public ProductBestPrice getBestPriceForProduct(String productId, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        ProductBestPrice best = null;

        for (String store : stores) {
            String csvFile = String.format("prices/%s_%s.csv", store, date);
            List<Product> products;
            try {
                products = CsvLoader.loadFromResource(csvFile, Product.class);
            } catch (Exception e) {
                // File might not exist for this store+date, just skip
                continue;
            }
            for (Product p : products) {
                if (p.getProductId().equalsIgnoreCase(productId)) {
                    if (best == null || p.getPrice() < best.getPrice()) {
                        best = new ProductBestPrice();
                        best.setProductId(p.getProductId());
                        best.setProductName(p.getProductName());
                        best.setStore(store);
                        best.setPrice(p.getPrice());
                        best.setCurrency(p.getCurrency());
                    }
                }
            }
        }
        return best;
    }

    public DiscountBestOffer getBestDiscountForProduct(String productId, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        DiscountBestOffer best = null;

        for (String store : stores) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            List<Discount> discounts;
            try {
                discounts = CsvLoader.loadFromResource(csvFile, Discount.class);
            } catch (Exception e) {
                continue; // File might not exist for this store+date, skip
            }
            for (Discount d : discounts) {
                if (d.getProductId() == null) continue; // skip broken rows
                if (d.getProductId().equalsIgnoreCase(productId)) {
                    if (best == null || d.getPercentageOfDiscount() > best.getPercentageOfDiscount()) {
                        best = new DiscountBestOffer();
                        best.setProductId(d.getProductId());
                        best.setProductName(d.getProductName());
                        best.setStore(store);
                        best.setPercentageOfDiscount(d.getPercentageOfDiscount());
                        best.setFromDate(d.getFromDate().toString());
                        best.setToDate(d.getToDate().toString());
                    }
                }
            }
        }
        return best;
    }

    public List<Discount> getAllDiscountsForStoreAndDate(String store, String date) {
        String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
        try {
            return CsvLoader.loadFromResource(csvFile, Discount.class);
        } catch (Exception e) {
            // Could log: e.printStackTrace();
            return List.of(); // Return empty list if not found
        }
    }



}
