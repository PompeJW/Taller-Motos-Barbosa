package org.biker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "subcategory")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;

    public SubCategory() {
    }

    public SubCategory(Long id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
}
