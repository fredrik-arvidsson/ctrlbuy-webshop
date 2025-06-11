package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import com.ctrlbuy.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterRequest registerRequest,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        log.info("Registreringsförsök för email: {}", registerRequest.getEmail());

        // Kör egen validering
        validateRegistration(registerRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            log.warn("Valideringsfel vid registrering: {}", bindingResult.getAllErrors());

            // Logga specifika fel för debugging
            bindingResult.getAllErrors().forEach(error ->
                    log.debug("Valideringsfel: {}", error.getDefaultMessage()));

            return "register";
        }

        try {
            // Registrera användare och få tillbaka User object + token
            RegistrationResult registrationResult = userService.registerNewUserWithToken(registerRequest);
            User newUser = registrationResult.getUser();
            String verificationToken = registrationResult.getToken();

            log.info("Användare skapad framgångsrikt: {}", newUser.getUsername());

            // Skicka verifieringsmail
            try {
                emailService.sendVerificationEmail(newUser, verificationToken);
                log.info("Verifieringsmail skickat till: {}", newUser.getEmail());
            } catch (Exception emailError) {
                log.error("Kunde inte skicka verifieringsmail till {}: {}", newUser.getEmail(), emailError.getMessage());
                model.addAttribute("error", "Kontot skapades men verifieringsmailen kunde inte skickas. Kontakta support.");
                return "register";
            }

            // Redirect med framgångsmeddelande
            redirectAttributes.addFlashAttribute("success",
                    "Registrering lyckades! Kontrollera din e-post (" + newUser.getEmail() + ") för att verifiera ditt konto.");

            return "redirect:/login";

        } catch (RuntimeException e) {
            log.error("Fel vid registrering: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            log.error("Oväntat fel vid registrering: {}", e.getMessage(), e);
            model.addAttribute("error", "Ett oväntat fel inträffade. Försök igen senare.");
            return "register";
        }
    }

    /**
     * Validerar registreringsdata
     */
    private void validateRegistration(RegisterRequest registerRequest, BindingResult bindingResult) {
        // Validera lösenordsmatchning
        if (registerRequest.getPassword() != null && registerRequest.getConfirmPassword() != null) {
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "password.mismatch",
                        "Lösenorden matchar inte. Kontrollera att du har skrivit samma lösenord i båda fälten.");
            }
        }

        // Kontrollera om användarnamn redan finns
        if (registerRequest.getUsername() != null &&
                userService.existsByUsernameIncludingInactive(registerRequest.getUsername())) {
            bindingResult.rejectValue("username", "username.exists",
                    "Detta användarnamn är redan upptaget. Välj ett annat användarnamn.");
        }

        // Kontrollera om e-post redan finns
        if (registerRequest.getEmail() != null &&
                userService.existsByEmailIncludingInactive(registerRequest.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Denna e-postadress är redan registrerad. Om du har glömt ditt lösenord kan du återställa det här.");
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        try {
            log.info("E-postverifiering påbörjad för token: {}", token.substring(0, 8) + "...");

            boolean verified = userService.verifyEmail(token);

            if (verified) {
                log.info("E-postverifiering lyckades för token: {}", token.substring(0, 8) + "...");
                model.addAttribute("message", "Ditt konto har bekräftats! Du kan nu logga in.");
                model.addAttribute("messageType", "success");
                model.addAttribute("showLoginButton", true);
                return "verification-result";
            } else {
                log.warn("E-postverifiering misslyckades för token: {}", token.substring(0, 8) + "...");
                model.addAttribute("message", "Ogiltigt eller utgånget verifieringstoken.");
                model.addAttribute("messageType", "error");
                model.addAttribute("showResendButton", true);
                return "verification-result";
            }

        } catch (RuntimeException e) {
            log.error("Fel vid e-postverifiering: {}", e.getMessage());
            model.addAttribute("message", "Ogiltigt eller utgånget verifieringstoken.");
            model.addAttribute("messageType", "error");
            model.addAttribute("showResendButton", true);
            return "verification-result";
        } catch (Exception e) {
            log.error("Oväntat fel vid e-postverifiering: {}", e.getMessage(), e);
            model.addAttribute("message", "Ett tekniskt fel inträffade. Försök igen senare.");
            model.addAttribute("messageType", "error");
            return "verification-result";
        }
    }

    @GetMapping("/resend-verification")
    public String showResendForm(Model model) {
        model.addAttribute("title", "Skicka verifieringsmail igen");
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {
        try {
            log.info("Begäran om att skicka om verifieringsmail för: {}", email);

            // Validera email-format
            if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("error", "Ange en giltig e-postadress.");
                model.addAttribute("messageType", "error");
                return "resend-verification";
            }

            // Kontrollera om användaren finns
            if (!userService.existsByEmailIncludingInactive(email)) {
                model.addAttribute("error", "E-postadressen hittades inte i vårt system.");
                model.addAttribute("messageType", "error");
                return "resend-verification";
            }

            // Skapa nytt verifieringstoken
            String newToken = userService.createNewVerificationToken(email);
            if (newToken != null) {
                log.info("Nytt verifieringstoken skapat för: {}", email);
                model.addAttribute("message",
                        "Nytt verifieringsmail har skickats till " + email + ". Kontrollera din e-post.");
                model.addAttribute("messageType", "success");
            } else {
                model.addAttribute("error",
                        "Kunde inte skicka verifieringsmail. Kontot kan redan vara verifierat eller inaktiverat.");
                model.addAttribute("messageType", "error");
            }

        } catch (RuntimeException e) {
            log.error("Fel vid omsändning av verifieringsmail för {}: {}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("messageType", "error");
        } catch (Exception e) {
            log.error("Oväntat fel vid omsändning av verifieringsmail för {}: {}", email, e.getMessage(), e);
            model.addAttribute("error", "Ett tekniskt fel inträffade. Försök igen senare.");
            model.addAttribute("messageType", "error");
        }

        return "resend-verification";
    }

    /**
     * API-endpoint för att kontrollera om användarnamn är tillgängligt
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsernameAvailability(@RequestParam("username") String username) {
        if (username == null || username.trim().length() < 3) {
            return false;
        }
        return !userService.existsByUsernameIncludingInactive(username.trim());
    }

    /**
     * API-endpoint för att kontrollera om e-post är tillgänglig
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam("email") String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        return !userService.existsByEmailIncludingInactive(email.trim());
    }
}