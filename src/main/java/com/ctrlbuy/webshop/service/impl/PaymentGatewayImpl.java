package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.model.*;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PaymentGatewayImpl implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentInfo paymentInfo) {
        log.info("Processing payment for amount: {}", paymentInfo.getAmount());

        try {
            // Simulate payment processing
            Thread.sleep(1000); // Simulate network call

            // Simple validation
            if (paymentInfo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return PaymentResult.failure("INVALID_AMOUNT", "Invalid payment amount");
            }

            // Generate mock transaction ID
            String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Simulate 90% success rate
            boolean success = Math.random() > 0.1;

            if (success) {
                log.info("Payment successful with transaction ID: {}", transactionId);
                return PaymentResult.success(transactionId, "Payment processed successfully");
            } else {
                log.warn("Payment failed for amount: {}", paymentInfo.getAmount());
                return PaymentResult.failure("PAYMENT_DECLINED", "Payment was declined by the bank");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
            return PaymentResult.failure("PROCESSING_ERROR", "Payment processing was interrupted");
        } catch (Exception e) {
            log.error("Error processing payment", e);
            return PaymentResult.failure("SYSTEM_ERROR", "An error occurred while processing the payment");
        }
    }

    @Override
    public boolean refund(String transactionId, BigDecimal amount) {
        log.info("Processing refund for transaction: {} amount: {}", transactionId, amount);

        try {
            // Simulate refund processing
            Thread.sleep(500);

            // Simulate 95% success rate for refunds
            boolean success = Math.random() > 0.05;

            if (success) {
                log.info("Refund successful for transaction: {}", transactionId);
            } else {
                log.warn("Refund failed for transaction: {}", transactionId);
            }

            return success;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Refund processing interrupted", e);
            return false;
        } catch (Exception e) {
            log.error("Error processing refund", e);
            return false;
        }
    }

    @Override
    public PaymentResult authorizePayment(PaymentInfo paymentInfo) {
        log.info("Authorizing payment for amount: {}", paymentInfo.getAmount());

        try {
            Thread.sleep(800);

            String authorizationId = "AUTH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            boolean success = Math.random() > 0.05;

            if (success) {
                log.info("Payment authorized with ID: {}", authorizationId);
                return PaymentResult.success(authorizationId, "Payment authorized successfully");
            } else {
                return PaymentResult.failure("AUTH_DECLINED", "Authorization declined");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("PROCESSING_ERROR", "Authorization interrupted");
        }
    }

    @Override
    public boolean capturePayment(String authorizationId, BigDecimal amount) {
        log.info("Capturing payment for authorization: {} amount: {}", authorizationId, amount);

        try {
            Thread.sleep(600);
            boolean success = Math.random() > 0.02;

            if (success) {
                log.info("Payment captured successfully for authorization: {}", authorizationId);
            } else {
                log.warn("Payment capture failed for authorization: {}", authorizationId);
            }

            return success;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment capture interrupted", e);
            return false;
        }
    }

    @Override
    public boolean voidAuthorization(String authorizationId) {
        log.info("Voiding authorization: {}", authorizationId);

        try {
            Thread.sleep(300);
            boolean success = Math.random() > 0.01;

            if (success) {
                log.info("Authorization voided successfully: {}", authorizationId);
            } else {
                log.warn("Failed to void authorization: {}", authorizationId);
            }

            return success;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Authorization void interrupted", e);
            return false;
        }
    }

    @Override
    public PaymentResult verifyCard(PaymentInfo paymentInfo) {
        log.info("Verifying card for order: {}", paymentInfo.getOrderId());

        try {
            Thread.sleep(400);

            // Basic card validation
            if (paymentInfo.getCardNumber() == null || paymentInfo.getCardNumber().length() < 13) {
                return PaymentResult.failure("INVALID_CARD", "Invalid card number");
            }

            boolean success = Math.random() > 0.1;

            if (success) {
                return PaymentResult.success("VERIFIED", "Card verified successfully");
            } else {
                return PaymentResult.failure("VERIFICATION_FAILED", "Card verification failed");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("PROCESSING_ERROR", "Card verification interrupted");
        }
    }

    @Override
    public boolean isHealthy() {
        log.debug("Checking payment gateway health");

        try {
            // Simulate health check
            Thread.sleep(100);
            // Simulate 99% uptime
            return Math.random() > 0.01;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public PaymentGatewayInfo getGatewayInfo() {
        return PaymentGatewayInfo.builder()
                .name("Mock Payment Gateway")
                .version("1.0.0")
                .supportedCurrencies(java.util.List.of("SEK", "USD", "EUR"))
                .provider("Mock Provider")
                .supportsRefunds(true)
                .status("ACTIVE")
                .build();
    }

    @Override
    public ValidationResult validatePaymentInfo(PaymentInfo paymentInfo) {
        log.debug("Validating payment info for order: {}", paymentInfo.getOrderId());

        ValidationResult.ValidationResultBuilder resultBuilder = ValidationResult.builder();

        // Basic validation
        if (paymentInfo == null) {
            return resultBuilder
                    .valid(false)
                    .errorCode("NULL_PAYMENT_INFO")
                    .errorMessage("Payment information is null")
                    .build();
        }

        if (paymentInfo.getAmount() == null || paymentInfo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return resultBuilder
                    .valid(false)
                    .errorCode("INVALID_AMOUNT")
                    .errorMessage("Invalid payment amount")
                    .build();
        }

        if (paymentInfo.getOrderId() == null) {
            return resultBuilder
                    .valid(false)
                    .errorCode("MISSING_ORDER_ID")
                    .errorMessage("Order ID is required")
                    .build();
        }

        // For credit card payments, validate card info
        if (paymentInfo.getCardNumber() != null) {
            if (paymentInfo.getCardNumber().length() < 13 || paymentInfo.getCardNumber().length() > 19) {
                return resultBuilder
                        .valid(false)
                        .errorCode("INVALID_CARD_NUMBER")
                        .errorMessage("Invalid card number length")
                        .build();
            }

            if (paymentInfo.getCvv() == null || paymentInfo.getCvv().length() < 3) {
                return resultBuilder
                        .valid(false)
                        .errorCode("INVALID_CVV")
                        .errorMessage("Invalid CVV")
                        .build();
            }

            if (paymentInfo.getExpiryMonth() == null ||
                    paymentInfo.getExpiryMonth() < 1 || paymentInfo.getExpiryMonth() > 12) {
                return resultBuilder
                        .valid(false)
                        .errorCode("INVALID_EXPIRY_MONTH")
                        .errorMessage("Invalid expiry month")
                        .build();
            }

            if (paymentInfo.getExpiryYear() == null ||
                    paymentInfo.getExpiryYear() < LocalDateTime.now().getYear()) {
                return resultBuilder
                        .valid(false)
                        .errorCode("INVALID_EXPIRY_YEAR")
                        .errorMessage("Invalid expiry year")
                        .build();
            }
        }

        log.debug("Payment info validation successful");
        return resultBuilder
                .valid(true)
                .errorCode(null)
                .errorMessage("Validation successful")
                .build();
    }
}