package org.biker.dto;

import java.math.BigDecimal;

public record SalePaymentRequest(Long paymentMethodId, BigDecimal amount) {}
