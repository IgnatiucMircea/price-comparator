package com.example.price_comparator.util;

import com.opencsv.bean.AbstractBeanField;
import java.time.LocalDate;

public class LocalDateConverter extends AbstractBeanField<LocalDate, String> {
    @Override
    protected LocalDate convert(String value) {
        return LocalDate.parse(value);
    }
}
