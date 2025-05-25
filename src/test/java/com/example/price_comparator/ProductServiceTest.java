package com.example.price_comparator;

import static org.junit.jupiter.api.Assertions.*;

import com.example.price_comparator.model.*;
import com.example.price_comparator.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetAllProductsNotEmpty() {
        List<Product> products = productService.getAllProducts();
        assertNotNull(products, "Products should not be null");
        assertFalse(products.isEmpty(), "Products list should not be empty");
    }

    @Test
    public void testGetBestPriceForProduct() {
        ProductBestPrice best = productService.getBestPriceForProduct("P001", "2025-05-01");
        assertNotNull(best, "Should find best price for product P001");
        assertEquals("P001", best.getProductId());
    }

    @Test
    public void testGetTopDiscounts() {
        List<Discount> discounts = productService.getTopDiscounts("2025-05-01");
        assertNotNull(discounts);
        assertFalse(discounts.isEmpty(), "Should return at least one discount");
        for (int i = 1; i < discounts.size(); i++) {
            assertTrue(discounts.get(i-1).getPercentageOfDiscount() >= discounts.get(i).getPercentageOfDiscount());
        }
    }

    @Test
    public void testGetBestBasketSplit() {
        BasketItem item = new BasketItem();
        item.setProductId("P001");
        item.setQuantity(2);
        ShoppingPlan plan = productService.getBestBasketSplit(List.of(item), "2025-05-01");
        assertNotNull(plan);
        assertTrue(plan.getTotalPrice() > 0);
    }

    @Test
    public void testGetPriceHistoryForProduct() {
        List<PriceHistoryEntry> history = productService.getPriceHistoryForProduct("P001");
        assertNotNull(history);
        assertFalse(history.isEmpty(), "Should return price history for P001");
    }

    @Test
    public void testGetProductSubstitutes() {
        List<BestValueProduct> subs = productService.getProductSubstitutes("P001", "2025-05-01");
        assertNotNull(subs);
        assertFalse(subs.isEmpty(), "Should return substitutes for P001");
    }
}
