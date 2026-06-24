package org.biker.dto;

import java.math.BigDecimal;

public record SparePartResponse(
        Long id,
        String name,
        String description,
        String skuCode,
        BigDecimal priceBuy,
        BigDecimal priceSell,
        Integer stock,
        Integer minStock,
        String brand,
        String category,
        String status
) {
}