package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import com.ctrlbuy.webshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminVerificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            // Debug logging
            System.out.println("AdminVerification - Attempting to send to: " + email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            // Find user - handle Optional return type
            Optional<User> userOptional = userService.findByEmail(email);
            if (!userOptional.isPresent()) {
                System.out.println("User not found for email: " + email);
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOptional.get();
            System.out.println("User found: " + user.getEmail());

            // Create verification token
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

            // Save user
            userService.save(user);
            System.out.println("Verification token created: " + verificationToken);

            // Send verification email - pass User object, not email string
            emailService.sendVerificationEmail(user, verificationToken);
            System.out.println("Verification email sent successfully to: " + email);

            // Create success response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification link sent successfully");
            response.put("email", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("AdminVerification error: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send verification link");
            errorResponse.put("details", e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Debug endpoint for testing
    @GetMapping("/test-verification/{email}")
    public ResponseEntity<?> testVerification(@PathVariable String email) {
        try {
            System.out.println("Testing verification for: " + email);

            Optional<User> userOptional = userService.findByEmail(email);
            if (!userOptional.isPresent()) {
                return ResponseEntity.badRequest().body("User not found: " + email);
            }

            User user = userOptional.get();
            String testToken = "test-" + UUID.randomUUID().toString();

            // Pass User object to EmailService
            emailService.sendVerificationEmail(user, testToken);

            return ResponseEntity.ok("Test verification email sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Test failed: " + e.getMessage());
        }
    }
}