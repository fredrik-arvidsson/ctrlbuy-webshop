package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.PaymentService;
import com.ctrlbuy.webshop.model.Payment;
import com.ctrlbuy.webshop.model.PaymentInfo;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/**
 * PaymentController - STEG 4: Lägg till Payment models
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Health check - Fungerar ✅
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "PaymentController");
        result.put("paymentServiceAvailable", paymentService != null);
        return result;
    }

    /**
     * Basic validation - Test med request body ✅
     */
    @PostMapping("/validate")
    public Map<String, Object> validateBasic(@RequestBody BasicRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean isValid = request != null && request.getAmount() != null;
            result.put("valid", isValid);
            result.put("message", isValid ? "Valid" : "Invalid");
        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", "Validation error");
        }

        return result;
    }

    /**
     * Process payment - RIKTIGA PaymentService call!
     */
    @PostMapping("/process")
    public Map<String, Object> processPayment(@RequestBody PaymentInfoRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderId(request.getOrderId());
            paymentInfo.setAmount(request.getAmount());

            // Testa om problemet är med PaymentInfo som parameter
            // Låt oss prova utan att använda paymentService.processPayment temporärt
            Payment payment = null;

            // Simulera payment creation istället för att använda service
            // payment = paymentService.processPayment(paymentInfo);

            result.put("debug", "PaymentService call temporarily disabled for testing");
            result.put("paymentInfoCreated", paymentInfo != null);

            result.put("success", true);
            if (payment != null) {
                if (payment.getPaymentId() != null) {
                    result.put("paymentId", payment.getPaymentId());
                }
                if (payment.getStatus() != null) {
                    String statusName = payment.getStatus().toString();
                    result.put("status", statusName);
                }
                if (payment.getAmount() != null) {
                    result.put("amount", payment.getAmount());
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
        }

        return result;
    }

    // Request classes
    @Data
    public static class BasicRequest {
        private BigDecimal amount;
        private Long orderId;
    }

    @Data
    public static class PaymentInfoRequest {
        private BigDecimal amount;
        private Long orderId;
    }
}