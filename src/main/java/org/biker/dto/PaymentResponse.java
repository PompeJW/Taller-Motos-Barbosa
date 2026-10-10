package org.biker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        BigDecimal amount,
        LocalDateTime paymentDate,
        String clientName,
        String workOrderCode,   // opcional
        String sparePartName,   // opcional
        String paymentMethod,
        String paymentStatus,
        Long workOrderId   // 👈 nuevo campo
) {}
