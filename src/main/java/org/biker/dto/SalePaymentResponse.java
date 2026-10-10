package org.biker.dto;

import java.math.BigDecimal;

public record SalePaymentResponse(String methodName, BigDecimal amount) {}
