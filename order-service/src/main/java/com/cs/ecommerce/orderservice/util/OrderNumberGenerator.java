package com.cs.ecommerce.orderservice.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderNumberGenerator {

    private static final String ORDER_PREFIX = "ORD";

    public static String generateOrderNumber(long orderId) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return ORDER_PREFIX + "-" + date + "-" + orderId;
    }
}
