package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity för databas-lagring
 * ✅ RAILWAY-OPTIMERAD: JPA Entity med förbättrade relationships och business logic
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_order", columnList = "order_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_type", columnList = "type"),
        @Index(name = "idx_payment_transaction", columnList = "transaction_id"),
        @Index(name = "idx_payment_gateway_transaction", columnList = "gateway_transaction_id"),
        @Index(name = "idx_payment_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    // ============================
    // CORE PAYMENT FIELDS
    // ============================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_order"))
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private PaymentType type = PaymentType.CREDIT_CARD;

    // ============================
    // TRANSACTION IDENTIFIERS
    // ============================
    @Column(name = "transaction_id", length = 100, unique = true)
    private String transactionId;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Column(name = "merchant_reference", length = 100)
    private String merchantReference;

    @Column(name = "authorization_code", length = 50)
    private String authorizationCode;

    // ============================
    // CARD INFORMATION
    // ============================
    @Column(name = "card_type", length = 20)
    private String cardType;

    @Column(name = "masked_card_number", length = 20)
    private String maskedCardNumber;

    @Column(name = "card_last_four_digits", length = 4)
    private String cardLastFourDigits;

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    // ============================
    // GATEWAY & PROCESSING
    // ============================
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway = "STRIPE";

    @Column(name = "gateway_response", length = 2000)
    private String gatewayResponse;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    // ============================
    // FINANCIAL FIELDS
    // ============================
    @Column(name = "currency", length = 3)
    private String currency = "SEK";

    @Column(name = "fee_amount", precision = 10)
    private BigDecimal feeAmount;

    @Column(name = "refund_amount", precision = 10)
    private BigDecimal refundAmount;

    @Column(name = "tax_amount", precision = 10)
    private BigDecimal taxAmount;

    // ============================
    // TIMESTAMPS
    // ============================
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // ============================
    // AUDIT & SECURITY FIELDS
    // ============================
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_fingerprint", length = 100)
    private String deviceFingerprint;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "fraud_check_result", length = 50)
    private String fraudCheckResult;

    // ============================
    // LIFECYCLE CALLBACKS
    // ============================
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-set completion timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        if (status == PaymentStatus.COMPLETED && completedAt == null) {
            completedAt = now;
        }
        if (status == PaymentStatus.FAILED && failedAt == null) {
            failedAt = now;
        }
        if ((status == PaymentStatus.REFUNDED || status == PaymentStatus.PARTIALLY_REFUNDED)
                && refundedAt == null) {
            refundedAt = now;
        }
    }

    // ============================
    // CONVENIENCE GETTER METHODS (för PaymentInfo compatibility)
    // ============================

    /**
     * Alias för id (används av PaymentInfo)
     */
    public Long getPaymentId() {
        return this.id;
    }

    /**
     * Get order ID direkt eller från order relationship
     */
    public Long getOrderId() {
        return this.order != null ? this.order.getId() : null;
    }

    /**
     * Get error code (required by PaymentInfo)
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get error message (required by PaymentInfo)
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get currency (required by PaymentInfo)
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Get fee amount (required by PaymentInfo)
     */
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    /**
     * Get gateway response (required by PaymentInfo)
     */
    public String getGatewayResponse() {
        return gatewayResponse;
    }

    // ============================
    // BUSINESS LOGIC METHODS
    // ============================

    /**
     * Kontrollera om betalningen är lyckad
     */
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Kontrollera om betalningen är misslyckad
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED;
    }

    /**
     * Kontrollera om betalningen pågår
     */
    public boolean isInProgress() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.PROCESSING;
    }

    /**
     * Kontrollera om det är en återbetalning
     */
    public boolean isRefund() {
        return type == PaymentType.REFUND ||
                status == PaymentStatus.REFUNDED ||
                status == PaymentStatus.PARTIALLY_REFUNDED;
    }

    /**
     * Kontrollera om betalningen kan återbetalas
     */
    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED &&
                type != PaymentType.REFUND &&
                (refundAmount == null || refundAmount.compareTo(amount) < 0);
    }

    /**
     * Kontrollera om betalningen är högrisk
     */
    public boolean isHighRisk() {
        return riskScore != null && riskScore > 75;
    }

    /**
     * Hämta total kostnad inklusive avgifter
     */
    public BigDecimal getTotalCost() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        if (feeAmount != null) {
            total = total.add(feeAmount);
        }
        if (taxAmount != null) {
            total = total.add(taxAmount);
        }
        return total;
    }

    /**
     * Hämta netto belopp efter återbetalningar
     */
    public BigDecimal getNetAmount() {
        BigDecimal net = amount != null ? amount : BigDecimal.ZERO;
        if (refundAmount != null) {
            net = net.subtract(refundAmount);
        }
        return net;
    }

    /**
     * Hämta återstående belopp som kan återbetalas
     */
    public BigDecimal getRefundableAmount() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        BigDecimal refunded = refundAmount != null ? refundAmount : BigDecimal.ZERO;
        return total.subtract(refunded);
    }

    /**
     * Hämta maskerat kortnummer
     */
    public String getMaskedCardNumber() {
        if (maskedCardNumber != null) {
            return maskedCardNumber;
        }
        if (cardLastFourDigits != null) {
            return "**** **** **** " + cardLastFourDigits;
        }
        return "****";
    }

    /**
     * Beräkna behandlingstid i minuter
     */
    public Long getProcessingTimeMinutes() {
        if (createdAt == null || processedAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, processedAt).toMinutes();
    }

    /**
     * Kontrollera om betalningen är gammal (över 30 minuter utan processing)
     */
    public boolean isStale() {
        if (createdAt == null || !isInProgress()) {
            return false;
        }
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    // ============================
    // BUSINESS OPERATIONS
    // ============================

    /**
     * Markera som behandlad
     */
    public Payment markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * Markera som genomförd
     */
    public Payment markAsCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * Markera som misslyckad
     */
    public Payment markAsFailed(String errorCode, String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.failedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * Lägg till återbetalning
     */
    public Payment addRefund(BigDecimal refundAmount) {
        if (this.refundAmount == null) {
            this.refundAmount = BigDecimal.ZERO;
        }
        this.refundAmount = this.refundAmount.add(refundAmount);

        // Uppdatera status baserat på återbetalningsbelopp
        if (this.refundAmount.compareTo(this.amount) >= 0) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }

        this.refundedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * Sätt riskbedömning
     */
    public Payment setRiskAssessment(Integer riskScore, String fraudResult) {
        this.riskScore = riskScore;
        this.fraudCheckResult = fraudResult;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    // ============================
    // CUSTOM SETTER WITH BUSINESS LOGIC
    // ============================

    /**
     * Update timestamp when status changes
     */
    public void setStatus(PaymentStatus newStatus) {
        PaymentStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        // Auto-set timestamps based on status transitions
        LocalDateTime now = LocalDateTime.now();
        if (newStatus == PaymentStatus.PROCESSING && processedAt == null) {
            processedAt = now;
        }
        if (newStatus == PaymentStatus.COMPLETED && completedAt == null) {
            completedAt = now;
        }
        if (newStatus == PaymentStatus.FAILED && failedAt == null) {
            failedAt = now;
        }
        if ((newStatus == PaymentStatus.REFUNDED || newStatus == PaymentStatus.PARTIALLY_REFUNDED)
                && refundedAt == null) {
            refundedAt = now;
        }
    }

    // ============================
    // PAYMENTSERVICE COMPATIBILITY METHODS
    // ============================

    /**
     * Set payment date (alias för processedAt) - CRITICAL för PaymentService
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.processedAt = paymentDate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get payment date (alias för processedAt)
     */
    public LocalDateTime getPaymentDate() {
        return this.processedAt;
    }

    /**
     * Set transaction ID
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get transaction ID
     */
    public String getTransactionId() {
        return this.transactionId;
    }

    /**
     * Set order and sync relationship (för PaymentService)
     */
    public void setOrder(Order order) {
        this.order = order;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get order
     */
    public Order getOrder() {
        return this.order;
    }

    /**
     * Set card last four digits
     */
    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get card last four digits
     */
    public String getCardLastFourDigits() {
        return this.cardLastFourDigits;
    }

    /**
     * Set completed at timestamp
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get completed at timestamp
     */
    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    /**
     * Set failure reason
     */
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get failure reason
     */
    public String getFailureReason() {
        return this.failureReason;
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, orderId=%d, amount=%s, status=%s, type=%s, transactionId='%s', processedAt=%s}",
                id, getOrderId(), amount, status, type, transactionId, processedAt);
    }

    // ============================
    // EQUALS & HASHCODE (baserat på transaction ID för business logic)
    // ============================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return id != null && id.equals(payment.id) ||
                (transactionId != null && transactionId.equals(payment.transactionId));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() :
                (transactionId != null ? transactionId.hashCode() : 0);
    }

    // ============================
    // CUSTOM BUILDER FOR PAYMENTINFO COMPATIBILITY
    // ============================

    /**
     * Static builder method
     */
    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    /**
     * Custom PaymentBuilder som stöder både paymentId() och id() methods
     */
    public static class PaymentBuilder {
        private Long id;
        private Order order;
        private BigDecimal amount;
        private PaymentStatus status;
        private PaymentType type = PaymentType.CREDIT_CARD;
        private String transactionId;
        private String gatewayTransactionId;
        private String merchantReference;
        private String authorizationCode;
        private String cardType;
        private String maskedCardNumber;
        private String cardLastFourDigits;
        private String cardHolderName;
        private String paymentGateway = "STRIPE";
        private String gatewayResponse;
        private String errorCode;
        private String errorMessage;
        private String failureReason;
        private String currency = "SEK";
        private BigDecimal feeAmount;
        private BigDecimal refundAmount;
        private BigDecimal taxAmount;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        private LocalDateTime processedAt;
        private LocalDateTime completedAt;
        private LocalDateTime failedAt;
        private LocalDateTime refundedAt;
        private String ipAddress;
        private String userAgent;
        private String deviceFingerprint;
        private Integer riskScore;
        private String fraudCheckResult;

        /**
         * CRITICAL: paymentId method för PaymentInfo.toPaymentEntity() compatibility
         */
        public PaymentBuilder paymentId(Long paymentId) {
            this.id = paymentId;
            return this;
        }

        public PaymentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public PaymentBuilder orderId(Long orderId) {
            // Set orderId by creating a minimal Order object
            if (orderId != null) {
                Order tempOrder = new Order();
                tempOrder.setId(orderId);
                this.order = tempOrder;
            }
            return this;
        }

        public PaymentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public PaymentBuilder type(PaymentType type) {
            this.type = type;
            return this;
        }

        public PaymentBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public PaymentBuilder gatewayTransactionId(String gatewayTransactionId) {
            this.gatewayTransactionId = gatewayTransactionId;
            return this;
        }

        public PaymentBuilder merchantReference(String merchantReference) {
            this.merchantReference = merchantReference;
            return this;
        }

        public PaymentBuilder authorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
            return this;
        }

        public PaymentBuilder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public PaymentBuilder maskedCardNumber(String maskedCardNumber) {
            this.maskedCardNumber = maskedCardNumber;
            return this;
        }

        public PaymentBuilder cardLastFourDigits(String cardLastFourDigits) {
            this.cardLastFourDigits = cardLastFourDigits;
            return this;
        }

        public PaymentBuilder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public PaymentBuilder paymentGateway(String paymentGateway) {
            this.paymentGateway = paymentGateway;
            return this;
        }

        public PaymentBuilder gatewayResponse(String gatewayResponse) {
            this.gatewayResponse = gatewayResponse;
            return this;
        }

        public PaymentBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public PaymentBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public PaymentBuilder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public PaymentBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public PaymentBuilder feeAmount(BigDecimal feeAmount) {
            this.feeAmount = feeAmount;
            return this;
        }

        public PaymentBuilder refundAmount(BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        public PaymentBuilder taxAmount(BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public PaymentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PaymentBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PaymentBuilder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public PaymentBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public PaymentBuilder failedAt(LocalDateTime failedAt) {
            this.failedAt = failedAt;
            return this;
        }

        public PaymentBuilder refundedAt(LocalDateTime refundedAt) {
            this.refundedAt = refundedAt;
            return this;
        }

        public PaymentBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public PaymentBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public PaymentBuilder deviceFingerprint(String deviceFingerprint) {
            this.deviceFingerprint = deviceFingerprint;
            return this;
        }

        public PaymentBuilder riskScore(Integer riskScore) {
            this.riskScore = riskScore;
            return this;
        }

        public PaymentBuilder fraudCheckResult(String fraudCheckResult) {
            this.fraudCheckResult = fraudCheckResult;
            return this;
        }

        /**
         * Build the Payment object
         */
        public Payment build() {
            Payment payment = new Payment();
            payment.id = this.id;
            payment.order = this.order;
            payment.amount = this.amount;
            payment.status = this.status;
            payment.type = this.type;
            payment.transactionId = this.transactionId;
            payment.gatewayTransactionId = this.gatewayTransactionId;
            payment.merchantReference = this.merchantReference;
            payment.authorizationCode = this.authorizationCode;
            payment.cardType = this.cardType;
            payment.maskedCardNumber = this.maskedCardNumber;
            payment.cardLastFourDigits = this.cardLastFourDigits;
            payment.cardHolderName = this.cardHolderName;
            payment.paymentGateway = this.paymentGateway;
            payment.gatewayResponse = this.gatewayResponse;
            payment.errorCode = this.errorCode;
            payment.errorMessage = this.errorMessage;
            payment.failureReason = this.failureReason;
            payment.currency = this.currency;
            payment.feeAmount = this.feeAmount;
            payment.refundAmount = this.refundAmount;
            payment.taxAmount = this.taxAmount;
            payment.createdAt = this.createdAt;
            payment.updatedAt = this.updatedAt;
            payment.processedAt = this.processedAt;
            payment.completedAt = this.completedAt;
            payment.failedAt = this.failedAt;
            payment.refundedAt = this.refundedAt;
            payment.ipAddress = this.ipAddress;
            payment.userAgent = this.userAgent;
            payment.deviceFingerprint = this.deviceFingerprint;
            payment.riskScore = this.riskScore;
            payment.fraudCheckResult = this.fraudCheckResult;
            return payment;
        }
    }
}