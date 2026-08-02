package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "movement_category")
public class MovementCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public MovementCategory() {
    }

    public MovementCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
