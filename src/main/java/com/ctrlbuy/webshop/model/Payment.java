package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity för databas-lagring
 */
@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "card_type", length = 20)
    private String cardType;

    @Column(name = "masked_card_number", length = 20)
    private String maskedCardNumber;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    @Builder.Default
    private PaymentType type = PaymentType.CREDIT_CARD;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "gateway_response", length = 1000)
    private String gatewayResponse;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Column(name = "authorization_code", length = 50)
    private String authorizationCode;

    @Column(name = "merchant_reference", length = 100)
    private String merchantReference;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Convenience methods för PaymentInfo compatibility
    public Long getPaymentId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.order != null ? this.order.getId() : null;
    }

    // Business methods
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isRefund() {
        return type == PaymentType.REFUND;
    }

    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED && type != PaymentType.REFUND;
    }

    // Update timestamp when status changes
    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", status=" + status +
                ", type=" + type +
                ", transactionId='" + transactionId + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}