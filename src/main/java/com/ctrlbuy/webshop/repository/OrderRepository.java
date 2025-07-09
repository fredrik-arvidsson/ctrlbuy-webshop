package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ============================
    // GRUNDLÄGGANDE METODER - UPPDATERADE FÖR ORDERSERVICE COMPATIBILITY
    // ============================

    /**
     * 🔧 CRITICAL: För AdminController och UserController compatibility
     */
    Order findByOrderNumberAndUser(String orderNumber, User user);
    Order findByOrderNumber(String orderNumber);
    List<Order> findByUserOrderByOrderDateDesc(User user);

    /**
     * 🔧 CRITICAL: Denna metod behöver matcha exakt vad OrderService förväntar sig
     */
    List<Order> findAllByOrderByOrderDateDesc();

    /**
     * 🔧 CRITICAL: För UserController compatibility - userId via user object
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 🔧 CRITICAL: För search functionality i UserController
     */
    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:orderNumber% AND o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findByOrderNumberContainingAndUserIdOrderByCreatedAtDesc(
            @Param("orderNumber") String orderNumber,
            @Param("userId") Long userId
    );

    Long countByUser(User user);
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    List<Order> findByUserAndStatusOrderByOrderDateDesc(User user, Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:search% ORDER BY o.orderDate DESC")
    List<Order> searchByOrderNumber(@Param("search") String search);

    // ============================
    // ORDERNUMMER-GENERERING
    // ============================

    /**
     * Hitta alla ordernummer som matchar ett pattern (för dagens ordrar)
     * Används för att generera unika ordernummer baserat på datum
     */
    @Query("SELECT o.orderNumber FROM Order o WHERE o.orderNumber LIKE :pattern ORDER BY o.orderNumber DESC")
    List<String> findOrderNumbersByPattern(@Param("pattern") String pattern);

    // ============================
    // PAGINERING
    // ============================

    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);
    Page<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status, Pageable pageable);

    // ============================
    // SÄKER HÄMTNING
    // ============================

    Optional<Order> findByIdAndUser(Long id, User user);

    // ============================
    // BERÄKNINGAR - FIXADE FÖR BIGDECIMAL COMPATIBILITY
    // ============================

    /**
     * 🔧 CRITICAL: Returnerar BigDecimal för Order entity compatibility
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user = :user")
    BigDecimal sumTotalAmountByUser(@Param("user") User user);

    /**
     * 🔧 CRITICAL: Double version för backward compatibility
     */
    @Query("SELECT COALESCE(SUM(CAST(o.totalAmount AS double)), 0.0) FROM Order o WHERE o.user = :user")
    Double sumTotalAmountByUserAsDouble(@Param("user") User user);

    /**
     * 🔧 CRITICAL: För UserController getTotalSpentByUser compatibility
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId")
    BigDecimal getTotalSpentByUserId(@Param("userId") Long userId);

    // ============================
    // EAGER LOADING METODER
    // ============================

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId AND o.user = :user")
    Optional<Order> findByIdAndUserWithItems(@Param("orderId") Long orderId, @Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItems(@Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderNumber = :orderNumber AND o.user = :user")
    Optional<Order> findByOrderNumberAndUserWithItems(@Param("orderNumber") String orderNumber, @Param("user") User user);

    // ============================
    // ADMIN METODER
    // ============================

    long countByStatus(Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    Page<Order> findByOrderDateBetweenOrderByOrderDateDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Page<Order> findByUserAndStatusOrderByOrderDateDesc(
            User user,
            Order.OrderStatus status,
            Pageable pageable
    );

    /**
     * UPPDATERAD SEARCH MED KORREKT FÄLTNAMN
     */
    @Query("SELECT o FROM Order o WHERE o.user.email LIKE %:keyword% OR o.user.username LIKE %:keyword% ORDER BY o.orderDate DESC")
    Page<Order> findByCustomerKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount > :amount ORDER BY o.orderDate DESC")
    Page<Order> findByTotalAmountGreaterThanOrderByOrderDateDesc(
            @Param("amount") BigDecimal amount,
            Pageable pageable
    );

    List<Order> findTop5ByUserOrderByOrderDateDesc(User user);

    @Query("SELECT o FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE ORDER BY o.orderDate DESC")
    List<Order> findTodaysOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    long countTodaysOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    BigDecimal calculateTotalSalesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE AND o.status != 'CANCELLED'")
    BigDecimal calculateTodaysSales();

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.orderDate < :cutoffDate ORDER BY o.orderDate ASC")
    List<Order> findPendingOrdersOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();

    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:search% ORDER BY o.orderDate DESC")
    Page<Order> searchByOrderNumber(@Param("search") String search, Pageable pageable);

    // ============================
    // EXTRA METODER FÖR TOTAL REVENUE CALCULATIONS
    // ============================

    /**
     * Get total revenue (all orders)
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal getTotalRevenue();

    /**
     * Double version för backward compatibility
     */
    @Query("SELECT COALESCE(SUM(CAST(o.totalAmount AS double)), 0.0) FROM Order o")
    Double getTotalRevenueAsDouble();

    /**
     * Count orders by user ID
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Find recent orders (last N orders)
     */
    List<Order> findTop10ByOrderByOrderDateDesc();

    /**
     * Search orders by order number containing text (for admin)
     */
    List<Order> findByOrderNumberContainingOrderByOrderDateDesc(String orderNumber);
}