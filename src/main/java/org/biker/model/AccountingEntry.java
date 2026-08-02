package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "accounting_entry")
public class AccountingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    // Monto
    @Column(nullable = false)
    private BigDecimal amount;

    // Fecha y hora
    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    // Descripción (ej: "Pago orden #123", "Venta libre", "Compra repuestos")
    @Column(nullable = false)
    private String description;

    // Relación opcional con Payment
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // Relación opcional con Sale
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    // Relación opcional con categoría de egreso (ej: nómina, servicios, inventario)
    @ManyToOne
    @JoinColumn(name = "expense_category_id")
    private MovementCategory expenseCategory;

    public AccountingEntry() {
    }

    public AccountingEntry(Long id, String type, BigDecimal amount, LocalDateTime entryDate, String description, Payment payment, Sale sale, MovementCategory expenseCategory) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.entryDate = entryDate;
        this.description = description;
        this.payment = payment;
        this.sale = sale;
        this.expenseCategory = expenseCategory;
    }
}
