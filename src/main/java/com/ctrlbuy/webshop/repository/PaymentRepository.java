package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Payment;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType; // ✅ FIXAT: Rätt import från enums paketet
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository för Payment entiteter
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * ✅ FIXAT: Hitta alla betalningar för en order (använder Order relation)
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    List<Payment> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Hitta betalningar med specifik status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Hitta betalning med transaktions-ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Hitta betalningar för en användare
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.email = :email ORDER BY p.processedAt DESC")
    List<Payment> findByUserEmail(@Param("email") String email);

    /**
     * Hitta betalningar inom ett datumintervall
     */
    @Query("SELECT p FROM Payment p WHERE p.processedAt BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Summera belopp för specifik status och datum
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.processedAt >= :date")
    BigDecimal sumAmountByStatusAndDate(@Param("status") PaymentStatus status,
                                        @Param("date") LocalDateTime date);

    /**
     * Räkna misslyckade betalningar sedan ett datum
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED' AND p.processedAt >= :date")
    long countFailedPaymentsSince(@Param("date") LocalDateTime date);

    /**
     * Hitta betalningar med specifik typ
     */
    List<Payment> findByType(PaymentType type);

    /**
     * ✅ FIXAT: Hitta betalningar för en order med specifik status (använder Order relation)
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = :status")
    List<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") PaymentStatus status);

    /**
     * Hitta alla återbetalningar för en order
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.type IN ('REFUND', 'PARTIAL_REFUND')")
    List<Payment> findRefundsByOrderId(@Param("orderId") Long orderId);

    /**
     * Hitta lyckade betalningar för en användare
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.email = :email AND p.status = 'COMPLETED' ORDER BY p.processedAt DESC")
    List<Payment> findSuccessfulPaymentsByUser(@Param("email") String email);

    /**
     * Hitta betalningar som behöver verifiering
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.processedAt < :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Räkna totalt antal betalningar för en användare
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.user.email = :email")
    long countPaymentsByUser(@Param("email") String email);

    /**
     * Hitta högsta betalning för en användare
     */
    @Query("SELECT MAX(p.amount) FROM Payment p WHERE p.order.user.email = :email AND p.status = 'COMPLETED'")
    BigDecimal findMaxPaymentAmountByUser(@Param("email") String email);

    /**
     * Hitta genomsnittligt betalningsbelopp
     */
    @Query("SELECT AVG(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt >= :since")
    BigDecimal findAveragePaymentAmountSince(@Param("since") LocalDateTime since);
}