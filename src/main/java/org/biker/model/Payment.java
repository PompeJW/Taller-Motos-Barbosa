package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
        name = "payment"
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Monto del pago
    @Column(nullable = false)
    private BigDecimal amount;

    // Fecha y hora del pago
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    // Relación con el cliente
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Relación opcional con una orden de trabajo
    @ManyToOne
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    // Relación opcional con un repuesto (venta directa)
    @ManyToOne
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;

    // Método de pago (efectivo, tarjeta, transferencia, etc.)
    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    public Payment() {}

    public Payment(BigDecimal amount, LocalDateTime paymentDate, Client client,
                   WorkOrder workOrder, SparePart sparePart, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.client = client;
        this.workOrder = workOrder;
        this.sparePart = sparePart;
        this.paymentMethod = paymentMethod;
    }

}
