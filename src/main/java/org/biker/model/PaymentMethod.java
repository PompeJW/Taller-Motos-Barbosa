package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "payment-method"
)
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public PaymentMethod() {}

    public PaymentMethod(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
