package com.company.project.models;

import com.company.project.models.enums.Role;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // Tipul enum pentru rolul angajatului (junior sau senior)

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    private double balance; // Soldul personului

    // Constructor implicit
    public Person() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PurchaseOrder> getOrders() {
        return purchaseOrders;
    }

    public void setOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Metoda pentru adăugarea unei comenzi la lista de comenzi ale personului
    public void addOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrders.add(purchaseOrder);
    }

    public PurchaseOrder getOrder() {
        return null;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Alte metode specifice pentru gestionarea contului personului pot fi adăugate aici, dacă este nevoie

    static public Person fetchDefaultUser(){
        var person = new Person();
        person.setRole(Role.UNREGISTERED);
        return person;
    }
}
