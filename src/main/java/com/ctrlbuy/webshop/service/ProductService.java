package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Hämtar alla produkter från databasen
     * @return lista med alla produkter
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Hämtar populära produkter baserat på visningar
     * @return lista med populära produkter (top 8)
     */
    public List<Product> getPopularProducts() {
        return productRepository.findTop8ByOrderByViewCountDesc();
    }

    /**
     * Hämtar produkter efter kategori
     * @param category kategorin som ska filtreras på
     * @return lista med produkter i den angivna kategorin
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Hämtar en produkt baserat på dess ID
     * @param id produkt-ID
     * @return produkten om den finns, annars null
     */
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    /**
     * Skapar eller uppdaterar en produkt
     * @param product produkten som ska sparas
     * @return den sparade produkten
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Tar bort en produkt baserat på dess ID
     * @param id produkt-ID som ska tas bort
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Söker efter produkter baserat på nyckelord (namn eller beskrivning)
     * @param keyword sökord
     * @return lista med matchande produkter
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    /**
     * Hämtar produkter inom ett visst prisintervall
     * @param minPrice minimipris
     * @param maxPrice maximipris
     * @return lista med produkter inom prisintervallet
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    /**
     * Inkrementerar visningsantalet för en produkt
     * @param productId produkt-ID
     */
    public void incrementViewCount(Long productId) {
        Product product = getProductById(productId);
        if (product != null) {
            // Kontrollera om viewCount är null och sätt till 1, annars inkrementera
            if (product.getViewCount() == null) {
                product.setViewCount(1);
            } else {
                product.setViewCount(product.getViewCount() + 1);
            }
            saveProduct(product);
        }
    }

    /**
     * Hämtar nyligen tillagda produkter
     * @return lista med nyligen tillagda produkter (top 10)
     */
    public List<Product> getNewArrivals() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }
}