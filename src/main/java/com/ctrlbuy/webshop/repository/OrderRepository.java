package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Order;
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

    // GRUNDLÄGGANDE METODER
    Order findByOrderNumberAndUser(String orderNumber, User user);
    Order findByOrderNumber(String orderNumber);
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findAllByOrderByOrderDateDesc();
    Long countByUser(User user);
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    List<Order> findByUserAndStatusOrderByOrderDateDesc(User user, Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:search% ORDER BY o.orderDate DESC")
    List<Order> searchByOrderNumber(@Param("search") String search);

    // PAGINERING
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);
    Page<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status, Pageable pageable);

    // SÄKER HÄMTNING
    Optional<Order> findByIdAndUser(Long id, User user);

    // BERÄKNINGAR
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user")
    Double sumTotalAmountByUser(@Param("user") User user);

    // EAGER LOADING METODER
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

    // ADMIN METODER
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

    // UPPDATERAD SEARCH MED KORREKT FÄLTNAMN
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
}
