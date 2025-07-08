package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST API Controller för produkter
 * Hanterar JSON-endpoints för frontend JavaScript
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Tillåt CORS för frontend
public class ProductApiController {

    private final ProductService productService;

    /**
     * GET /api/products - Hämta alla produkter (JSON)
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        try {
            List<Product> products;

            if (search != null && !search.trim().isEmpty()) {
                try {
                    products = productService.searchProducts(search.trim());
                } catch (Exception e) {
                    products = productService.getAllProducts();
                }
            } else if (category != null && !category.trim().isEmpty() && !category.equals("Alla")) {
                try {
                    products = productService.getProductsByCategory(category);
                } catch (Exception e) {
                    products = productService.getAllProducts();
                }
            } else {
                products = productService.getAllProducts();
            }

            return ResponseEntity.ok(products);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/{id} - Hämta en specifik produkt (JSON)
     * FIX: RAD 80 - Optional<Product> conversion error löst
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {

        try {
            // FIX: Använd direkt Optional chain istället för conversion
            return productService.findById(id)
                    .map(product -> ResponseEntity.ok(product))
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            // Fallback: försök med alternativ metod
            try {
                Optional<Product> productOpt = productService.getProductById(id);
                if (productOpt.isPresent()) {
                    return ResponseEntity.ok(productOpt.get());
                }
            } catch (Exception e2) {
                // Ignore fallback errors
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/category/{category} - Hämta produkter per kategori (JSON)
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {

        try {
            List<Product> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/search - Sök produkter (JSON)
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {

        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        try {
            List<Product> products = productService.searchProducts(q.trim());
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            // Fallback: returnera alla produkter om sökning misslyckas
            try {
                List<Product> allProducts = productService.getAllProducts();
                return ResponseEntity.ok(allProducts.stream()
                        .filter(p -> p.getName().toLowerCase().contains(q.toLowerCase()))
                        .toList());
            } catch (Exception e2) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * GET /api/products/count - Räkna antal produkter (JSON)
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> getProductCount() {

        try {
            List<Product> products = productService.getAllProducts();
            long count = products.size();
            return ResponseEntity.ok(count);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/sale - Hämta produkter på rea (JSON)
     */
    @GetMapping("/products/sale")
    public ResponseEntity<List<Product>> getProductsOnSale() {

        try {
            List<Product> saleProducts = productService.getProductsOnSale();
            return ResponseEntity.ok(saleProducts);

        } catch (Exception e) {
            // Fallback: försök hitta produkter med salePrice > 0
            try {
                List<Product> allProducts = productService.getAllProducts();
                List<Product> saleProducts = allProducts.stream()
                        .filter(p -> {
                            try {
                                return p.getSalePrice() != null && p.getSalePrice().compareTo(BigDecimal.ZERO) > 0;
                            } catch (Exception ex) {
                                return false;
                            }
                        })
                        .toList();
                return ResponseEntity.ok(saleProducts);
            } catch (Exception e2) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * GET /api/products/popular - Hämta populära produkter (JSON)
     */
    @GetMapping("/products/popular")
    public ResponseEntity<List<Product>> getPopularProducts(@RequestParam(defaultValue = "12") int limit) {

        try {
            List<Product> popularProducts = productService.getPopularProducts(limit);
            return ResponseEntity.ok(popularProducts);

        } catch (Exception e) {
            // Fallback: returnera första X produkter
            try {
                List<Product> allProducts = productService.getAllProducts();
                List<Product> limitedProducts = allProducts.stream()
                        .limit(limit)
                        .toList();
                return ResponseEntity.ok(limitedProducts);
            } catch (Exception e2) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * GET /api/products/new - Hämta nya produkter (JSON)
     */
    @GetMapping("/products/new")
    public ResponseEntity<List<Product>> getNewProducts(@RequestParam(defaultValue = "12") int limit) {

        try {
            List<Product> newProducts = productService.getNewestProducts(limit);
            return ResponseEntity.ok(newProducts);

        } catch (Exception e) {
            // Fallback: returnera sista X produkter
            try {
                List<Product> allProducts = productService.getAllProducts();
                List<Product> limitedProducts = allProducts.stream()
                        .skip(Math.max(0, allProducts.size() - limit))
                        .toList();
                return ResponseEntity.ok(limitedProducts);
            } catch (Exception e2) {
                return ResponseEntity.ok(List.of());
            }
        }
    }
}