package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "financial_obligation_payment")
public class FinancialObligationPayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "obligation_id", nullable = false)
    private FinancialObligation obligation;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private LocalDateTime paidAt;
    private String note;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_entry_id")
    private AccountingEntry accountingEntry;
}
