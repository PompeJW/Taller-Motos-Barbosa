package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    private BigDecimal priceBuy;

    @Column(name = "price_sell")
    private BigDecimal priceSell;

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

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubCategory subCategory;

    public SparePart() {
    }

    public SparePart(SubCategory subCategory, Status status, Category category, Brand brand, Integer minStock, Integer stock, BigDecimal priceSell,
                     BigDecimal priceBuy, String skuCode, String description, String name, Long id) {
        this.subCategory = subCategory;
        this.status = status;
        this.category = category;
        this.brand = brand;
        this.minStock = minStock;
        this.stock = stock;
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
        this.skuCode = skuCode;
        this.description = description;
        this.name = name;
        this.id = id;
    }
}