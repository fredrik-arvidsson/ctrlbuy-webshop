package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ctrlbuy.webshop.service.UserService;
import java.util.stream.Collectors;

@Controller
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
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
    public String registerUser(@ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest,
                               BindingResult bindingResult, Model model) {

        // Kontrollera om lösenorden matchar
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest",
                    "Lösenorden matchar inte");
        }

        // Om det finns valideringsfel, visa registreringsformuläret igen
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            return "register";
        }

        // Försök registrera användaren
        try {
            // Registrera användaren och få tillbaka verifikationstoken
            String verificationToken = userService.registerNewUserWithToken(registerRequest);

            // Skicka bekräftelsemail
            emailService.sendVerificationEmail(
                    registerRequest.getEmail(),
                    verificationToken,
                    registerRequest.getFirstName()
            );

            // Omdirigera till bekräftelsesida
            return "redirect:/login?registration-pending";

        } catch (Exception e) {
            // Om något går fel (t.ex. användarnamnet är redan taget)
            model.addAttribute("errors", java.util.Collections.singletonList(e.getMessage()));
            return "register";
        }
    }

    // FIXAD: Ändrat från /verify till /verify-email för att matcha SecurityConfig
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        System.out.println("🔍 === EMAIL VERIFIERING STARTAD ===");
        System.out.println("🔍 Token mottagen: " + token);
        System.out.println("🔍 Token längd: " + token.length());
        System.out.println("🔍 Token format: " + (token.matches("[a-f0-9-]{36}") ? "UUID format" : "Ogiltigt format"));

        try {
            System.out.println("🔍 Söker efter token i databasen...");

            boolean verified = userService.verifyEmailToken(token);
            System.out.println("🔍 Verifiering resultat: " + verified);

            if (verified) {
                System.out.println("✅ Token verifierad framgångsrikt!");
                model.addAttribute("message", "Ditt konto har bekräftats! Du kan nu logga in.");
                model.addAttribute("messageType", "success");
            } else {
                System.out.println("❌ Token verifiering misslyckades");
                model.addAttribute("message", "Bekräftelselänken är ogiltig eller har gått ut.");
                model.addAttribute("messageType", "error");
            }

        } catch (Exception e) {
            System.out.println("💥 Exception vid verifiering: " + e.getClass().getSimpleName());
            System.out.println("💥 Exception meddelande: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("message", "Ett fel uppstod vid bekräftelsen: " + e.getMessage());
            model.addAttribute("messageType", "error");
        }

        System.out.println("🔍 === EMAIL VERIFIERING AVSLUTAD ===");
        return "verification-result";
    }

    @GetMapping("/resend-verification")
    public String showResendForm() {
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {
        try {
            String newToken = userService.createNewVerificationToken(email);

            // Hämta förnamn från användaren via email
            String firstName = userService.getFirstNameByEmail(email);
            emailService.sendVerificationEmail(email, newToken, firstName);

            model.addAttribute("message", "Ett nytt bekräftelsemail har skickats till " + email);
            model.addAttribute("messageType", "success");

        } catch (Exception e) {
            model.addAttribute("message", "Kunde inte skicka nytt bekräftelsemail: " + e.getMessage());
            model.addAttribute("messageType", "error");
        }

        return "resend-verification";
    }
}