package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailTestController {

    private final EmailService emailService;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @GetMapping("/test-email")
    public String testEmail(@RequestParam("email") String email) {
        try {
            emailService.sendVerificationEmail(email, "test-token-123", "Testanvändare");
            return "E-post skickad till: " + email;
        } catch (Exception e) {
            return "FEL vid e-postsändning: " + e.getMessage();
        }
    }

    // 🆕 ENKLA DEBUG ENDPOINTS
    @GetMapping("/test-email-status")
    public String testEmailStatus() {
        log.info("=== EMAIL STATUS TEST ===");
        log.info("EmailService: {}", emailService != null ? "EXISTS" : "NULL");
        log.info("Email Enabled: {}", emailEnabled);

        if (emailService != null) {
            log.info("EmailService Class: {}", emailService.getClass().getName());
        }

        return String.format("EmailService: %s | Email Enabled: %s",
                emailService != null ? "EXISTS" : "NULL",
                emailEnabled);
    }

    @GetMapping("/test-email-connection")
    public String testEmailConnection() {
        try {
            log.info("=== EMAIL CONNECTION TEST ===");

            if (emailService == null) {
                return "❌ EmailService is NULL - Check bean configuration";
            }

            log.info("EmailService found, testing connection...");
            boolean connectionResult = emailService.testEmailConnection();

            log.info("Connection test result: {}", connectionResult);

            return String.format("✅ EmailService: EXISTS | Email Enabled: %s | Connection: %s",
                    emailEnabled,
                    connectionResult ? "SUCCESS" : "FAILED");

        } catch (Exception e) {
            log.error("Email connection test failed: ", e);
            return "❌ Connection test failed: " + e.getMessage();
        }
    }

    @GetMapping("/test-order-email")
    public String testOrderEmail() {
        try {
            log.info("=== ORDER EMAIL TEST ===");

            if (emailService == null) {
                return "❌ EmailService is NULL";
            }

            // Skapa en test-order med korrekta datatyper
            Order testOrder = new Order();
            testOrder.setOrderNumber("TEST-" + System.currentTimeMillis());
            testOrder.setDeliveryName("Test Användare");
            testOrder.setTotalAmount(999.00); // Double istället för BigDecimal
            testOrder.setStatus(Order.OrderStatus.PENDING); // Enum istället för String

            String testEmail = "fredrik.g.arvidsson@gmail.com";

            log.info("Sending test order confirmation to: {}", testEmail);
            emailService.sendOrderConfirmation(testOrder, testEmail);

            return "✅ Test order email sent successfully to " + testEmail +
                    " for order: " + testOrder.getOrderNumber();

        } catch (Exception e) {
            log.error("Order email test failed: ", e);
            return "❌ Order email test failed: " + e.getMessage();
        }
    }

    @GetMapping("/debug-email-full")
    public String debugEmailFull() {
        StringBuilder debug = new StringBuilder();
        debug.append("=== FULL EMAIL DEBUG ===\n");

        // Basic status
        debug.append("EmailService Status: ").append(emailService != null ? "EXISTS" : "NULL").append("\n");
        debug.append("Email Enabled Config: ").append(emailEnabled).append("\n");

        if (emailService != null) {
            debug.append("EmailService Class: ").append(emailService.getClass().getName()).append("\n");
            debug.append("EmailService Package: ").append(emailService.getClass().getPackage().getName()).append("\n");

            // Test connection
            try {
                boolean connectionTest = emailService.testEmailConnection();
                debug.append("Connection Test: ").append(connectionTest ? "SUCCESS" : "FAILED").append("\n");
            } catch (Exception e) {
                debug.append("Connection Test: EXCEPTION - ").append(e.getMessage()).append("\n");
            }
        }

        // System properties check
        debug.append("System property app.email.enabled: ").append(System.getProperty("app.email.enabled", "NOT SET")).append("\n");

        // Environment variables check
        debug.append("Env var APP_EMAIL_ENABLED: ").append(System.getenv("APP_EMAIL_ENABLED") != null ? System.getenv("APP_EMAIL_ENABLED") : "NOT SET").append("\n");

        return debug.toString();
    }

    @GetMapping("/test-email-minimal")
    public String testEmailMinimal() {
        return "EmailService: " + (emailService != null ? "OK" : "NULL") +
                " | Enabled: " + emailEnabled;
    }
}