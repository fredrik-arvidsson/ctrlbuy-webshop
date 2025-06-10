package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {

    private String paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String description;
    private String customerEmail;
    private String customerName;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // Credit card fields
    private String cardNumber;
    private String cardType;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cvv;
    private String cardHolderName;

    // Saknade metoder som PaymentService behöver
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public PaymentInfo setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentInfo setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    // Credit card getters and setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    // Builder pattern helpers
    public static PaymentInfo createForOrder(Long orderId, BigDecimal amount, PaymentType type) {
        return PaymentInfo.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentType(type)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Validation methods
    public boolean isValid() {
        return paymentId != null &&
                orderId != null &&
                amount != null &&
                amount.compareTo(BigDecimal.ZERO) > 0 &&
                paymentType != null;
    }

    // Status check methods
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(paymentStatus);
    }

    public boolean isCompleted() {
        return PaymentStatus.COMPLETED.equals(paymentStatus);
    }

    public boolean isFailed() {
        return PaymentStatus.FAILED.equals(paymentStatus);
    }

    // Convert to Payment entity - fix type conversion
    public Payment toPaymentEntity() {
        return Payment.builder()
                .id(this.paymentId != null ? Long.parseLong(this.paymentId) : null)
                .amount(this.amount)
                .type(this.paymentType)
                .status(this.paymentStatus != null ? this.paymentStatus : PaymentStatus.PENDING)
                .gatewayTransactionId(this.transactionId)
                .cardType(this.cardType)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .build();
    }

    // Create from Payment entity - fix type conversion
    public static PaymentInfo fromPaymentEntity(Payment payment) {
        return PaymentInfo.builder()
                .paymentId(payment.getPaymentId() != null ? payment.getPaymentId().toString() : null)
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentType(payment.getType())
                .paymentStatus(payment.getStatus())
                .transactionId(payment.getGatewayTransactionId())
                .cardType(payment.getCardType())
                .createdAt(payment.getCreatedAt())
                .processedAt(payment.getProcessedAt())
                .build();
    }

    // Utility method for masking card number
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    // Helper method to create a sanitized copy without sensitive data
    public PaymentInfo createSanitizedCopy() {
        PaymentInfo sanitized = PaymentInfo.builder()
                .paymentId(this.paymentId)
                .orderId(this.orderId)
                .amount(this.amount)
                .paymentType(this.paymentType)
                .paymentStatus(this.paymentStatus)
                .transactionId(this.transactionId)
                .description(this.description)
                .customerEmail(this.customerEmail)
                .customerName(this.customerName)
                .createdAt(this.createdAt)
                .processedAt(this.processedAt)
                .cardType(this.cardType)
                .expiryMonth(this.expiryMonth)
                .expiryYear(this.expiryYear)
                .cardHolderName(this.cardHolderName)
                .build();

        // Set masked card number instead of real one
        sanitized.setCardNumber(getMaskedCardNumber());
        // Clear CVV for security
        sanitized.setCvv(null);

        return sanitized;
    }
}