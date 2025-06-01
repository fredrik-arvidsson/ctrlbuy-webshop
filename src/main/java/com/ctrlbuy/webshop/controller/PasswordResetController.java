package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.PasswordResetService;
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
    private PasswordResetService passwordResetService;

    /**
     * Visa formulär för att begära lösenordsåterställning
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    /**
     * Hantera begäran om lösenordsåterställning
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            logger.info("Begäran om lösenordsåterställning för email: {}", email);

            // Validera email-format
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                redirectAttributes.addFlashAttribute("error", "Vänligen ange en giltig e-postadress.");
                return "redirect:/forgot-password";
            }

            // Begär lösenordsåterställning
            boolean success = passwordResetService.requestPasswordReset(email.trim().toLowerCase());

            if (success) {
                redirectAttributes.addFlashAttribute("success",
                        "Om e-postadressen finns i vårt system har vi skickat instruktioner för lösenordsåterställning.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Ett fel uppstod. Försök igen senare.");
            }

        } catch (Exception e) {
            logger.error("Fel vid hantering av lösenordsåterställning", e);
            redirectAttributes.addFlashAttribute("error",
                    "Ett tekniskt fel uppstod. Försök igen senare.");
        }

        return "redirect:/forgot-password";
    }

    /**
     * Visa formulär för att återställa lösenord (från email-länk)
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        try {
            logger.info("Visar återställningsformulär för token");

            // Validera token
            if (!passwordResetService.isValidResetToken(token)) {
                model.addAttribute("error", "Ogiltigt eller utgånget återställningstoken. Begär en ny länk.");
                return "reset-password-error";
            }

            model.addAttribute("token", token);
            return "reset-password";

        } catch (Exception e) {
            logger.error("Fel vid visning av återställningsformulär", e);
            model.addAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
            return "reset-password-error";
        }
    }

    /**
     * Hantera återställning av lösenord
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            logger.info("Behandlar lösenordsåterställning");

            // Validera token
            if (!passwordResetService.isValidResetToken(token)) {
                model.addAttribute("error", "Ogiltigt eller utgånget återställningstoken. Begär en ny länk.");
                return "reset-password-error";
            }

            // Validera lösenord
            if (password == null || password.trim().length() < 6) {
                model.addAttribute("error", "Lösenordet måste vara minst 6 tecken långt.");
                model.addAttribute("token", token);
                return "reset-password";
            }

            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Lösenorden matchar inte.");
                model.addAttribute("token", token);
                return "reset-password";
            }

            // Återställ lösenord
            boolean success = passwordResetService.resetPassword(token, password);

            if (success) {
                redirectAttributes.addFlashAttribute("success",
                        "Ditt lösenord har återställts! Du kan nu logga in med ditt nya lösenord.");
                return "redirect:/login";
            } else {
                model.addAttribute("error", "Kunde inte återställa lösenordet. Försök igen.");
                model.addAttribute("token", token);
                return "reset-password";
            }

        } catch (Exception e) {
            logger.error("Fel vid återställning av lösenord", e);
            model.addAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}