package com.company.project.controllers;

import com.company.project.models.PurchaseOrder;
import com.company.project.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchaseOrders")
public class PurchaseOrderController {

    private final OrderService orderService;

    public PurchaseOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<PurchaseOrder> getAllOrders(@PathVariable Long personId) {
        List<PurchaseOrder> purchaseOrders = orderService.getAllOrders();
        return purchaseOrders;
    }


    @GetMapping("/{id}")
    public PurchaseOrder getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{id}")
    public PurchaseOrder addOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return orderService.addOrder(purchaseOrder);
    }

    @PutMapping("/{id}")
    public PurchaseOrder updateOrder(@PathVariable Long id, @RequestBody PurchaseOrder updatedPurchaseOrder) {
        return orderService.updateOrder(id, updatedPurchaseOrder);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
