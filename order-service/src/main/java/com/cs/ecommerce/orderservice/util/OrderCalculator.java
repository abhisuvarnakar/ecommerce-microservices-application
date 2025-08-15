package com.cs.ecommerce.orderservice.util;

import com.cs.ecommerce.orderservice.dto.CartItemDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class OrderCalculator {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal SHIPPING_THRESHOLD = new BigDecimal("50.00");
    private static final BigDecimal SHIPPING_COST = new BigDecimal("5.00");

    public static OrderTotals calculateTotals(List<CartItemDTO> cartItems,
                                       Map<Long, ProductDTO> products) {
        BigDecimal subtotal = calculateSubtotal(cartItems, products);
        BigDecimal tax = calculateTax(subtotal);
        BigDecimal shipping = calculateShipping(subtotal);
        BigDecimal discount = calculateDiscount(subtotal);
        BigDecimal total = subtotal.add(tax).add(shipping).subtract(discount);

        return new OrderTotals(subtotal, tax, shipping, discount, total);
    }

    private static BigDecimal calculateSubtotal(List<CartItemDTO> cartItems,
                                         Map<Long, ProductDTO> products) {
        return cartItems.stream()
                .map(item -> {
                    ProductDTO product = products.get(item.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateShipping(BigDecimal subtotal) {
        return subtotal.compareTo(SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_COST;
    }

    private static BigDecimal calculateDiscount(BigDecimal subtotal) {
        // TODO: Implement discount logic based on promotions, coupons, etc.
        return BigDecimal.ZERO;
    }

    @Data
    @AllArgsConstructor
    public static class OrderTotals {
        private final BigDecimal subtotal;
        private final BigDecimal tax;
        private final BigDecimal shipping;
        private final BigDecimal discount;
        private final BigDecimal total;
    }
}
