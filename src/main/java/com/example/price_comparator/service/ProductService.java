package com.example.price_comparator.service;

import com.example.price_comparator.model.*;
import com.example.price_comparator.util.CsvLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.*;
import java.io.File;

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

    public ShoppingPlan getBestBasketSplit(List<BasketItem> basket, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        Map<String, List<BasketProductDetail>> storeProducts = new HashMap<>();
        double grandTotal = 0.0;

        for (BasketItem item : basket) {
            ProductBestPrice best = getBestPriceForProduct(item.getProductId(), date);
            System.out.println("ProductId: " + item.getProductId() + ", Best: " + best);
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

    public List<Discount> getTopDiscounts(String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        List<Discount> allDiscounts = new ArrayList<>();

        for (String store : stores) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            try {
                allDiscounts.addAll(CsvLoader.loadFromResource(csvFile, Discount.class));
            } catch (Exception e) {
                // File might not exist for this store/date
            }
        }

        // Filter out any with null productId or zero discount, then sort descending
        return allDiscounts.stream()
                .filter(d -> d.getProductId() != null && d.getPercentageOfDiscount() > 0)
                .sorted((d1, d2) -> Integer.compare(d2.getPercentageOfDiscount(), d1.getPercentageOfDiscount()))
                .toList();
    }

    public List<Discount> getNewDiscounts(String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        List<Discount> newDiscounts = new ArrayList<>();

        for (String store : stores) {
            String csvFile = String.format("discounts/%s_discounts_%s.csv", store, date);
            try {
                List<Discount> storeDiscounts = CsvLoader.loadFromResource(csvFile, Discount.class);
                for (Discount d : storeDiscounts) {
                    // Compare only if from_date matches the query date
                    if (d.getFromDate() != null && d.getFromDate().toString().equals(date)) {
                        newDiscounts.add(d);
                    }
                }
            } catch (Exception e) {
                // File may not exist for that store/date
            }
        }
        return newDiscounts;
    }

    public List<PriceHistoryEntry> getPriceHistoryForProduct(String productId) {
        List<PriceHistoryEntry> history = new ArrayList<>();
        String[] stores = {"lidl", "profi", "kaufland"};

        for (String store : stores) {
            // List all files that start with this store's name
            try {
                // Assuming files are on the file system during dev
                File pricesDir = new File("src/main/resources/prices/");
                File[] storeFiles = pricesDir.listFiles((dir, name) -> name.startsWith(store + "_") && name.endsWith(".csv"));
                if (storeFiles != null) {
                    for (File file : storeFiles) {
                        // Extract the date from the filename
                        String filename = file.getName(); // e.g., lidl_2025-05-01.csv
                        String date = filename.replace(store + "_", "").replace(".csv", "");
                        // Load the CSV and check for the product
                        List<Product> products = CsvLoader.loadFromResource("prices/" + filename, Product.class);
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
                }
            } catch (Exception e) {
                // Ignore errors for missing dirs/files
            }
        }
        return history;
    }

    public List<BestValueProduct> getBestValueProducts(String category, String date) {
        String[] stores = {"lidl", "profi", "kaufland"};
        List<BestValueProduct> valueProducts = new ArrayList<>();

        for (String store : stores) {
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
                        // Calculate price per unit (avoid division by zero)
                        if (p.getPackageQuantity() > 0) {
                            best.setPricePerUnit(p.getPrice() / p.getPackageQuantity());
                        } else {
                            best.setPricePerUnit(0.0);
                        }
                        valueProducts.add(best);
                    }
                }
            } catch (Exception e) {
                // skip files not found
            }
        }
        // Sort by pricePerUnit ascending (best value first)
        valueProducts.sort((a, b) -> Double.compare(a.getPricePerUnit(), b.getPricePerUnit()));
        return valueProducts;
    }

    public List<BestValueProduct> getProductSubstitutes(String productId, String date) {
        Product ref = null;
        String[] stores = {"lidl", "profi", "kaufland"};
        search:
        for (String store : stores) {
            String csvFile = String.format("prices/%s_%s.csv", store, date);
            try {
                List<Product> storeProducts = CsvLoader.loadFromResource(csvFile, Product.class);
                for (Product p : storeProducts) {
                    if (p.getProductId().equalsIgnoreCase(productId)) {
                        ref = p;
                        break search;
                    }
                }
            } catch (Exception ignored) {}
        }
        if (ref == null) return List.of();

        String category = ref.getProductCategory();

        List<BestValueProduct> valueProducts = new ArrayList<>();
        for (String store : stores) {
            String csvFile = String.format("prices/%s_%s.csv", store, date);
            try {
                List<Product> products = CsvLoader.loadFromResource(csvFile, Product.class);
                for (Product p : products) {
                    if (
                            p.getProductCategory().equalsIgnoreCase(category)
                                    && !p.getProductId().equalsIgnoreCase(productId) // Exclude the original product!
                    ) {
                        BestValueProduct best = new BestValueProduct();
                        best.setProductId(p.getProductId());
                        best.setProductName(p.getProductName());
                        best.setBrand(p.getBrand());
                        best.setStore(store);
                        best.setPackageQuantity(p.getPackageQuantity());
                        best.setUnit(p.getPackageUnit());
                        best.setPrice(p.getPrice());
                        best.setCurrency(p.getCurrency());
                        if (p.getPackageQuantity() > 0) {
                            best.setPricePerUnit(p.getPrice() / p.getPackageQuantity());
                        } else {
                            best.setPricePerUnit(0.0);
                        }
                        valueProducts.add(best);
                    }
                }
            } catch (Exception e) {
                // skip missing files
            }
        }
        valueProducts.sort(Comparator.comparingDouble(BestValueProduct::getPricePerUnit));
        return valueProducts;
    }

    public boolean checkPriceAlert(String productId, String date, double targetPrice) {
        ProductBestPrice best = getBestPriceForProduct(productId, date);
        if (best == null) return false;
        return best.getPrice() <= targetPrice;
    }

    public List<Product> getProductsForStoreAndDate(String store, String date) {
        String csvFile = String.format("prices/%s_%s.csv", store, date);
        try {
            return CsvLoader.loadFromResource(csvFile, Product.class);
        } catch (Exception e) {
            return List.of(); // Or throw exception, your choice
        }
    }





}
