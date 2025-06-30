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
                                        @RequestParam("username") String username,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            logger.info("🔐 Begäran om lösenordsåterställning för användarnamn: {} och email: {}", username, email);

            // Kräv BÅDE username OCH email för säkerhet
            String resetToken = userService.generateResetTokenWithUsernameAndEmail(
                    username.trim(),
                    email.trim().toLowerCase()
            );

            if (resetToken != null) {
                // Skicka email via EmailService
                boolean emailSent = emailService.sendPasswordResetEmail(email.trim().toLowerCase(), resetToken);

                if (emailSent) {
                    logger.info("✅ Reset-mail skickat till: {} för användare: {}", email, username);
                    model.addAttribute("success",
                            "En återställningslänk har skickats till din registrerade e-postadress.");
                } else {
                    logger.warn("⚠️ Kunde inte skicka reset-mail till: {} för användare: {}", email, username);
                    model.addAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
                }
            } else {
                logger.warn("⚠️ Ingen matchning för användarnamn: {} och email: {}", username, email);
                model.addAttribute("error",
                        "Ingen användare hittades med denna kombination av användarnamn och e-postadress.");
            }

        } catch (Exception e) {
            logger.error("❌ Fel vid lösenordsåterställning för {} / {}: {}", username, email, e.getMessage());
            model.addAttribute("error", "Ett fel uppstod. Kontrollera dina uppgifter och försök igen.");
        }

        return "forgot-password";
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

        if (password.length() < 6) {
            model.addAttribute("error", "Lösenordet måste vara minst 6 tecken långt.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Lösenorden matchar inte.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = userService.resetPassword(token, password);

        if (success) {
            logger.info("✅ Lösenord återställt framgångsrikt för token: {}", token.substring(0, 8) + "...");
            redirectAttributes.addFlashAttribute("success", "Lösenord återställt framgångsrikt! Du kan nu logga in.");
            return "redirect:/user/login";
        } else {
            logger.error("❌ Kunde inte återställa lösenordet för token: {}", token.substring(0, 8) + "...");
            model.addAttribute("error", "Kunde inte återställa lösenordet. Tokenets giltighetstid kan ha gått ut.");
            return "reset-password-error";
        }
    }
}