package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
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

        log.info("🚀 Registreringsförsök för användare: {}", registerRequest.getUsername());

        // 🔧 TRIMMA FÄLT INNAN VALIDERING
        registerRequest.trimAllFields();
        registerRequest.normalizeEmail();

        // Kontrollera om några kritiska fält är null EFTER trimning
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            bindingResult.rejectValue("username", "username.required", "Användarnamn är obligatoriskt");
        }

        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            bindingResult.rejectValue("email", "email.required", "E-post är obligatorisk");
        }

        // Kör egen validering
        validateRegistration(registerRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            log.warn("⚠️ Valideringsfel vid registrering för: {}", registerRequest.getUsername());
            bindingResult.getAllErrors().forEach(error -> {
                log.debug("Valideringsfel: {}", error.getDefaultMessage());
            });
            return "register";
        }

        try {
            log.info("✅ Validering OK, försöker registrera användare: {}", registerRequest.getUsername());

            // ✅ ANVÄND ENKEL REGISTRERING UTAN EMAIL-VERIFIERING
            RegistrationResult registrationResult = userService.registerUser(registerRequest);

            if (registrationResult.isSuccess()) {
                log.info("🎉 Registrering lyckades för användare: {}", registerRequest.getUsername());

                // ✅ ANVÄNDAREN KAN LOGGA IN DIREKT - INGEN EMAIL KRÄVS
                redirectAttributes.addFlashAttribute("success",
                        "Registrering lyckades! Du kan nu logga in direkt med ditt användarnamn och lösenord.");

                return "redirect:/login";
            } else {
                log.warn("❌ Registrering misslyckades för användare: {} - {}",
                        registerRequest.getUsername(), registrationResult.getMessage());
                model.addAttribute("error", registrationResult.getMessage());
                return "register";
            }

        } catch (RuntimeException e) {
            log.error("❌ RuntimeException vid registrering för användare: {}",
                    registerRequest.getUsername(), e);
            model.addAttribute("error", "Registreringsfel: " + e.getMessage());
            return "register";
        } catch (Exception e) {
            log.error("❌ Oväntat fel vid registrering för användare: {}",
                    registerRequest.getUsername(), e);
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
        // ✅ EMAIL-VERIFIERING DISABLED - VISA MEDDELANDE
        model.addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt.");
        model.addAttribute("messageType", "info");
        model.addAttribute("showLoginButton", true);
        return "verification-result";
    }

    @GetMapping("/resend-verification")
    public String showResendForm(Model model) {
        // ✅ EMAIL-VERIFIERING DISABLED - VISA MEDDELANDE
        model.addAttribute("title", "Email-verifiering inaktiverad");
        model.addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt vid registrering.");
        model.addAttribute("messageType", "info");
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {
        // ✅ EMAIL-VERIFIERING DISABLED - VISA MEDDELANDE
        model.addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt vid registrering.");
        model.addAttribute("messageType", "info");
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