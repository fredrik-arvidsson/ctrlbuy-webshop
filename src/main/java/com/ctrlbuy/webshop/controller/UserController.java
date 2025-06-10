package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/registrera")
    public String register(Model model) {
        logger.debug("User registration page requested");
        model.addAttribute("title", "Registrera dig - CtrlBuy");
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        logger.debug("User login page requested");
        model.addAttribute("title", "Logga in - CtrlBuy");
        return "login";
    }

    @GetMapping("/profil")
    public String profil(Model model, Authentication authentication) {
        logger.debug("User profile page requested");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("User not authenticated, redirecting to login");
            return "redirect:/user/login";
        }

        model.addAttribute("title", "Min profil - CtrlBuy");
        model.addAttribute("username", authentication.getName());

        // Här kan du lägga till mer användarspecifik data senare
        return "user-profile";
    }

    @GetMapping("/bestallningar")
    public String orders(Model model, Authentication authentication) {
        logger.debug("User orders page requested");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("User not authenticated, redirecting to login");
            return "redirect:/user/login";
        }

        model.addAttribute("pageTitle", "Mina beställningar - CtrlBuy");
        model.addAttribute("feature", "Mina beställningar");
        model.addAttribute("icon", "shopping-bag");
        model.addAttribute("message", "Översikt över alla dina beställningar och deras status.");
        model.addAttribute("details", "Funktionen för att visa beställningshistorik utvecklas. Snart kan du se alla dina köp, status och spåra leveranser här.");
        model.addAttribute("returnUrl", "/user/profil");
        return "coming-soon";
    }

    @GetMapping("/favoriter")
    public String favorites(Model model, Authentication authentication) {
        logger.debug("User favorites page requested");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("User not authenticated, redirecting to login");
            return "redirect:/user/login";
        }

        model.addAttribute("pageTitle", "Mina favoriter - CtrlBuy");
        model.addAttribute("feature", "Mina favoriter");
        model.addAttribute("icon", "heart");
        model.addAttribute("message", "Alla produkter du har markerat som favoriter.");
        model.addAttribute("details", "Favoritfunktionen kommer snart! Du kommer att kunna spara produkter du är intresserad av för enkel återkomst.");
        model.addAttribute("returnUrl", "/user/profil");
        return "coming-soon";
    }

    @GetMapping("/installningar")
    public String settings(Model model, Authentication authentication) {
        logger.debug("User settings page requested");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("User not authenticated, redirecting to login");
            return "redirect:/user/login";
        }

        model.addAttribute("pageTitle", "Inställningar - CtrlBuy");
        model.addAttribute("feature", "Kontoinställningar");
        model.addAttribute("icon", "cog");
        model.addAttribute("message", "Hantera dina kontoinställningar och preferenser.");
        model.addAttribute("details", "Här kommer du snart att kunna ändra lösenord, uppdatera profil, hantera notifikationer och mycket mer.");
        model.addAttribute("returnUrl", "/user/profil");
        return "coming-soon";
    }
}