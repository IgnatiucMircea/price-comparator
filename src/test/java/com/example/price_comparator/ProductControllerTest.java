package com.example.price_comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllProductsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk());
    }

    @Test
    void getBestPriceShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/best-price/P001?date=2025-05-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("P001"));
    }
}
