package org.biker.dto;

import java.math.BigDecimal;

public record SparePartRequest(
        String name,
        String description,
        String skuCode,
        BigDecimal priceBuy,
        BigDecimal priceSell,
        Integer stock,
        Integer minStock,
        Long brandId,
        Long categoryId,
        Long statusId,
        Long subCategory
) {
}