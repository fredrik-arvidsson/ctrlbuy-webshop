package com.ctrlbuy.webshop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        log.info("Rendering home page");

        // Explicit loggning för felsökning
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.debug("Authentication object exists. Name: {}, Principal: {}, Authenticated: {}",
                    auth.getName(), auth.getPrincipal(), auth.isAuthenticated());
        } else {
            log.debug("Authentication object is null");
        }

        // Viktigt: Lägg till autentiseringsinformation eftersom din HTML använder dessa attribut
        addAuthenticationToModel(model);

        // Lägg till välkomstmeddelande
        model.addAttribute("welcomeMessage", "Välkommen till CtrlBuy Webshop");

        // Logga alla attribut i modellen för felsökning
        log.debug("Model attributes: {}", model.asMap().keySet());

        return "home";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        log.info("Rendering welcome page");
        addAuthenticationToModel(model);
        model.addAttribute("welcomeMessage", "Välkommen tillbaka till CtrlBuy Webshop");
        return "welcome";
    }

    @GetMapping("/about")
    public String about(Model model) {
        log.info("Rendering about page");
        addAuthenticationToModel(model);
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        log.info("Rendering contact page");
        addAuthenticationToModel(model);
        return "contact";
    }

    @GetMapping("/products")
    public String products(Model model) {
        log.info("Rendering products page");
        addAuthenticationToModel(model);
        return "products";
    }

    // Viktigt: Se till att denna metod anropas för att lägga till de attribut din HTML använder
    private void addAuthenticationToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            log.debug("User is authenticated: {}", authentication.getName());
            model.addAttribute("username", authentication.getName());
            model.addAttribute("isAuthenticated", true);
            // Lägg till behörigheter om det behövs
            model.addAttribute("authorities", authentication.getAuthorities());
        } else {
            log.debug("User is not authenticated or is anonymous");
            model.addAttribute("isAuthenticated", false);
        }
    }
}