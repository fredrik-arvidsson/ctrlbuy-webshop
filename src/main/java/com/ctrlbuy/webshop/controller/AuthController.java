package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.UserService;
import com.ctrlbuy.webshop.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    /**
     * REST API endpoint for email verification
     * URL: /api/auth/verify?token=xxx
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean verified = userService.verifyEmail(token);

            if (verified) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Email successfully verified! You can now log in."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid or expired verification token."
                ));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid or expired verification token.",
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Web page endpoint for email verification (redirects to success/error page)
     * URL: /api/auth/verify-page?token=xxx
     */
    @GetMapping("/verify-page")
    public String verifyEmailPage(@RequestParam("token") String token, Model model) {
        try {
            boolean verified = userService.verifyEmail(token);

            if (verified) {
                model.addAttribute("message", "🎉 Your email has been verified! You can now log in to your account.");
                model.addAttribute("messageType", "success");
                model.addAttribute("redirectUrl", "/login");
            } else {
                model.addAttribute("message", "❌ Invalid or expired verification token. Please request a new verification email.");
                model.addAttribute("messageType", "error");
                model.addAttribute("redirectUrl", "/resend-verification");
            }

        } catch (RuntimeException e) {
            model.addAttribute("message", "❌ Verification failed: " + e.getMessage());
            model.addAttribute("messageType", "error");
            model.addAttribute("redirectUrl", "/resend-verification");
        }

        return "verification-result";
    }

    /**
     * Password reset request
     * URL: /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam("email") String email) {
        try {
            String resetToken = userService.generateResetToken(email);
            boolean emailSent = emailService.sendPasswordResetEmail(email, resetToken);

            if (emailSent) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Reset link sent to your email"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "If email exists, reset link has been sent"
                ));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "If email exists, reset link has been sent"
            ));
        }
    }
}