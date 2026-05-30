package org.biker.dto;

public record SparePartRequest(
        String name,
        String description,
        String skuCode,
        Double priceBuy,
        Double priceSell,
        Integer stock,
        Integer minStock,
        Long brandId,
        Long categoryId,
        Long statusId
) {
}