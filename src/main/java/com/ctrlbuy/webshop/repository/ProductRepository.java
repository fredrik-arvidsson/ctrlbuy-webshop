package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Hämta top 8 produkter sorterade efter visningsantal (descending)
    List<Product> findTop8ByOrderByViewCountDesc();

    // Sök efter produkter som innehåller sökord i namn eller beskrivning
    List<Product> findByNameContainingOrDescriptionContainingIgnoreCase(String name, String description);

    // Använd category direkt som en String
    List<Product> findByCategory(String category);

    // Hämta nyligen tillagda produkter (top 10)
    List<Product> findTop10ByOrderByCreatedAtDesc();
}