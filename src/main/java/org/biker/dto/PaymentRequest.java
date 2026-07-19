package org.biker.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        Long clientId,
        Long workOrderId,   // opcional, si es pago de orden
        Long sparePartId,   // opcional, si es venta directa
        Long paymentMethodId
) {}
