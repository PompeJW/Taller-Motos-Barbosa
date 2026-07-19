package org.biker.dto;

import java.math.BigDecimal;

public record SaleItemResponse(
        String sparePartName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
