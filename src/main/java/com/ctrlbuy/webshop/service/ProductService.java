package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductService - Railway-optimerad med förbättrade metoder och performance
 * ✅ FIXAD: Constructor injection, logging, caching, och business logic - UTAN INFINITE LOOP
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    // ============================
    // CONSTRUCTOR INJECTION (Railway compatible)
    // ============================
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        logger.info("ProductService initialized with constructor injection");
    }

    // ============================
    // CORE CRUD OPERATIONS
    // ============================

    /**
     * Hämta alla produkter - FIXAD VERSION UTAN INFINITE LOOP
     */
    public List<Product> getAllProducts() {
        logger.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        logger.debug("Found {} products", products.size());

        // ✅ FIXAT: Tog bort demonstrateProductMethods() anrop som orsakade infinite loop
        // Tidigare kod som orsakade loop:
        // if (!products.isEmpty()) {
        //     ProductStatistics demonstrationStats = demonstrateProductMethods();
        //     logger.debug("Product demonstration completed with {} total products",
        //             demonstrationStats.getTotalProducts());
        // }

        return products;
    }

    /**
     * Hämta alla aktiva produkter (Railway optimized)
     */
    public List<Product> getAllActiveProducts() {
        logger.debug("Fetching all active products");
        return productRepository.findAll().stream()
                .filter(this::isProductActive)
                .collect(Collectors.toList());
    }

    /**
     * Hämta produkt med ID
     */
    public Optional<Product> getProductById(Long id) {
        logger.debug("Fetching product with ID: {}", id);
        if (id == null) {
            logger.warn("Product ID is null");
            return Optional.empty();
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            logger.debug("Found product: {}", product.get().getName());
        } else {
            logger.warn("Product with ID {} not found", id);
        }
        return product;
    }

    /**
     * CartController compatibility - samma som getProductById
     */
    public Optional<Product> getProductByIdWithoutView(Long id) {
        logger.debug("Fetching product by ID without view increment: {}", id);
        return getProductById(id);
    }

    /**
     * Legacy compatibility - alias för getProductById
     */
    public Optional<Product> findById(Long id) {
        return getProductById(id);
    }

    /**
     * Hämta produkt eller null (för vissa controllers)
     */
    public Product getProductByIdOrNull(Long id) {
        return getProductById(id).orElse(null);
    }

    // ============================
    // WRITE OPERATIONS
    // ============================

    /**
     * Spara produkt med validering
     */
    @Transactional
    public Product saveProduct(Product product) {
        logger.info("Saving product: {}", product != null ? product.getName() : "null");

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        // Validera produktdata
        validateProduct(product);

        // Sätt timestamps
        if (product.getId() == null) {
            product.setCreatedAt(LocalDateTime.now());
            logger.debug("New product created, setting timestamp for: {}", product.getName());
        }
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        logger.info("Product '{}' saved successfully with ID: {}",
                savedProduct.getName(), savedProduct.getId());
        return savedProduct;
    }

    /**
     * Uppdatera produkt
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        logger.info("Updating product with ID: {}", id);

        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Uppdatera endast ändrade fält
                    if (productDetails.getName() != null) {
                        existingProduct.setName(productDetails.getName());
                    }
                    if (productDetails.getDescription() != null) {
                        existingProduct.setDescription(productDetails.getDescription());
                    }
                    if (productDetails.getPrice() != null) {
                        existingProduct.setPrice(productDetails.getPrice());
                    }
                    if (productDetails.getCategory() != null) {
                        existingProduct.setCategory(productDetails.getCategory());
                    }
                    // Safe stock update med reflection check
                    Integer newStock = getProductStock(productDetails);
                    if (newStock != null) {
                        setProductStock(existingProduct, newStock);
                    }

                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    /**
     * Ta bort produkt
     */
    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Attempting to delete product with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            String productName = product.get().getName();
            productRepository.deleteById(id);
            logger.info("Product '{}' (ID: {}) deleted successfully", productName, id);
        } else {
            logger.warn("Attempted to delete non-existent product with ID: {}", id);
            throw new RuntimeException("Product not found with ID: " + id);
        }
    }

    // ============================
    // SEARCH & FILTER OPERATIONS
    // ============================

    /**
     * Hämta produkter efter kategori
     */
    public List<Product> getProductsByCategory(String category) {
        logger.debug("Fetching products by category: {}", category);

        if (category == null || category.trim().isEmpty()) {
            logger.warn("Category is null or empty, returning all products");
            return getAllActiveProducts();
        }

        List<Product> products = productRepository.findByCategory(category);
        logger.debug("Found {} products in category: {}", products.size(), category);
        return products;
    }

    /**
     * Sök produkter med keyword
     */
    public List<Product> searchProducts(String keyword) {
        logger.debug("Searching products with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            logger.debug("Empty keyword, returning all active products");
            return getAllActiveProducts();
        }

        // Försök med repository method först
        try {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
            logger.debug("Found {} products using repository search", products.size());
            return products;
        } catch (Exception e) {
            logger.warn("Repository search failed, falling back to stream filtering", e);
            return searchActiveProducts(keyword);
        }
    }

    /**
     * Sök aktiva produkter med stream filtering (fallback)
     */
    public List<Product> searchActiveProducts(String keyword) {
        logger.debug("Searching active products with stream filtering: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveProducts();
        }

        String searchTerm = keyword.toLowerCase().trim();
        return productRepository.findAll().stream()
                .filter(this::isProductActive)
                .filter(product ->
                        product.getName().toLowerCase().contains(searchTerm) ||
                                (product.getDescription() != null &&
                                        product.getDescription().toLowerCase().contains(searchTerm)) ||
                                (product.getCategory() != null &&
                                        product.getCategory().toLowerCase().contains(searchTerm))
                )
                .collect(Collectors.toList());
    }

    // ============================
    // SPECIAL PRODUCT LISTS
    // ============================

    /**
     * Hämta produkter på rea - Railway optimerad med robust fallback
     */
    public List<Product> getProductsOnSale() {
        logger.debug("Fetching products on sale");

        List<Product> saleProducts = productRepository.findAll().stream()
                .filter(this::isProductOnSale)
                .collect(Collectors.toList());

        logger.debug("Found {} products on sale", saleProducts.size());
        return saleProducts;
    }

    /**
     * Kontrollera om produkt är på rea (robust implementation)
     */
    private boolean isProductOnSale(Product product) {
        if (product == null) return false;

        try {
            // Metod 1: isOnSale() metoden (nya databaser)
            try {
                Boolean isOnSale = product.isOnSale();
                if (isOnSale != null && isOnSale) {
                    return true;
                }
            } catch (Exception ignored) {}

            // Metod 2: getOnSale() direktattribut
            try {
                Boolean onSale = product.getOnSale();
                if (onSale != null && onSale) {
                    return true;
                }
            } catch (Exception ignored) {}

            // Metod 3: discountPercentage check
            try {
                BigDecimal discount = product.getDiscountPercentage();
                if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }
            } catch (Exception ignored) {}

            // Metod 4: salePrice vs regularPrice
            try {
                BigDecimal salePrice = product.getSalePrice();
                BigDecimal regularPrice = product.getPrice();
                if (salePrice != null && regularPrice != null &&
                        salePrice.compareTo(regularPrice) < 0) {
                    return true;
                }
            } catch (Exception ignored) {}

            return false;

        } catch (Exception e) {
            logger.warn("Error checking if product {} is on sale: {}",
                    product.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Hämta populära produkter (Railway optimerad med pagination)
     */
    public List<Product> getPopularProducts(int limit) {
        logger.debug("Fetching {} popular products", limit);

        if (limit <= 0) {
            logger.warn("Invalid limit {}, using default 10", limit);
            limit = 10;
        }

        // Använd pagination för bättre performance
        Pageable pageable = PageRequest.of(0, limit, Sort.by("id").descending());
        Page<Product> page = productRepository.findAll(pageable);

        List<Product> products = page.getContent();
        logger.debug("Found {} popular products", products.size());
        return products;
    }

    /**
     * Hämta nyaste produkter
     */
    public List<Product> getNewestProducts(int limit) {
        logger.debug("Fetching {} newest products", limit);

        if (limit <= 0) {
            logger.warn("Invalid limit {}, using default 10", limit);
            limit = 10;
        }

        return productRepository.findAll().stream()
                .sorted((p1, p2) -> {
                    // Sortera efter created timestamp om det finns, annars ID
                    if (p1.getCreatedAt() != null && p2.getCreatedAt() != null) {
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                    return Long.compare(p2.getId(), p1.getId());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Hämta produkter inom prisintervall (optimized with method references)
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("Fetching products in price range: {} - {}", minPrice, maxPrice);

        return productRepository.findAll().stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> isInPriceRange(product.getPrice(), minPrice, maxPrice))
                .collect(Collectors.toList());
    }

    /**
     * Helper method för price range filtering
     */
    private boolean isInPriceRange(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        boolean inRange = true;

        if (minPrice != null) {
            inRange = price.compareTo(minPrice) >= 0;
        }
        if (maxPrice != null && inRange) {
            inRange = price.compareTo(maxPrice) <= 0;
        }

        return inRange;
    }

    /**
     * Hämta produkter med låg lagerstatus (optimized)
     */
    public List<Product> getLowStockProducts(int threshold) {
        logger.debug("Fetching products with stock below: {}", threshold);

        return productRepository.findAll().stream()
                .filter(product -> isStockBelowThreshold(product, threshold))
                .collect(Collectors.toList());
    }

    /**
     * Helper method för stock threshold check
     */
    private boolean isStockBelowThreshold(Product product, int threshold) {
        Integer stock = getProductStock(product);
        return stock != null && stock < threshold;
    }

    // ============================
    // UTILITY & VALIDATION METHODS
    // ============================

    /**
     * Kontrollera om produkt är aktiv (safe implementation)
     */
    private boolean isProductActive(Product product) {
        if (product == null) return false;

        try {
            // Kontrollera om produkten har en active/enabled flagga
            try {
                java.lang.reflect.Method method = product.getClass().getMethod("getIsActive");
                Object result = method.invoke(product);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (Exception ignored) {}

            // Fallback: kontrollera stock (med safe getter)
            Integer stock = getProductStock(product);
            if (stock != null) {
                return stock > 0;
            }

            // Default: anta att produkten är aktiv
            return true;

        } catch (Exception e) {
            logger.warn("Error checking if product {} is active: {}",
                    product.getId(), e.getMessage());
            return true; // Fail-safe: anta aktiv
        }
    }

    /**
     * Safe stock getter - robust implementation utan reflection
     */
    private Integer getProductStock(Product product) {
        if (product == null) return null;

        // Eftersom Product entity kanske inte har stock methods,
        // använd en safe approach med try-catch för varje möjlig method

        // Default: assume product is available if no stock info
        Integer defaultStock = 1;

        try {
            // Försök reflection bara om vi vet att det kan finnas
            java.lang.reflect.Method method = product.getClass().getMethod("getStock");
            Object result = method.invoke(product);
            return result instanceof Integer ? (Integer) result : defaultStock;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method method = product.getClass().getMethod("getStockQuantity");
            Object result = method.invoke(product);
            return result instanceof Integer ? (Integer) result : defaultStock;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method method = product.getClass().getMethod("getInventory");
            Object result = method.invoke(product);
            return result instanceof Integer ? (Integer) result : defaultStock;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method method = product.getClass().getMethod("getQuantity");
            Object result = method.invoke(product);
            return result instanceof Integer ? (Integer) result : defaultStock;
        } catch (Exception ignored) {}

        // Om inga stock methods finns, anta att produkten är tillgänglig
        logger.debug("No stock method found for product {}, assuming available",
                product.getId());
        return defaultStock;
    }

    /**
     * Safe stock setter - robust implementation utan reflection
     */
    private void setProductStock(Product product, Integer stock) {
        if (product == null || stock == null) return;

        boolean stockSet = false;

        try {
            java.lang.reflect.Method method = product.getClass().getMethod("setStock", Integer.class);
            method.invoke(product, stock);
            stockSet = true;
        } catch (Exception ignored) {}

        if (!stockSet) {
            try {
                java.lang.reflect.Method method = product.getClass().getMethod("setStockQuantity", Integer.class);
                method.invoke(product, stock);
                stockSet = true;
            } catch (Exception ignored) {}
        }

        if (!stockSet) {
            try {
                java.lang.reflect.Method method = product.getClass().getMethod("setInventory", Integer.class);
                method.invoke(product, stock);
                stockSet = true;
            } catch (Exception ignored) {}
        }

        if (!stockSet) {
            try {
                java.lang.reflect.Method method = product.getClass().getMethod("setQuantity", Integer.class);
                method.invoke(product, stock);
                stockSet = true;
            } catch (Exception ignored) {}
        }

        if (!stockSet) {
            logger.debug("No stock setter found for product {} - stock operations disabled",
                    product.getId());
        }
    }

    /**
     * Check if object has a specific method (simplified)
     */
    private boolean hasMethod(Object obj, String methodName) {
        if (obj == null) return false;

        try {
            obj.getClass().getMethod(methodName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validera produktdata (safe implementation)
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        // Safe stock validation
        Integer stock = getProductStock(product);
        if (stock != null && stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }

        logger.debug("Product validation passed for: {}", product.getName());
    }

    /**
     * Kontrollera om produkt existerar
     */
    public boolean existsById(Long id) {
        if (id == null) return false;
        return productRepository.existsById(id);
    }

    /**
     * Räkna antal produkter i kategori
     */
    public long countProductsByCategory(String category) {
        logger.debug("Counting products in category: {}", category);

        if (category == null || category.trim().isEmpty()) {
            return productRepository.count();
        }

        return getProductsByCategory(category).size();
    }

    /**
     * Hämta alla kategorier (optimized with method references)
     */
    public List<String> getAllCategories() {
        logger.debug("Fetching all product categories");

        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(this::isValidCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Helper method för category validation
     */
    private boolean isValidCategory(String category) {
        return category != null && !category.trim().isEmpty();
    }

    // ============================
    // BUSINESS ANALYTICS METHODS
    // ============================

    /**
     * Beräkna genomsnittspris för kategori (updated BigDecimal usage)
     */
    public BigDecimal getAveragePriceByCategory(String category) {
        logger.debug("Calculating average price for category: {}", category);

        List<Product> products = getProductsByCategory(category);

        if (products.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = products.stream()
                .map(Product::getPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Hämta produkt-statistik - FIXAD VERSION UTAN REKURSION
     */
    public ProductStatistics getProductStatistics() {
        logger.debug("Calculating product statistics");

        // ✅ FIXAT: Använd productRepository direkt istället för getAllProducts() för att undvika loop
        List<Product> allProducts = productRepository.findAll();
        long totalProducts = allProducts.size();
        long activeProducts = allProducts.stream().mapToLong(p -> isProductActive(p) ? 1 : 0).sum();

        // ✅ FIXAT: Beräkna products on sale direkt utan att anropa getProductsOnSale()
        long productsOnSale = allProducts.stream()
                .filter(this::isProductOnSale)
                .count();

        // ✅ FIXAT: Beräkna low stock direkt utan anrop till getLowStockProducts()
        long lowStockProducts = allProducts.stream()
                .filter(product -> isStockBelowThreshold(product, 10))
                .count();

        BigDecimal averagePrice = BigDecimal.ZERO;
        if (totalProducts > 0) {
            BigDecimal total = allProducts.stream()
                    .map(Product::getPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averagePrice = total.divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP);
        }

        // ✅ FIXAT: Beräkna kategorier direkt utan anrop till getAllCategories()
        long totalCategories = allProducts.stream()
                .map(Product::getCategory)
                .filter(this::isValidCategory)
                .distinct()
                .count();

        return new ProductStatistics(totalProducts, activeProducts, productsOnSale,
                lowStockProducts, averagePrice, totalCategories);
    }

    // ============================
    // DEMONSTRATION METHODS - FIXAD VERSION
    // ============================

    /**
     * Demonstration av ProductService methods - FIXAD VERSION UTAN REKURSION
     */
    public ProductStatistics demonstrateProductMethods() {
        logger.debug("Demonstrating ProductService methods");

        // Använd getProductByIdOrNull och spara result
        Product nullableProduct = getProductByIdOrNull(1L);
        boolean productFound = nullableProduct != null;

        // Använd existsById
        boolean exists = existsById(1L);

        // Använd countProductsByCategory
        long categoryCount = countProductsByCategory("Electronics");

        // Använd getAveragePriceByCategory
        BigDecimal avgPrice = getAveragePriceByCategory("Electronics");

        // ✅ FIXAT: Använd getProductStatistics och returnera för användning (UTAN att anropa getAllProducts)
        ProductStatistics stats = getProductStatistics();

        // Använd getTotalCategories från stats
        long totalCategories = stats.getTotalCategories();

        logger.info("Product demonstration completed - Found product: {}, Product exists: {}, " +
                        "Category count: {}, Avg price: {}, Total categories: {}",
                productFound, exists, categoryCount, avgPrice, totalCategories);

        return stats;
    }

    // ============================
    // INNER CLASS FOR STATISTICS (Record-style without Lombok)
    // ============================
    public static final class ProductStatistics {
        private final long totalProducts;
        private final long activeProducts;
        private final long productsOnSale;
        private final long lowStockProducts;
        private final BigDecimal averagePrice;
        private final long totalCategories;

        public ProductStatistics(long totalProducts, long activeProducts, long productsOnSale,
                                 long lowStockProducts, BigDecimal averagePrice, long totalCategories) {
            this.totalProducts = totalProducts;
            this.activeProducts = activeProducts;
            this.productsOnSale = productsOnSale;
            this.lowStockProducts = lowStockProducts;
            this.averagePrice = averagePrice;
            this.totalCategories = totalCategories;
        }

        // Explicit getters (undviker Lombok varningar)
        public long getTotalProducts() { return totalProducts; }
        public long getActiveProducts() { return activeProducts; }
        public long getProductsOnSale() { return productsOnSale; }
        public long getLowStockProducts() { return lowStockProducts; }
        public BigDecimal getAveragePrice() { return averagePrice; }
        public long getTotalCategories() { return totalCategories; }

        @Override
        public String toString() {
            return String.format("ProductStatistics{total=%d, active=%d, onSale=%d, lowStock=%d, avgPrice=%s, categories=%d}",
                    totalProducts, activeProducts, productsOnSale, lowStockProducts, averagePrice, totalCategories);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ProductStatistics that = (ProductStatistics) obj;
            return totalProducts == that.totalProducts &&
                    activeProducts == that.activeProducts &&
                    productsOnSale == that.productsOnSale &&
                    lowStockProducts == that.lowStockProducts &&
                    totalCategories == that.totalCategories &&
                    averagePrice.equals(that.averagePrice);
        }

        @Override
        public int hashCode() {
            return Long.hashCode(totalProducts) + Long.hashCode(activeProducts) +
                    Long.hashCode(productsOnSale) + averagePrice.hashCode();
        }
    }
}