package com.ctrlbuy.webshop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller för juridiska sidor som användarvillkor och integritetspolicy
 */
@Controller
@Slf4j
public class LegalController {

    /**
     * Visar användarvillkor
     */
    @GetMapping("/terms")
    public String showTerms(Model model, Authentication auth) {
        log.debug("Visar användarvillkor för användare: {}",
                auth != null ? auth.getName() : "anonym");

        // Lägg till metadata för sidan
        model.addAttribute("pageTitle", "Användarvillkor");
        model.addAttribute("pageDescription", "Läs våra användarvillkor och regler för handel");

        return "terms";
    }

    /**
     * Visar integritetspolicy
     */
    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy(Model model, Authentication auth) {
        log.debug("Visar integritetspolicy för användare: {}",
                auth != null ? auth.getName() : "anonym");

        // Lägg till metadata för sidan
        model.addAttribute("pageTitle", "Integritetspolicy");
        model.addAttribute("pageDescription", "Information om hur vi hanterar dina personuppgifter");

        return "integritetspolicy";  // FIXAT! Nu matchar det filnamnet integritetspolicy.html
    }

    /**
     * Visar cookies-policy (för framtida användning)
     */
    @GetMapping("/cookies")
    public String showCookiesPolicy(Model model, Authentication auth) {
        log.debug("Visar cookies-policy för användare: {}",
                auth != null ? auth.getName() : "anonym");

        model.addAttribute("pageTitle", "Cookies-policy");
        model.addAttribute("pageDescription", "Information om hur vi använder cookies");

        return "cookies";
    }

    /**
     * Visar GDPR-information och användarrättigheter
     */
    @GetMapping("/gdpr")
    public String showGdprInfo(Model model, Authentication auth) {
        log.debug("Visar GDPR-information för användare: {}",
                auth != null ? auth.getName() : "anonym");

        model.addAttribute("pageTitle", "Dina rättigheter enligt GDPR");
        model.addAttribute("pageDescription", "Information om dina dataskyddsrättigheter");

        return "gdpr";
    }

    /**
     * Visar allmänna villkor och policy-sida (samlingssida)
     */
    @GetMapping("/legal")
    public String showLegalIndex(Model model, Authentication auth) {
        log.debug("Visar juridisk översiktssida för användare: {}",
                auth != null ? auth.getName() : "anonym");

        model.addAttribute("pageTitle", "Juridisk information");
        model.addAttribute("pageDescription", "Övergripande information om våra villkor och policys");

        return "legal/index";
    }
}