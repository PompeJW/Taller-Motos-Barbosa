package org.biker.dto;
import java.math.BigDecimal;
public record InventoryEntryPreview(Long sparePartId, String product, Integer previousStock, Integer receivedQuantity, Integer resultingStock, BigDecimal previousAverageCost, BigDecimal incomingUnitCost, BigDecimal resultingAverageCost, BigDecimal currentSalePrice, BigDecimal suggestedSalePrice, BigDecimal resultingMarginPercent) {}
