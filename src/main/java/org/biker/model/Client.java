package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(
        name = "Client"
)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( name = "clinet_name", nullable = false)
    private String clientName;

    @Column( name = "documento", nullable = true)
    private String documento;

    // Relación con WorkOrder
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrder> workOrders;

}
