package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import com.ctrlbuy.webshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;

    public RegisterController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid RegisterRequest registerRequest,
                               BindingResult result,
                               Model model) {

        if (!validateRegistrationRequest(registerRequest, result)) {
            if (result.hasErrors()) {
                model.addAttribute("errors", result.getAllErrors());
            }
            return "register";
        }

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "register";
        }

        try {
            // ✅ FIX: Registrera användare och få tillbaka User object + token
            RegistrationResult registrationResult = userService.registerNewUserWithToken(registerRequest);
            User newUser = registrationResult.getUser();
            String verificationToken = registrationResult.getToken();

            // ✅ FIX: Anropa EmailService med korrekt User object och token
            emailService.sendVerificationEmail(newUser, verificationToken);

            // ✅ FIX: Redirect efter lyckad registrering
            return "redirect:/login?registration-pending";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("messageType", "error");
            return "register";
        }
    }

    private boolean validateRegistrationRequest(RegisterRequest request, BindingResult result) {
        boolean hasErrors = false;

        // Validera användarnamn
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            result.rejectValue("username", "error.username", "Användarnamn måste anges");
            hasErrors = true;
        } else if (request.getUsername().length() < 3) {
            result.rejectValue("username", "error.username", "Användarnamn måste vara minst 3 tecken");
            hasErrors = true;
        } else if (userService.existsByUsernameIncludingInactive(request.getUsername())) {
            result.rejectValue("username", "error.username", "Detta användarnamn är redan upptaget");
            hasErrors = true;
        }

        // Validera e-post
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            result.rejectValue("email", "error.email", "E-post måste anges");
            hasErrors = true;
        } else if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.rejectValue("email", "error.email", "Ogiltig e-postadress");
            hasErrors = true;
        } else if (userService.existsByEmailIncludingInactive(request.getEmail())) {
            result.rejectValue("email", "error.email",
                    "Denna e-postadress är redan registrerad. Om du har glömt ditt lösenord, använd 'Glömt lösenord'-funktionen.");
            hasErrors = true;
        }

        // Validera lösenord
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            result.rejectValue("password", "error.password", "Lösenord måste vara minst 6 tecken");
            hasErrors = true;
        }

        if (request.getConfirmPassword() == null ||
                !request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerRequest", "Lösenorden matchar inte");
            hasErrors = true;
        }

        // Validera förnamn
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            result.rejectValue("firstName", "error.firstName", "Förnamn måste anges");
            hasErrors = true;
        }

        // Validera efternamn
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            result.rejectValue("lastName", "error.lastName", "Efternamn måste anges");
            hasErrors = true;
        }

        return !hasErrors;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        try {
            boolean verified = userService.verifyEmail(token);

            if (verified) {
                model.addAttribute("message", "Ditt konto har bekräftats! Du kan nu logga in.");
                model.addAttribute("messageType", "success");
                return "verification-result";
            } else {
                model.addAttribute("message", "Ogiltigt eller utgånget verifieringstoken.");
                model.addAttribute("messageType", "error");
                return "verification-result";
            }

        } catch (RuntimeException e) {
            model.addAttribute("message", "Ogiltigt eller utgånget verifieringstoken.");
            model.addAttribute("messageType", "error");
            return "verification-result";
        }
    }

    @GetMapping("/resend-verification")
    public String showResendForm() {
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {
        try {
            if (!userService.existsByEmailIncludingInactive(email)) {
                model.addAttribute("error", "E-postadressen hittades inte.");
                model.addAttribute("messageType", "error");
                return "resend-verification";
            }

            String newToken = userService.createNewVerificationToken(email);
            if (newToken != null) {
                model.addAttribute("message", "Nytt verifieringsmail skickat!");
                model.addAttribute("messageType", "success");
            } else {
                model.addAttribute("error", "E-postadressen hittades inte eller kontot är inaktiverat.");
                model.addAttribute("messageType", "error");
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("messageType", "error");
        }
        return "resend-verification";
    }
}