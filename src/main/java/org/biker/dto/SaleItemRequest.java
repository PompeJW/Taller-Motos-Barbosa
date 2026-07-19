package org.biker.dto;

import java.math.BigDecimal;

public record SaleItemRequest(
        Long sparePartId,      // repuesto
        Integer quantity,      // cantidad
        BigDecimal unitPrice   // precio unitario (editable en liquidación)
) {}
