package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Payment;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository för Railway-kompatibel databasaccess
 * ✅ UPPDATERAD: Använder model.Payment och förbättrade queries
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * ✅ FIXAT: Hitta alla betalningar för en order (använder Order relation)
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.processedAt DESC")
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
    @Query("SELECT p FROM Payment p WHERE p.processedAt BETWEEN :startDate AND :endDate ORDER BY p.processedAt DESC")
    List<Payment> findPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Summera belopp för specifik status och datum
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.processedAt >= :date")
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
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = :status ORDER BY p.processedAt DESC")
    List<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") PaymentStatus status);

    /**
     * Hitta alla återbetalningar för en order
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.type IN ('REFUND', 'PARTIAL_REFUND') ORDER BY p.processedAt DESC")
    List<Payment> findRefundsByOrderId(@Param("orderId") Long orderId);

    /**
     * Hitta lyckade betalningar för en användare
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.email = :email AND p.status = 'COMPLETED' ORDER BY p.processedAt DESC")
    List<Payment> findSuccessfulPaymentsByUser(@Param("email") String email);

    /**
     * Hitta betalningar som behöver verifiering
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.processedAt < :cutoffTime ORDER BY p.processedAt ASC")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Räkna totalt antal betalningar för en användare
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.user.email = :email")
    long countPaymentsByUser(@Param("email") String email);

    /**
     * Hitta högsta betalning för en användare
     */
    @Query("SELECT COALESCE(MAX(p.amount), 0) FROM Payment p WHERE p.order.user.email = :email AND p.status = 'COMPLETED'")
    BigDecimal findMaxPaymentAmountByUser(@Param("email") String email);

    /**
     * Hitta genomsnittligt betalningsbelopp
     */
    @Query("SELECT COALESCE(AVG(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt >= :since")
    BigDecimal findAveragePaymentAmountSince(@Param("since") LocalDateTime since);

    // ===============================
    // 🚀 RAILWAY-OPTIMERADE METODER
    // ===============================

    /**
     * Räkna betalningar med specifik status (för analytics)
     */
    long countByStatus(PaymentStatus status);

    /**
     * Hitta senaste betalningar för dashboard
     */
    @Query("SELECT p FROM Payment p ORDER BY p.processedAt DESC")
    List<Payment> findRecentPayments();

    /**
     * Beräkna total summa för genomförda betalningar
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);

    /**
     * Hitta misslyckade betalningar för en order
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = 'FAILED' ORDER BY p.processedAt DESC")
    List<Payment> findFailedPaymentsByOrderId(@Param("orderId") Long orderId);

    /**
     * Beräkna genomsnittligt betalningsbelopp för status
     */
    @Query("SELECT COALESCE(AVG(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal averageAmountByStatus(@Param("status") PaymentStatus status);

    /**
     * Hitta betalningar för specifik kund (via order)
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId ORDER BY p.processedAt DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);

    /**
     * Hitta dagens betalningar
     */
    @Query("SELECT p FROM Payment p WHERE DATE(p.processedAt) = DATE(CURRENT_DATE) ORDER BY p.processedAt DESC")
    List<Payment> findTodaysPayments();

    /**
     * Hitta betalningar denna vecka
     */
    @Query("SELECT p FROM Payment p WHERE p.processedAt >= :startOfWeek ORDER BY p.processedAt DESC")
    List<Payment> findThisWeeksPayments(@Param("startOfWeek") LocalDateTime startOfWeek);

    /**
     * Räkna lyckade betalningar för period
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt BETWEEN :start AND :end")
    long countSuccessfulPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Beräkna success rate för period
     */
    @Query("SELECT " +
            "CASE WHEN COUNT(p) = 0 THEN 0 " +
            "ELSE (COUNT(CASE WHEN p.status = 'COMPLETED' THEN 1 END) * 100.0) / COUNT(p) END " +
            "FROM Payment p WHERE p.processedAt BETWEEN :start AND :end")
    Double calculateSuccessRateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Hitta betalningar som behöver återbetalning
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND p.type = 'PAYMENT' " +
            "AND NOT EXISTS (SELECT r FROM Payment r WHERE r.order = p.order AND r.type IN ('REFUND', 'PARTIAL_REFUND'))")
    List<Payment> findRefundablePayments();

    /**
     * Hitta duplicerade betalningar (samma order, status COMPLETED)
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id IN " +
            "(SELECT p2.order.id FROM Payment p2 WHERE p2.status = 'COMPLETED' GROUP BY p2.order.id HAVING COUNT(p2) > 1) " +
            "AND p.status = 'COMPLETED' ORDER BY p.order.id, p.processedAt")
    List<Payment> findDuplicatePayments();

    /**
     * Hitta betalningar med höga belopp (för fraud detection)
     */
    @Query("SELECT p FROM Payment p WHERE p.amount > :threshold AND p.status IN ('COMPLETED', 'PENDING') ORDER BY p.amount DESC")
    List<Payment> findHighValuePayments(@Param("threshold") BigDecimal threshold);
}