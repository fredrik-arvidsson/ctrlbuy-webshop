package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // BEFINTLIGA METODER (oförändrade)
    Order findByOrderNumberAndUser(String orderNumber, User user);
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findAllByOrderByOrderDateDesc();
    Long countByUser(User user);
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    List<Order> findByUserAndStatusOrderByOrderDateDesc(User user, Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:search% ORDER BY o.orderDate DESC")
    List<Order> searchByOrderNumber(@Param("search") String search);

    Order findByOrderNumber(String orderNumber);

    // NYA METODER FÖR ORDERHISTORIK
    /**
     * Paginering för orderhistorik
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * Säker hämtning av order (kontrollerar ägarskap)
     */
    Optional<Order> findByIdAndUser(Long id, User user);

    /**
     * Beräkna total summa för användares alla orders
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user")
    Double sumTotalAmountByUser(@Param("user") User user);
}