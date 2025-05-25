package com.example.price_comparator.util;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class CsvLoader {
    public static <T> List<T> loadFromResource(String resourceFileName, Class<T> clazz) {
        try {
            InputStream inputStream = CsvLoader.class.getClassLoader().getResourceAsStream(resourceFileName);
            if (inputStream == null) {
                throw new RuntimeException("Resource file not found: " + resourceFileName);
            }
            Reader reader = new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8);
            return new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withSeparator(';') // change to ',' if your CSV uses comma
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load or parse CSV file: " + resourceFileName, e);
        }
    }
}
