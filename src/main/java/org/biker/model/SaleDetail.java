package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "sale_detail")
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la venta
    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    // Relación con el repuesto
    @ManyToOne
    @JoinColumn(name = "spare_part_id", nullable = false)
    private SparePart sparePart;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public SaleDetail() {}
}
