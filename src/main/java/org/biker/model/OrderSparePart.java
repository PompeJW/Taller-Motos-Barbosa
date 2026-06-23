package org.biker.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_spare_part")
public class OrderSparePart {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String repuesto;
    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "spare_part_id")
    private  SparePart sparePart;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public OrderSparePart(){}

    public OrderSparePart(Long id, String repuesto, Integer cantidad, SparePart sparePart, Status status) {
        this.id = id;
        this.repuesto = repuesto;
        this.cantidad = cantidad;
        this.sparePart=sparePart;
        this.status = status;
    }

}
