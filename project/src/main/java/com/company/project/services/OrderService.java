package com.company.project.services;

import com.company.project.models.Person;
import com.company.project.models.PurchaseOrder;
import com.company.project.models.Product;
import com.company.project.models.enums.ProductType;
import com.company.project.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService{
    private final OrderRepository orderRepository;
    @Autowired
    private PersonService personService;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public PurchaseOrder getOrderById(Long id) {
        return (PurchaseOrder) orderRepository.findById(id).orElse(null);
    }

    public PurchaseOrder addOrder(PurchaseOrder purchaseOrder) {
        // Verifică dacă produsele din comandă sunt disponibile în stoc sau dacă personul are suficiente fonduri
        for (Product product : purchaseOrder.getProducts()) {
            if (!checkProductAvailability(product)) {
                throw new RuntimeException("Product " + product.getName() + " is not available in stock");
            }
        }
        if (!checkPersonFunds(purchaseOrder.getPerson())) {
            throw new RuntimeException("Person does not have sufficient funds to place the order");
        }

        // Salvează comanda în baza de date
        return orderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updateOrder(Long id, PurchaseOrder updatedPurchaseOrder) {
        PurchaseOrder existingPurchaseOrder = (PurchaseOrder) orderRepository.findById(id).orElse(null);
        if (existingPurchaseOrder != null) {
            // Actualizează starea comenzii sau alte detalii specifice comenzii
            existingPurchaseOrder.setStatus(updatedPurchaseOrder.getStatus());
            existingPurchaseOrder.setDescription(updatedPurchaseOrder.getDescription());
            // Completează cu alte actualizări necesare
            return orderRepository.save(existingPurchaseOrder);
        }
        return null;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private boolean checkProductAvailability(Product product) {
        // Verificăm disponibilitatea produsului în stoc
        int availableQuantity = product.getQuantity(); // Presupunem că avem o metodă getQuantity() în clasa Product care returnează cantitatea disponibilă în stoc
        return availableQuantity > 0; // Returnăm true dacă există cantitate disponibilă în stoc, altfel returnăm false
    }

    private boolean checkPersonFunds(Person person) {
        // Verificăm dacă personul are suficiente fonduri pentru a plăti comanda
        double orderTotal = calculateOrderTotal(person.getOrder()); // Calculăm totalul comenzii
        return person.getBalance() >= orderTotal; // Returnăm true dacă soldul personului este mai mare sau egal cu totalul comenzii, altfel returnăm false
    }

    private double calculateOrderTotal(PurchaseOrder purchaseOrder) {
        // Calculăm totalul comenzii
        double total = 0;
        for (Product product : purchaseOrder.getProducts()) {
            total += product.getPrice(); // Adăugăm prețul fiecărui produs la totalul comenzii

            // Convertem tipul produsului la ProductType și verificăm dacă este un sistem pre-asamblat
            ProductType type = getProductType(product);
            if (type == ProductType.PRE_ASSEMBLED_SYSTEM) {
                total += 100; // Adăugăm taxa suplimentară pentru sistemele pre-asamblate
            }
        }
        return total;
    }

    // Metodă pentru a obține tipul produsului ca enum ProductType
    private ProductType getProductType(Product product) {
        // Implementăm logica pentru a obține tipul produsului
        // În acest exemplu, presupunem că avem o metodă getTip() în clasa Product care returnează tipul produsului sub formă de string
        // Vom folosi această metodă pentru a obține tipul și apoi îl vom converti la enum ProductType
        String typeAsString = String.valueOf(product.getType()); // Presupunem că avem o metodă getType() care returnează tipul sub formă de string
        return ProductType.valueOf(typeAsString); // Convertem string-ul la enum ProductType
    }
}
