package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.UserService;
import com.ctrlbuy.webshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            logger.info("🔐 Begäran om lösenordsåterställning för: {}", email);

            // Generera reset-token via UserService
            String resetToken = userService.generateResetToken(email.trim().toLowerCase());

            if (resetToken != null) {
                // Skicka email via EmailService
                boolean emailSent = emailService.sendPasswordResetEmail(email.trim().toLowerCase(), resetToken);

                if (emailSent) {
                    logger.info("✅ Reset-mail skickat till: {}", email);
                } else {
                    logger.warn("⚠️ Kunde inte skicka reset-mail till: {}", email);
                }
            } else {
                logger.warn("⚠️ Ingen reset-token genererad för: {}", email);
            }

            // Visa alltid samma meddelande för säkerhet
            redirectAttributes.addFlashAttribute("success",
                    "Om e-postadressen finns har vi skickat återställningsinstruktioner.");

        } catch (Exception e) {
            logger.error("❌ Fel vid lösenordsåterställning för {}: {}", email, e.getMessage());
            redirectAttributes.addFlashAttribute("success",
                    "Om e-postadressen finns har vi skickat återställningsinstruktioner.");
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!userService.isValidResetToken(token)) {
            model.addAttribute("error", "Ogiltigt eller utgånget återställningstoken.");
            return "reset-password-error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        if (!userService.isValidResetToken(token)) {
            model.addAttribute("error", "Ogiltigt token.");
            return "reset-password-error";
        }

        if (password.length() < 6 || !password.equals(confirmPassword)) {
            model.addAttribute("error", "Ogiltigt lösenord eller matchar inte.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = userService.resetPassword(token, password);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Lösenord återställt!");
            return "redirect:/user/login";
        } else {
            model.addAttribute("error", "Kunde inte återställa lösenordet.");
            return "reset-password-error";
        }
    }
}