package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_number", nullable = false, unique = true)
    private String saleNumber;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    // Cliente opcional
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Método de pago obligatorio
    //@ManyToOne
    //@JoinColumn(name = "payment_method_id", nullable = false)
    //private PaymentMethod paymentMethod;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    // Relación con detalles
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SaleDetail> details;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalePayment> payments = new ArrayList<>();


    public Sale() {}
}
