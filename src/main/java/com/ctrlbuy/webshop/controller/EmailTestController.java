package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail(@RequestParam("email") String email) {
        try {
            emailService.sendVerificationEmail(email, "test-token-123", "Testanvändare");
            return "E-post skickad till: " + email;
        } catch (Exception e) {
            return "FEL vid e-postsändning: " + e.getMessage();
        }
    }
}