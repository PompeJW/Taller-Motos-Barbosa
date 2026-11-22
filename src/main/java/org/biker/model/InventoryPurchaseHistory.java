package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "inventory_purchase_history", indexes = @Index(name="idx_purchase_history_part",columnList="spare_part_id"))
@Getter @Setter
public class InventoryPurchaseHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="spare_part_id",nullable=false) private SparePart sparePart;
    @Column(nullable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal unitCost;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal previousAverageCost;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal newAverageCost;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal previousSalePrice;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal newSalePrice;
    @Column(nullable=false) private Integer previousStock;
    @Column(nullable=false) private Integer newStock;
    @Column(length=255) private String note;
}
