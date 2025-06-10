package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {

    private boolean successful;
    private String transactionId;
    private String message;
    private PaymentStatus paymentStatus;
    private String errorCode;
    private LocalDateTime processedAt;
    private LocalDateTime refundedAt;

    // Factory methods för att skapa results
    public static PaymentResult success(String transactionId, String message) {
        return PaymentResult.builder()
                .successful(true)
                .transactionId(transactionId)
                .message(message)
                .paymentStatus(PaymentStatus.COMPLETED)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentResult failure(String errorCode, String message) {
        return PaymentResult.builder()
                .successful(false)
                .errorCode(errorCode)
                .message(message)
                .paymentStatus(PaymentStatus.FAILED)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentResult pending(String message) {
        return PaymentResult.builder()
                .successful(false)
                .message(message)
                .paymentStatus(PaymentStatus.PENDING)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // Saknade metoder som PaymentService försöker använda
    public PaymentResult setPaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
        return this;
    }

    public PaymentResult setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentResult setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
        return this;
    }

    // Helper methods
    public PaymentResult negate() {
        return PaymentResult.builder()
                .successful(!this.successful)
                .transactionId(this.transactionId)
                .message("Negated: " + this.message)
                .paymentStatus(this.successful ? PaymentStatus.FAILED : PaymentStatus.COMPLETED)
                .errorCode(this.errorCode)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean isSuccess() {
        return successful;
    }

    public boolean isFailed() {
        return !successful;
    }

    public String getErrorMessage() {
        return message;
    }
}