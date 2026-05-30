package org.biker.dto;

public record SparePartResponse(
        Long id,
        String name,
        String description,
        String skuCode,
        Double priceBuy,
        Double priceSell,
        Integer stock,
        Integer minStock,
        String brand,
        String category,
        String status
) {
}