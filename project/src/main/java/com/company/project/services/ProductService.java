package com.company.project.services;

import com.company.project.models.Product;
import com.company.project.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return (Product) productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        // Adaugă logica pentru a verifica și a adăuga produsul
        if (product.getPrice() < 0 || product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid product data");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = (Product) productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            // Adaugă logica pentru actualizarea produsului
            if (updatedProduct.getPrice() >= 0) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            if (updatedProduct.getDescription() != null) {
                existingProduct.setDescription(updatedProduct.getDescription());
            }
            if (updatedProduct.getStars() >= 0) {
                existingProduct.setStars(updatedProduct.getStars());
            }
            // Updatează restul câmpurilor după nevoie
            return productRepository.save(existingProduct);
        }
        return null;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
