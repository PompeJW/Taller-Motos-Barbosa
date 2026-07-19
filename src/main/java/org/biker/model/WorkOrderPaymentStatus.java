package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "work_order_payment_status")
public class WorkOrderPaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // "Pendiente", "Pagado", "Fiado", "Abonos"
}
