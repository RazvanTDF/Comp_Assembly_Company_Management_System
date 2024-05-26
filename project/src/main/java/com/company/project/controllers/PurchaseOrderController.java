package com.company.project.controllers;

import com.company.project.models.Product;
import com.company.project.models.PurchaseOrder;
import com.company.project.services.OrderService;
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
    public List<PurchaseOrder> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/{id}")
    public PurchaseOrder getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("products/{id}")
    public List<Product> getProductsByOrderId(@PathVariable Long id) {
        return orderService.getProductsByOrderId(id);
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

    @GetMapping("/{userId}")
    public List<PurchaseOrder> getOrdersByUserId(Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
