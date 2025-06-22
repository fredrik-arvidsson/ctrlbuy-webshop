package com.ctrlbuy.webshop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "methodNotAllowed", required = false) String methodNotAllowed,
                        @RequestParam(value = "registration-pending", required = false) String registrationPending,
                        Model model) {

        // Hämta authentication från SecurityContext (som testerna förväntar sig)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kontrollera om användaren redan är inloggad
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            return "redirect:/";
        }

        // Sätt title-attribut som testerna förväntar sig
        model.addAttribute("title", "Logga in - CtrlBuy");

        // Hantera parametrar i den ordning testerna förväntar sig
        // methodNotAllowed har högst prioritet enligt testerna
        if (methodNotAllowed != null) {
            model.addAttribute("error", "Ett tekniskt fel uppstod. Försök igen.");
        } else if (error != null) {
            model.addAttribute("error", "Ogiltigt användarnamn eller lösenord.");
        }

        if (logout != null) {
            model.addAttribute("success", "Du har loggats ut framgångsrikt.");
        }

        if (registrationPending != null) {
            model.addAttribute("registrationPending", true);
        }

        return "login";
    }

    @RequestMapping("/login-process-error")
    public String loginProcessError() {
        return "redirect:/login?methodNotAllowed=true";
    }
}