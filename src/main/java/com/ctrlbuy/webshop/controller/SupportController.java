package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/support")
public class SupportController {

    private static final Logger logger = LoggerFactory.getLogger(SupportController.class);

    @GetMapping("")
    public String support(Model model) {
        logger.debug("Support main page requested");
        model.addAttribute("title", "Support - CtrlBuy");
        return "support";
    }

    @GetMapping("/spara-bestallning")
    public String sparaBestallning(Model model) {
        logger.debug("Track order page requested");
        model.addAttribute("pageTitle", "Spåra beställning - CtrlBuy");
        model.addAttribute("feature", "Spåra beställning");
        model.addAttribute("icon", "shipping-fast");
        model.addAttribute("message", "Följ dina paket i realtid med detaljerad spårningsinformation.");
        model.addAttribute("details", "Snart kommer du att kunna spåra alla dina beställningar direkt här. Du får automatiska uppdateringar via email och SMS.");
        model.addAttribute("returnUrl", "/support");
        return "coming-soon";
    }

    @GetMapping("/returer")
    public String returer(Model model) {
        logger.debug("Returns page requested");
        model.addAttribute("pageTitle", "Returer - CtrlBuy");
        model.addAttribute("feature", "Returer");
        model.addAttribute("icon", "undo-alt");
        model.addAttribute("message", "Enkel och smidig returprocess direkt online.");
        model.addAttribute("details", "Vi utvecklar ett enkelt system för att hantera returer. Du kommer att kunna starta returprocessen direkt här utan att behöva kontakta kundtjänst.");
        model.addAttribute("returnUrl", "/support");
        return "coming-soon";
    }

    @GetMapping("/garantivillkor")
    public String garantivillkor(Model model) {
        logger.debug("Warranty page requested");
        model.addAttribute("pageTitle", "Garantivillkor - CtrlBuy");
        model.addAttribute("feature", "Garantivillkor");
        model.addAttribute("icon", "shield-alt");
        model.addAttribute("message", "Digital garantihantering och villkor för dina produkter.");
        model.addAttribute("details", "Här kommer du att kunna hantera garantier för alla dina köp. Vi sparar automatiskt kvitton och garantiinformation.");
        model.addAttribute("returnUrl", "/support");
        return "coming-soon";
    }

    @GetMapping("/kundtjanst")
    public String kundtjanst(Model model) {
        logger.debug("Customer service page requested");
        model.addAttribute("pageTitle", "Kundtjänst - CtrlBuy");
        model.addAttribute("feature", "Kundtjänst");
        model.addAttribute("icon", "headset");
        model.addAttribute("message", "Direktkontakt med vår kundtjänst för personlig hjälp och support.");
        model.addAttribute("details", "Vi bygger ett avancerat supportsystem med live-chat, ticket-system och FAQ. Snart kan du få hjälp dygnet runt.");
        model.addAttribute("returnUrl", "/support");
        return "coming-soon";
    }
}
