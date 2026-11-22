package org.biker.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record FinancialObligationCreateRequest(String type, String counterparty, String description,
                                               BigDecimal totalAmount, LocalDate dueDate, Long categoryId) {}
