package org.biker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountingEntryRequest(
        String type,
        BigDecimal amount,
        LocalDateTime entryDate,
        String description,
        Long payment,
        Long sale,
        Long expenseCategory
) {
}