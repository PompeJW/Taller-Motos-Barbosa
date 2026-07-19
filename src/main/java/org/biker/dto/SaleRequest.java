package org.biker.dto;

import java.util.List;

public record SaleRequest(
        Long clientId,
        String customerName,
        String documento,
        Long paymentMethodId,
        List<SaleItemRequest> items
) {}
