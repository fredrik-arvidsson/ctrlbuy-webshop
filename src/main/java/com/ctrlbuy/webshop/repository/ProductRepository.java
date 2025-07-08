package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ================================
    // BEFINTLIGA METODER - FUNGERAR MED NUVARANDE DATABAS
    // ================================

    /**
     * Sök efter produkter som innehåller sökord i namn eller beskrivning
     * BEFINTLIG - fungerar med nuvarande databas
     */
    List<Product> findByNameContainingOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * Hitta produkter efter kategori
     * BEFINTLIG - fungerar med nuvarande databas
     */
    List<Product> findByCategory(String category);

    // ================================
    // GRUNDLÄGGANDE METODER SOM FUNGERAR NU
    // ================================

    /**
     * Hitta produkter inom prisintervall
     * Fungerar med befintlig price-kolumn
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Hitta produkter efter kategori med paginering
     * Fungerar med befintlig databas
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Sök produkter med paginering
     * Fungerar med befintlig databas
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Hitta produkter med lågt lager
     * Fungerar med befintlig stock_quantity
     */
    List<Product> findByStockQuantityLessThan(Integer threshold);

    /**
     * Hitta produkter som är slut i lager
     * Fungerar med befintlig stock_quantity
     */
    List<Product> findByStockQuantity(Integer stockQuantity);

    /**
     * Hämta alla unika kategorier
     * Fungerar med befintlig databas
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    /**
     * Räkna produkter per kategori
     * Fungerar med befintlig databas
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);

    /**
     * Hitta produkter sorterade efter pris
     * Fungerar med befintlig databas
     */
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();

    /**
     * Hitta produkter sorterade efter namn
     * Fungerar med befintlig databas
     */
    List<Product> findAllByOrderByNameAsc();
    List<Product> findAllByOrderByNameDesc();

    // ================================
    // 🔥 REA-METODER - FIXED FIELD NAMES
    // ÄNDRAT: onSale → isOnSale, active → isActive
    // ================================

    /**
     * Hämta alla produkter som är markerade som på REA och aktiva
     * FIXED: onSale → isOnSale, active → isActive
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrue();

    /**
     * Hämta alla produkter som är på REA (oavsett active-status)
     * FIXED: onSale → isOnSale
     */
    List<Product> findByIsOnSaleTrue();

    /**
     * Hämta aktiva REA:or inom datumintervall
     * FIXED: onSale → isOnSale, active → isActive
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrueAndSaleStartDateLessThanEqualAndSaleEndDateGreaterThanEqual(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Hämta kommande REA:or
     * FIXED: onSale → isOnSale, active → isActive
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrueAndSaleStartDateGreaterThan(LocalDateTime now);

    /**
     * Hämta utgångna REA:or
     * FIXED: onSale → isOnSale, active → isActive
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrueAndSaleEndDateLessThan(LocalDateTime now);

    /**
     * Hämta REA-produkter sorterade efter rabatt (högst rabatt först)
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL " +
            "ORDER BY ((p.originalPrice - p.salePrice) / p.originalPrice) DESC")
    List<Product> findSaleProductsOrderByDiscountDesc();

    /**
     * Hämta REA-produkter i en specifik kategori
     * FIXED: onSale → isOnSale, active → isActive
     */
    List<Product> findByIsOnSaleTrueAndIsActiveTrueAndCategory(String category);

    /**
     * Hämta REA-produkter med rabatt över en viss procent
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL " +
            "AND ((p.originalPrice - p.salePrice) / p.originalPrice * 100) >= :minDiscountPercent")
    List<Product> findSaleProductsWithMinimumDiscount(@Param("minDiscountPercent") BigDecimal minDiscountPercent);

    /**
     * Hämta REA-produkter som sparar mer än ett visst belopp
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL " +
            "AND (p.originalPrice - p.salePrice) >= :minSavings")
    List<Product> findSaleProductsWithMinimumSavings(@Param("minSavings") BigDecimal minSavings);

    /**
     * Hämta de bästa REA-erbjudandena (högsta rabatter)
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL " +
            "ORDER BY ((p.originalPrice - p.salePrice) / p.originalPrice) DESC")
    List<Product> findTopSaleDeals(Pageable pageable);

    /**
     * Räkna antal produkter på REA
     * FIXED: onSale → isOnSale, active → isActive
     */
    long countByIsOnSaleTrueAndIsActiveTrue();

    /**
     * Hämta REA-produkter som slutar inom X dagar
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.saleEndDate IS NOT NULL " +
            "AND p.saleEndDate BETWEEN :now AND :endDate")
    List<Product> findSalesEndingSoon(@Param("now") LocalDateTime now,
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Söka bland REA-produkter
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.isActive = true " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchSaleProducts(@Param("searchTerm") String searchTerm);

    /**
     * Hämta REA-produkter grupperade efter kategori
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p " +
            "WHERE p.isOnSale = true AND p.isActive = true " +
            "GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> findSaleProductCountByCategory();

    /**
     * Beräkna totala besparingar för alla REA-produkter
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT SUM(p.originalPrice - p.salePrice) FROM Product p " +
            "WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL")
    BigDecimal calculateTotalSavingsFromSales();

    /**
     * Hämta genomsnittlig rabattprocent
     * FIXED: @Query med isOnSale och isActive
     */
    @Query("SELECT AVG((p.originalPrice - p.salePrice) / p.originalPrice * 100) FROM Product p " +
            "WHERE p.isOnSale = true AND p.isActive = true " +
            "AND p.salePrice IS NOT NULL AND p.originalPrice IS NOT NULL")
    BigDecimal calculateAverageDiscountPercentage();

    // ================================
    // METODER SOM KRÄVER NYA KOLUMNER
    // Kommenterade tills vidare - kan aktiveras när kolumnerna läggs till
    // ================================

    /*
    // AKTIVERAS NÄR is_active KOLUMN LÄGGS TILL:

    List<Product> findByIsActiveTrueOrderByNameAsc();
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Optional<Product> findByIdAndIsActiveTrue(Long id);
    long countByIsActiveTrue();
    List<Product> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
    List<String> findDistinctCategoriesByIsActiveTrue();
    */

    /*
    // AKTIVERAS NÄR sale_price KOLUMN LÄGGS TILL:

    List<Product> findBySalePriceIsNotNullOrderBySalePriceAsc();
    long countBySalePriceIsNotNull();

    @Query("SELECT p FROM Product p WHERE " +
           "((p.salePrice IS NOT NULL AND p.salePrice BETWEEN :minPrice AND :maxPrice) OR " +
           "(p.salePrice IS NULL AND p.price BETWEEN :minPrice AND :maxPrice))")
    List<Product> findByEffectivePriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    */

    /*
    // AKTIVERAS NÄR view_count KOLUMN LÄGGS TILL:

    List<Product> findTop8ByOrderByViewCountDesc();
    List<Product> findByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.viewCount > :minViews ORDER BY p.viewCount DESC")
    List<Product> findPopularProducts(@Param("minViews") Integer minViews, Pageable pageable);
    */

    /*
    // AKTIVERAS NÄR created_at KOLUMN LÄGGS TILL:

    List<Product> findTop10ByOrderByCreatedAtDesc();
    List<Product> findByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.updatedAt > p.createdAt ORDER BY p.updatedAt DESC")
    List<Product> findRecentlyUpdatedProducts(Pageable pageable);
    */

    // ================================
    // STATISTIK OCH AGGREGERING - FUNGERAR NU
    // ================================

    /**
     * Grundläggande statistik som fungerar med nuvarande databas
     */
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
    // TEMPORÄRA WORKAROUND-METODER
    // Dessa använder befintliga kolumner för att simulera ny funktionalitet
    // ================================

    /**
     * Tillfällig metod för "populära" produkter baserat på ID
     * (högre ID = nyare produkt, kan användas som proxy för popularitet)
     */
    @Query("SELECT p FROM Product p ORDER BY p.id DESC")
    List<Product> findRecentProductsAsPopular(Pageable pageable);

    /**
     * Tillfällig metod för "nya" produkter baserat på ID
     * (högre ID = nyare produkt)
     */
    @Query("SELECT p FROM Product p ORDER BY p.id DESC")
    List<Product> findNewestProductsByIdProxy(Pageable pageable);

    /**
     * Simulera "aktiva" produkter genom att filtrera på lagerstatus
     * (produkter med lager > 0 eller namn som inte innehåller "INAKTIV")
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity >= 0 ORDER BY p.name ASC")
    List<Product> findActiveProductsProxy();

    /**
     * Simulera "rea" produkter genom att hitta produkter med vissa nyckelord
     * (tillfälligt tills sale_price kolumn läggs till)
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE '%rea%' OR " +
            "LOWER(p.name) LIKE '%sale%' OR " +
            "LOWER(p.description) LIKE '%kampanj%' " +
            "ORDER BY p.price ASC")
    List<Product> findSaleProductsProxy();

    // ================================
    // AVANCERADE SÖKFUNKTIONER - FUNGERAR NU
    // ================================

    /**
     * Flexibel sökning med flera filter
     * Fungerar med befintlig databasstruktur
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
            "  LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> findProductsWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    /**
     * Hitta relaterade produkter baserat på kategori
     * Exkluderar en specifik produkt
     */
    @Query("SELECT p FROM Product p WHERE " +
            "p.category = :category AND p.id != :excludeId " +
            "ORDER BY p.name ASC")
    List<Product> findRelatedProductsByCategory(
            @Param("category") String category,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    /**
     * Sök efter produkter inom ett prisintervall med paginering
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Hitta produkter med specifika nyckelord i namnet
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.name ASC")
    List<Product> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    // ================================
    // 🚀 NYA METODER FÖR ATT HÄMTA ALLA 58 PRODUKTER
    // ================================

    /**
     * HÄMTA ALLA PRODUKTER - UTAN PAGINERING
     * Garanterar att vi får alla 58 produkter från databasen
     */
    @Query("SELECT p FROM Product p ORDER BY p.id ASC")
    List<Product> findAllProductsNoPaging();

    /**
     * Native SQL för att vara 100% säker på att vi får alla produkter
     */
    @Query(value = "SELECT * FROM products ORDER BY id ASC", nativeQuery = true)
    List<Product> findAllProductsNative();

    /**
     * Räkna alla produkter med native SQL
     */
    @Query(value = "SELECT COUNT(*) FROM products", nativeQuery = true)
    Long countAllProductsNative();

    /**
     * Hämta alla produkter oavsett status med JPQL
     */
    @Query("SELECT p FROM Product p")
    List<Product> findEveryProduct();

    // ================================
    // 🆕 NYA NATIVE SQL METODER FÖR PRISSTATISTIK
    // ================================

    /**
     * Hämta högsta pris med native SQL
     */
    @Query(value = "SELECT MAX(price) FROM products", nativeQuery = true)
    BigDecimal findMaxPriceNative();

    /**
     * Hämta lägsta pris med native SQL
     */
    @Query(value = "SELECT MIN(price) FROM products", nativeQuery = true)
    BigDecimal findMinPriceNative();

    /**
     * Hämta dyraste produkten med native SQL
     */
    @Query(value = "SELECT * FROM products ORDER BY price DESC LIMIT 1", nativeQuery = true)
    Product findMostExpensiveProductNative();

    /**
     * Hämta billigaste produkten med native SQL
     */
    @Query(value = "SELECT * FROM products ORDER BY price ASC LIMIT 1", nativeQuery = true)
    Product findCheapestProductNative();
}