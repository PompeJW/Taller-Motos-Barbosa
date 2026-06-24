package org.biker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(
        name = "work_order_detail"
)
public class WorkOrderDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Integer quantity;

        private BigDecimal unitPrice;

        @ManyToOne
        @JoinColumn(name = "work_order_id")
        @JsonIgnore
        private WorkOrder workOrder;

        @ManyToOne
        @JoinColumn(name = "spare_part_id")
        private SparePart sparePart;

    public WorkOrderDetail() {
    }

    public WorkOrderDetail(Long id, Integer quantity, BigDecimal unitPrice, WorkOrder workOrder, SparePart sparePart) {
        this.id = id;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.workOrder = workOrder;
        this.sparePart = sparePart;
    }
}
