package org.biker.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record FinancialObligationResponse(Long id, String type, String counterparty, String description,
    BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal remainingAmount,
    LocalDate dueDate, LocalDateTime createdAt, String status, Long categoryId, String categoryName) {}
