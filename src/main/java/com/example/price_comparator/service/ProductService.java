package com.example.price_comparator.service;

import com.example.price_comparator.model.*;
import com.example.price_comparator.util.CsvLoader;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.util.*;

@Service
public class ProductService {
    private List<Product> products;

    /**
     * Loads initial test product list from products.csv (if present).
     */
    @PostConstruct
    public void init() {
        try {
            products = CsvLoader.loadFromResource("products.csv", Product.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns initial static products (used for early testing).
     */
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Finds the best price for a product on a given date across all stores.
     */
    public ProductBestPrice getBestPriceForProduct(String productId, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        ProductBestPrice best = null;

        for (String store : stores) {
            String csvFile = String.format("prices/%s_%s.csv", store, date);
            List<Product> products;
            try {
                products = CsvLoader.loadFromResource(csvFile, Product.class);
            } catch (Exception e) {
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

    /**
     * Returns the best discount offer for a product on a specific date.
     */
    public DiscountBestOffer getBestDiscountForProduct(String productId, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        DiscountBestOffer best = null;

        for (String store : stores) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            try {
                List<Discount> discounts = CsvLoader.loadFromResource(csvFile, Discount.class);
                for (Discount d : discounts) {
                    if (d.getProductId() != null && d.getProductId().equalsIgnoreCase(productId)) {
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
            } catch (Exception e) {
                // skip
            }
        }
        return best;
    }

    /**
     * Returns all discounts for a given store and date.
     */
    public List<Discount> getAllDiscountsForStoreAndDate(String store, String date) {
        String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
        try {
            return CsvLoader.loadFromResource(csvFile, Discount.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Splits the basket optimally across stores for the given date.
     */
    public ShoppingPlan getBestBasketSplit(List<BasketItem> basket, String date) {
        Map<String, List<BasketProductDetail>> storeProducts = new HashMap<>();
        double grandTotal = 0.0;

        for (BasketItem item : basket) {
            ProductBestPrice best = getBestPriceForProduct(item.getProductId(), date);
            if (best == null) continue;

            BasketProductDetail detail = new BasketProductDetail();
            detail.setProductId(item.getProductId());
            detail.setProductName(best.getProductName());
            detail.setQuantity(item.getQuantity());
            detail.setPricePerUnit(best.getPrice());
            detail.setTotalPrice(best.getPrice() * item.getQuantity());
            grandTotal += detail.getTotalPrice();

            storeProducts.computeIfAbsent(best.getStore(), k -> new ArrayList<>()).add(detail);
        }

        ShoppingPlan plan = new ShoppingPlan();
        plan.setStoreProducts(storeProducts);
        plan.setTotalPrice(grandTotal);
        return plan;
    }

    /**
     * Returns the top discounted products by percentage.
     */
    public List<Discount> getTopDiscounts(String date) {
        List<Discount> allDiscounts = new ArrayList<>();
        for (String store : List.of("lidl", "profi", "kaufland")) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            try {
                allDiscounts.addAll(CsvLoader.loadFromResource(csvFile, Discount.class));
            } catch (Exception ignored) {}
        }

        return allDiscounts.stream()
                .filter(d -> d.getProductId() != null && d.getPercentageOfDiscount() > 0)
                .sorted(Comparator.comparingInt(Discount::getPercentageOfDiscount).reversed())
                .toList();
    }

    /**
     * Lists discounts that start on the exact provided date.
     */
    public List<Discount> getNewDiscounts(String date) {
        List<Discount> newDiscounts = new ArrayList<>();
        for (String store : List.of("lidl", "profi", "kaufland")) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            try {
                List<Discount> discounts = CsvLoader.loadFromResource(csvFile, Discount.class);
                for (Discount d : discounts) {
                    if (date.equals(String.valueOf(d.getFromDate()))) {
                        newDiscounts.add(d);
                    }
                }
            } catch (Exception ignored) {}
        }
        return newDiscounts;
    }

    /**
     * Returns historical prices for a product across available dates and stores.
     */
    public List<PriceHistoryEntry> getPriceHistoryForProduct(String productId) {
        List<PriceHistoryEntry> history = new ArrayList<>();
        for (String store : List.of("lidl", "profi", "kaufland")) {
            try {
                File pricesDir = new File("src/main/resources/prices/");
                File[] storeFiles = pricesDir.listFiles((dir, name) -> name.startsWith(store + "_") && name.endsWith(".csv"));
                if (storeFiles == null) continue;

                for (File file : storeFiles) {
                    String date = file.getName().replace(store + "_", "").replace(".csv", "");
                    List<Product> products = CsvLoader.loadFromResource("prices/" + file.getName(), Product.class);
                    for (Product p : products) {
                        if (p.getProductId().equalsIgnoreCase(productId)) {
                            PriceHistoryEntry entry = new PriceHistoryEntry();
                            entry.setDate(date);
                            entry.setStore(store);
                            entry.setPrice(p.getPrice());
                            entry.setCurrency(p.getCurrency());
                            history.add(entry);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return history;
    }

    /**
     * Returns best value products sorted by price per unit, optionally filtered by category.
     */
    public List<BestValueProduct> getBestValueProducts(String category, String date) {
        List<BestValueProduct> valueProducts = new ArrayList<>();
        for (String store : List.of("lidl", "profi", "kaufland")) {
            String csvFile = String.format("prices/%s_%s.csv", store, date);
            try {
                List<Product> products = CsvLoader.loadFromResource(csvFile, Product.class);
                for (Product p : products) {
                    if (category == null || p.getProductCategory().equalsIgnoreCase(category)) {
                        BestValueProduct best = new BestValueProduct();
                        best.setProductId(p.getProductId());
                        best.setProductName(p.getProductName());
                        best.setBrand(p.getBrand());
                        best.setStore(store);
                        best.setPackageQuantity(p.getPackageQuantity());
                        best.setUnit(p.getPackageUnit());
                        best.setPrice(p.getPrice());
                        best.setCurrency(p.getCurrency());
                        best.setPricePerUnit(p.getPackageQuantity() > 0 ? p.getPrice() / p.getPackageQuantity() : 0.0);
                        valueProducts.add(best);
                    }
                }
            } catch (Exception ignored) {}
        }
        valueProducts.sort(Comparator.comparingDouble(BestValueProduct::getPricePerUnit));
        return valueProducts;
    }

    /**
     * Suggests alternative products in the same category with better value per unit.
     */
    public List<BestValueProduct> getProductSubstitutes(String productId, String date) {
        Product ref = null;
        for (String store : List.of("lidl", "profi", "kaufland")) {
            try {
                List<Product> storeProducts = CsvLoader.loadFromResource(String.format("prices/%s_%s.csv", store, date), Product.class);
                ref = storeProducts.stream().filter(p -> p.getProductId().equalsIgnoreCase(productId)).findFirst().orElse(null);
                if (ref != null) break;
            } catch (Exception ignored) {}
        }
        if (ref == null) return List.of();

        String category = ref.getProductCategory();
        List<BestValueProduct> valueProducts = new ArrayList<>();
        for (String store : List.of("lidl", "profi", "kaufland")) {
            try {
                List<Product> products = CsvLoader.loadFromResource(String.format("prices/%s_%s.csv", store, date), Product.class);
                for (Product p : products) {
                    if (p.getProductCategory().equalsIgnoreCase(category) && !p.getProductId().equalsIgnoreCase(productId)) {
                        BestValueProduct best = new BestValueProduct();
                        best.setProductId(p.getProductId());
                        best.setProductName(p.getProductName());
                        best.setBrand(p.getBrand());
                        best.setStore(store);
                        best.setPackageQuantity(p.getPackageQuantity());
                        best.setUnit(p.getPackageUnit());
                        best.setPrice(p.getPrice());
                        best.setCurrency(p.getCurrency());
                        best.setPricePerUnit(p.getPackageQuantity() > 0 ? p.getPrice() / p.getPackageQuantity() : 0.0);
                        valueProducts.add(best);
                    }
                }
            } catch (Exception ignored) {}
        }
        valueProducts.sort(Comparator.comparingDouble(BestValueProduct::getPricePerUnit));
        return valueProducts;
    }

    /**
     * Checks if the product price is less than or equal to the target price.
     */
    public boolean checkPriceAlert(String productId, String date, double targetPrice) {
        ProductBestPrice best = getBestPriceForProduct(productId, date);
        return best != null && best.getPrice() <= targetPrice;
    }

    /**
     * Loads products available at a specific store and date.
     */
    public List<Product> getProductsForStoreAndDate(String store, String date) {
        try {
            return CsvLoader.loadFromResource(String.format("prices/%s_%s.csv", store, date), Product.class);
        } catch (Exception e) {
            return List.of();
        }
    }
}
