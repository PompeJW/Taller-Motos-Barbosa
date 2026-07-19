package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(
        name = "work_order"
)
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con Client
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "customer_name")
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String model;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Observation> observations;

    @Column(name = "total_value")
    private BigDecimal  totalValue;

    private BigDecimal labour;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderDetail> details;

    @Column(name = "motorcycle_plate")
    private String motorcyclePlate;

    @ManyToOne
    @JoinColumn(name = "payment_status_id")
    private WorkOrderPaymentStatus paymentStatus;


    public WorkOrder() {
    }

    public WorkOrder(Long id, String orderNumber, LocalDateTime createdAt, LocalDateTime updatedAt, String customerName, Brand brand, String model, List<Observation> observations, BigDecimal totalValue, BigDecimal labour, Status status, List<WorkOrderDetail> details, String motorcyclePlate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customerName = customerName;
        this.brand = brand;
        this.model = model;
        this.observations = observations;
        this.totalValue = totalValue;
        this.labour = labour;
        this.status = status;
        this.details = details;
        this.motorcyclePlate = motorcyclePlate;
    }
}