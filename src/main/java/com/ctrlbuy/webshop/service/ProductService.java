package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Method needed by CartController
    public Optional<Product> getProductByIdWithoutView(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // SAKNADE METODER - Lägger till dessa för att fixa compilation errors:

    // 1. findById() - används av controllers (samma som getProductById men utan Optional wrapper i vissa fall)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // 2. ✅ FIXAD getProductsOnSale() - för kampanjprodukter
    public List<Product> getProductsOnSale() {
        return productRepository.findAll().stream()
                .filter(product -> {
                    try {
                        // Försök med isOnSale() metoden först (nya databaser)
                        Boolean isOnSale = product.isOnSale();
                        if (isOnSale != null && isOnSale) {
                            return true;
                        }

                        // Fallback 1: kolla getOnSale() direktattribut
                        Boolean onSale = product.getOnSale();
                        if (onSale != null && onSale) {
                            return true;
                        }

                        // Fallback 2: kolla discountPercentage (gamla metoden)
                        BigDecimal discount = product.getDiscountPercentage();
                        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                            return true;
                        }

                        // Fallback 3: kolla om salePrice finns och är lägre än price
                        BigDecimal salePrice = product.getSalePrice();
                        BigDecimal regularPrice = product.getPrice();
                        if (salePrice != null && regularPrice != null &&
                                salePrice.compareTo(regularPrice) < 0) {
                            return true;
                        }

                        return false;

                    } catch (Exception e) {
                        // Om något går fel, returnera false
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // 3. getPopularProducts(int limit) - för populära produkter med limit
    public List<Product> getPopularProducts(int limit) {
        return productRepository.findAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 4. getNewestProducts(int limit) - för senaste produkter med limit
    public List<Product> getNewestProducts(int limit) {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 5. searchActiveProducts() - för produktsökning
    public List<Product> searchActiveProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }

        return productRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (product.getDescription() != null &&
                                product.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    // Extra: Om ni behöver en metod för att hitta produkt utan Optional (för vissa controllers)
    public Product getProductByIdOrNull(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}