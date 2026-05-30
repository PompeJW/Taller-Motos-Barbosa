package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
        name = "spare_part",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "sku_code")
        }
)
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "price_buy")
    private Double priceBuy;

    @Column(name = "price_sell")
    private Double priceSell;

    private Integer stock;

    @Column(name = "min_stock")
    private Integer minStock;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public SparePart() {
    }

    public SparePart(Long id, String name, String description, String skuCode,
                     Double priceBuy, Double priceSell,
                     Integer stock, Integer minStock,
                     Brand brand, Category category, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.skuCode = skuCode;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
        this.stock = stock;
        this.minStock = minStock;
        this.brand = brand;
        this.category = category;
        this.status = status;
    }
}