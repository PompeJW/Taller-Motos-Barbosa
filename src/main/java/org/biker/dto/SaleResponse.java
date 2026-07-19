package org.biker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        String saleNumber,
        LocalDateTime saleDate,
        String clientName,
        String paymentMethod,
        BigDecimal total,
        List<SaleItemResponse> items
) {}
