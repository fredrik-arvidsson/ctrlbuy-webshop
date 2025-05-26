package com.ctrlbuy.webshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ctrlbuy.webshop.repository.ProductRepository;

@Controller
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "logout", required = false) String logout) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kontrollera om användaren är inloggad
        boolean isAuthenticated = auth != null && auth.isAuthenticated() &&
                !auth.getName().equals("anonymousUser");

        if (isAuthenticated) {
            model.addAttribute("username", auth.getName());
            // LÄGG TILL PRODUKTER NÄR ANVÄNDAREN ÄR INLOGGAD
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        // Visa meddelande när användaren loggat ut
        if (logout != null) {
            model.addAttribute("success", "Du har loggats ut framgångsrikt.");
        }

        model.addAttribute("title", "Hem - CtrlBuy");
        return "index";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        // Omdirigera till hem istället
        return "redirect:/";
    }

    @GetMapping("/produkter")
    public String products(Model model) {
        // Lägg till alla produkter på produktsidan
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("title", "Produkter - CtrlBuy");
        return "products";
    }

    @GetMapping("/om-oss")
    public String about(Model model) {
        model.addAttribute("title", "Om oss - CtrlBuy");
        return "about";
    }

    @GetMapping("/kontakt")
    public String contact(Model model) {
        model.addAttribute("title", "Kontakt - CtrlBuy");
        return "contact";
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("title", "Support - CtrlBuy");
        return "support";
    }

    @GetMapping("/registrera")
    public String register(Model model) {
        model.addAttribute("title", "Registrera dig - CtrlBuy");
        return "register";
    }

    @GetMapping("/min-profil")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Min profil - CtrlBuy");
        model.addAttribute("username", authentication.getName());
        return "profile";
    }

    @GetMapping("/varukorg")
    public String cart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Varukorg - CtrlBuy");
        return "cart";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Glömt lösenord - CtrlBuy");
        return "forgot-password";
    }
}