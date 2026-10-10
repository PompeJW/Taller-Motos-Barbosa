package org.biker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountingEntryResponse(
        Long id,
        String type,
        BigDecimal amount,
        LocalDateTime entryDate,
        String description,
        Long paymentId,
        Long saleId,
        Long expenseCategoryId,
        String expenseCategoryName,
        Long workOrderId   // 👈 nuevo campo
) {
    public static AccountingEntryResponse fromEntity(org.biker.model.AccountingEntry entry) {
        return new AccountingEntryResponse(
                entry.getId(),
                entry.getType(),
                entry.getAmount(),
                entry.getEntryDate(),
                entry.getDescription(),
                entry.getPayment() != null ? entry.getPayment().getId() : null,
                entry.getSale() != null ? entry.getSale().getId() : null,
                entry.getExpenseCategory() != null ? entry.getExpenseCategory().getId() : null,
                entry.getExpenseCategory() != null ? entry.getExpenseCategory().getName() : null,
                entry.getPayment() != null && entry.getPayment().getWorkOrder() != null
                        ? entry.getPayment().getWorkOrder().getId()
                        : null
        );
    }
}
