package org.biker.dto;
import java.math.BigDecimal;
public record InventoryEntryRequest(Integer quantity, BigDecimal unitCost, BigDecimal salePrice, String note) {}
