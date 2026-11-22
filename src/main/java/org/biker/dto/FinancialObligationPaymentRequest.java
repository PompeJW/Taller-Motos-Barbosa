package org.biker.dto;
import java.math.BigDecimal;
public record FinancialObligationPaymentRequest(BigDecimal amount, String note) {}
