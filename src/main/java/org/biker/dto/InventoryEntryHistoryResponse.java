package org.biker.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record InventoryEntryHistoryResponse(Long id, LocalDateTime createdAt, Integer quantity, BigDecimal unitCost, BigDecimal previousAverageCost, BigDecimal newAverageCost, BigDecimal previousSalePrice, BigDecimal newSalePrice, Integer previousStock, Integer newStock, String note) {}
