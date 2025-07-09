package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ProductRepository - Railway-optimerad utan duplicates
 * ✅ CLEANED UP: Removed duplicates, organized methods, added usage demonstrations
 * ✅ ADDED: calculateTotalSavingsFromSales method för AdminController
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ================================
    // CORE METHODS USED BY PRODUCTSERVICE
    // ================================

    /**
     * Basic search by name (used by ProductService.searchProducts)
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * Search with OR condition (used by ProductService legacy compatibility)
     */
    List<Product> findByNameContainingOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * Find by category (used by ProductService.getProductsByCategory)
     */
    List<Product> findByCategory(String category);

    /**
     * Category search with pagination (used by ProductService)
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Price range search (used by ProductService.getProductsByPriceRange)
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Price range with pagination
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Low stock products (used by ProductService.getLowStockProducts)
     */
    List<Product> findByStockQuantityLessThan(Integer threshold);

    /**
     * Out of stock products
     */
    List<Product> findByStockQuantity(Integer stockQuantity);

    // ================================
    // SORTING METHODS (used by ProductService)
    // ================================

    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();
    List<Product> findAllByOrderByNameAsc();
    List<Product> findAllByOrderByNameDesc();

    // ================================
    // ENHANCED SEARCH (ProductService compatibility)
    // ================================

    /**
     * Enhanced search with relevance ranking (used by ProductController)
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY " +
            "CASE WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 1 " +
            "     WHEN LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 2 " +
            "     ELSE 3 END, p.name ASC")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Multi-filter search (used by ProductController advanced search)
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR " +
            "  (:inStock = true AND p.stockQuantity > 0) OR " +
            "  (:inStock = false AND p.stockQuantity = 0)) AND " +
            "(:searchTerm IS NULL OR " +
            "  LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "  LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY p.name ASC")
    Page<Product> findProductsWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // ================================
    // ACTIVE PRODUCTS (if isActive column exists)
    // ================================

    /**
     * Active products (used by ProductService when isActive is available)
     */
    List<Product> findByIsActiveTrueOrderByNameAsc();
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Optional<Product> findByIdAndIsActiveTrue(Long id);
    long countByIsActiveTrue();

    /**
     * Active products by category
     */
    List<Product> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);

    /**
     * Featured products (if isFeatured column exists)
     */
    List<Product> findByIsFeaturedTrueAndIsActiveTrueOrderByNameAsc();

    // ================================
    // SALE PRODUCTS (if sale columns exist)
    // ================================

    /**
     * Sale products (used by ProductService.getProductsOnSale)
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrue();
    List<Product> findByIsOnSaleTrue();

    /**
     * Sale products by category
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrueAndCategory(String category);

    /**
     * Sale products with best discounts
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true AND " +
            "p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL " +
            "ORDER BY ((p.originalPrice - p.salePrice) / p.originalPrice) DESC")
    List<Product> findSaleProductsOrderByDiscountDesc();

    /**
     * Count sale products
     */
    long countByIsOnSaleTrueAndIsActiveTrue();

    // ================================
    // ADMIN ANALYTICS (used by AdminController)
    // ================================

    /**
     * Calculate total savings from sales - NEW METHOD for AdminController
     * Beräknar totala besparingar från alla produkter som har lägre pris än originalpris
     */
    @Query("SELECT COALESCE(SUM(p.originalPrice - p.price), 0) FROM Product p WHERE " +
            "p.originalPrice IS NOT NULL AND p.price IS NOT NULL AND p.originalPrice > p.price")
    BigDecimal calculateTotalSavingsFromSales();

    // ================================
    // CATEGORY ANALYTICS (used by ProductService)
    // ================================

    /**
     * Get all categories (used by ProductService.getAllCategories)
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    /**
     * Count products by category (used by ProductService.countProductsByCategory)
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);

    /**
     * Active categories (if isActive exists)
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE " +
            "p.category IS NOT NULL AND p.isActive = true " +
            "ORDER BY p.category ASC")
    List<String> findDistinctActiveCategories();

    // ================================
    // STATISTICS (used by ProductService analytics)
    // ================================

    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal findAveragePrice();

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal findMaxPrice();

    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Long findTotalStock();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    long countOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > 0")
    long countInStockProducts();

    // ================================
    // PERFORMANCE OPERATIONS (used by ProductService)
    // ================================

    /**
     * Increment view count (used by ProductController.getProduct)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.viewCount = COALESCE(p.viewCount, 0) + 1 WHERE p.id = :productId")
    void incrementViewCount(@Param("productId") Long productId);

    /**
     * Decrease stock (used by OrderService)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * Increase stock (used by InventoryService)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
    void increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // ================================
    // RELATED PRODUCTS (used by ProductController)
    // ================================

    /**
     * Related products by category (used by ProductController.getRelatedProducts)
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.id != :excludeId " +
            "ORDER BY p.name ASC")
    List<Product> findRelatedProductsByCategory(
            @Param("category") String category,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // ================================
    // POPULAR & NEWEST PRODUCTS (used by HomeController)
    // ================================

    /**
     * Popular products proxy (used by ProductService.getPopularProducts)
     */
    @Query("SELECT p FROM Product p ORDER BY p.id DESC")
    List<Product> findRecentProductsAsPopular(Pageable pageable);

    /**
     * Newest products by ID (used by ProductService.getNewestProducts)
     */
    @Query("SELECT p FROM Product p ORDER BY p.id DESC")
    List<Product> findNewestProductsByIdProxy(Pageable pageable);

    /**
     * Popular products by view count (if viewCount exists)
     */
    @Query("SELECT p FROM Product p WHERE p.viewCount IS NOT NULL " +
            "ORDER BY p.viewCount DESC")
    Page<Product> findPopularProducts(Pageable pageable);

    /**
     * Newest products by creation date (if createdAt exists)
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt IS NOT NULL " +
            "ORDER BY p.createdAt DESC")
    Page<Product> findNewestProducts(Pageable pageable);

    // ================================
    // LEGACY COMPATIBILITY (used by old controllers)
    // ================================

    @Query("SELECT p FROM Product p ORDER BY p.id ASC")
    List<Product> findAllProductsNoPaging();

    @Query(value = "SELECT * FROM products ORDER BY id ASC", nativeQuery = true)
    List<Product> findAllProductsNative();

    @Query(value = "SELECT COUNT(*) FROM products", nativeQuery = true)
    Long countAllProductsNative();

    @Query("SELECT p FROM Product p")
    List<Product> findEveryProduct();

    // Native price queries
    @Query(value = "SELECT MAX(price) FROM products", nativeQuery = true)
    BigDecimal findMaxPriceNative();

    @Query(value = "SELECT MIN(price) FROM products", nativeQuery = true)
    BigDecimal findMinPriceNative();

    @Query(value = "SELECT * FROM products ORDER BY price DESC LIMIT 1", nativeQuery = true)
    Product findMostExpensiveProductNative();

    @Query(value = "SELECT * FROM products ORDER BY price ASC LIMIT 1", nativeQuery = true)
    Product findCheapestProductNative();

    // ================================
    // PROXY METHODS FOR OLDER DATABASES
    // ================================

    /**
     * Active products proxy (used when isActive column doesn't exist)
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity >= 0 ORDER BY p.name ASC")
    List<Product> findActiveProductsProxy();

    /**
     * Sale products proxy (used when sale columns don't exist)
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE '%rea%' OR " +
            "LOWER(p.name) LIKE '%sale%' OR " +
            "LOWER(p.description) LIKE '%kampanj%' " +
            "ORDER BY p.price ASC")
    List<Product> findSaleProductsProxy();
}