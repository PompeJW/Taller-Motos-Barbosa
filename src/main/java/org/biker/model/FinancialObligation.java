package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "financial_obligation")
public class FinancialObligation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 15)
    private String type; // PAYABLE or RECEIVABLE
    @Column(nullable = false)
    private String counterparty;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal totalAmount;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_category_id")
    private MovementCategory category;
    private LocalDate dueDate;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private String status; // PENDING, PARTIAL, PAID
    @PrePersist
    void beforeInsert() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
