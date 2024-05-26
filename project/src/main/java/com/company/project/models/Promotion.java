package com.company.project.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private List<Long> productIds;// Id-urile produselor incluse în promoție
    // Constructor implicit
    public Promotion() {
        // Constructor implicit necesar pentru JPA
    }

    // Metodele getters și setters pentru câmpurile clasei

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
