package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
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

        log.info("API: Getting products - category: {}, search: {}", category, search);

        try {
            List<Product> products;

            if (search != null && !search.trim().isEmpty()) {
                try {
                    products = productService.searchProducts(search.trim());
                } catch (Exception e) {
                    log.warn("Search method failed, trying alternative: {}", e.getMessage());
                    products = productService.getAllProducts();
                }
                log.info("API: Search found {} products for query: {}", products.size(), search);
            } else if (category != null && !category.trim().isEmpty() && !category.equals("Alla")) {
                try {
                    products = productService.getProductsByCategory(category);
                } catch (Exception e) {
                    log.warn("Category method failed, trying alternative: {}", e.getMessage());
                    products = productService.getAllProducts();
                }
                log.info("API: Category filter found {} products for: {}", products.size(), category);
            } else {
                products = productService.getAllProducts();
                log.info("API: Retrieved all {} products", products.size());
            }

            return ResponseEntity.ok(products);

        } catch (Exception e) {
            log.error("API: Error getting products", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/{id} - Hämta en specifik produkt (JSON)
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("API: Getting product by ID: {}", id);

        try {
            Optional<Product> productOpt = productService.findById(id);

            if (productOpt.isPresent()) {
                log.info("API: Found product: {}", productOpt.get().getName());
                return ResponseEntity.ok(productOpt.get());
            } else {
                log.warn("API: Product not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("API: Error getting product by ID: {}", id, e);
            // Fallback: försök med alternativ metod
            try {
                Product product = productService.getProductById(id);
                if (product != null) {
                    return ResponseEntity.ok(product);
                }
            } catch (Exception e2) {
                log.error("API: Fallback method also failed: {}", e2.getMessage());
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/category/{category} - Hämta produkter per kategori (JSON)
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        log.info("API: Getting products by category: {}", category);

        try {
            List<Product> products = productService.getProductsByCategory(category);
            log.info("API: Found {} products in category: {}", products.size(), category);
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            log.error("API: Error getting products by category: {}", category, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/search - Sök produkter (JSON)
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        log.info("API: Searching products with query: {}", q);

        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        try {
            List<Product> products = productService.searchProducts(q.trim());
            log.info("API: Search found {} products for: {}", products.size(), q);
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            log.error("API: Error searching products with query: {}", q, e);
            // Fallback: returnera alla produkter om sökning misslyckas
            try {
                List<Product> allProducts = productService.getAllProducts();
                return ResponseEntity.ok(allProducts.stream()
                        .filter(p -> p.getName().toLowerCase().contains(q.toLowerCase()))
                        .toList());
            } catch (Exception e2) {
                log.error("API: Fallback search also failed: {}", e2.getMessage());
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * GET /api/products/count - Räkna antal produkter (JSON)
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> getProductCount() {
        log.info("API: Getting product count");

        try {
            List<Product> products = productService.getAllProducts();
            long count = products.size();
            log.info("API: Product count: {}", count);
            return ResponseEntity.ok(count);

        } catch (Exception e) {
            log.error("API: Error getting product count", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/sale - Hämta produkter på rea (JSON)
     */
    @GetMapping("/products/sale")
    public ResponseEntity<List<Product>> getProductsOnSale() {
        log.info("API: Getting products on sale");

        try {
            List<Product> saleProducts = productService.getProductsOnSale();
            log.info("API: Found {} products on sale", saleProducts.size());
            return ResponseEntity.ok(saleProducts);

        } catch (Exception e) {
            log.error("API: Error getting products on sale", e);
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
        log.info("API: Getting popular products, limit: {}", limit);

        try {
            List<Product> popularProducts = productService.getPopularProducts(limit);
            log.info("API: Found {} popular products", popularProducts.size());
            return ResponseEntity.ok(popularProducts);

        } catch (Exception e) {
            log.error("API: Error getting popular products", e);
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
        log.info("API: Getting new products, limit: {}", limit);

        try {
            List<Product> newProducts = productService.getNewestProducts(limit);
            log.info("API: Found {} new products", newProducts.size());
            return ResponseEntity.ok(newProducts);

        } catch (Exception e) {
            log.error("API: Error getting new products", e);
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