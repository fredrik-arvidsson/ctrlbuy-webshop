package com.ctrlbuy.webshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "methodNotAllowed", required = false) String methodNotAllowed,
                        @RequestParam(value = "registration-pending", required = false) String registrationPending,
                        Model model) {

        log.info("Visar inloggningssidan");

        // Kontrollera om användaren redan är inloggad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            log.info("Användaren {} är redan inloggad, omdirigerar till hem", auth.getName());
            return "redirect:/";
        }

        model.addAttribute("title", "Logga in - CtrlBuy");

        if (error != null) {
            log.warn("Inloggningsfel: Ogiltigt användarnamn eller lösenord");
            model.addAttribute("error", "Ogiltigt användarnamn eller lösenord.");
        }

        if (logout != null) {
            log.info("Användaren har loggats ut framgångsrikt");
            model.addAttribute("success", "Du har loggats ut framgångsrikt.");
        }

        if (methodNotAllowed != null) {
            log.error("405 Method Not Allowed-fel vid inloggningsförsök");
            model.addAttribute("error", "Ett tekniskt fel uppstod. Försök igen.");
        }

        if (registrationPending != null) {
            log.info("Visar bekräftelsemeddelande för registrering");
            model.addAttribute("registrationPending", true);
        }

        return "login";
    }

    // Hantera 405 Method Not Allowed-fel för inloggningsprocessen
    @RequestMapping("/login-process-error")
    public String loginProcessError() {
        log.error("405 Method Not Allowed-fel vid inloggningsförsök");
        return "redirect:/login?methodNotAllowed=true";
    }
}