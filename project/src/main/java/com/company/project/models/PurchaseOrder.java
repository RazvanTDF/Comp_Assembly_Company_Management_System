package com.company.project.models;

import com.company.project.models.enums.OrderStatus;
import com.company.project.models.enums.OrderType;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Transactional
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OneToMany(fetch=LAZY)
    private Person person;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String description; // Descrierea comenzii

    // Constructor implicit
    public PurchaseOrder() {
        // Constructor implicit necesar pentru JPA
    }

    // Metodele getters și setters pentru câmpurile clasei

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Metoda toString pentru a afișa detaliile comenzii
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", personId=" + person.getId() +
                ", type=" + type +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
