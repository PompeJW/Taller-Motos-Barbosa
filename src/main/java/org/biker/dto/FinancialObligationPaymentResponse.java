package org.biker.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record FinancialObligationPaymentResponse(Long id, Long obligationId, BigDecimal amount,
    LocalDateTime paidAt, String note, Long accountingEntryId) {}
