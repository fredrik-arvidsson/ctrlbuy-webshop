package com.ctrlbuy.webshop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HelpController {

    private static final Logger logger = LoggerFactory.getLogger(HelpController.class);

    @GetMapping("/leveransinformation")
    public String leveransinformation(Model model) {
        logger.debug("Leveransinformation page requested");
        model.addAttribute("pageTitle", "Leveransinformation - CtrlBuy");
        model.addAttribute("feature", "Leveransinformation");
        model.addAttribute("icon", "truck");
        model.addAttribute("message", "Information om frakt, leveranstider och spårning av ditt paket.");
        model.addAttribute("details", "Här kommer du snart att kunna läsa om våra leveransalternativ, fraktkostnader och hur du spårar ditt paket.");
        model.addAttribute("returnUrl", "/contact");
        return "coming-soon";
    }

    @GetMapping("/returer-reklamationer")
    public String returerReklamationer(Model model) {
        logger.debug("Returer & Reklamationer page requested");
        model.addAttribute("pageTitle", "Returer & Reklamationer - CtrlBuy");
        model.addAttribute("feature", "Returer & Reklamationer");
        model.addAttribute("icon", "undo-alt");
        model.addAttribute("message", "Här kan du hantera returer och reklamationer enkelt och smidigt.");
        model.addAttribute("details", "Funktionen för att returnera varor och göra reklamationer kommer snart. Du kommer att kunna starta returprocessen direkt här.");
        model.addAttribute("returnUrl", "/contact");
        return "coming-soon";
    }

    @GetMapping("/betalningsmetoder")
    public String betalningsmetoder(Model model) {
        logger.debug("Betalningsmetoder page requested");
        model.addAttribute("pageTitle", "Betalningsmetoder - CtrlBuy");
        model.addAttribute("feature", "Betalningsmetoder");
        model.addAttribute("icon", "credit-card");
        model.addAttribute("message", "Information om våra betalningsalternativ och hur betalningen fungerar.");
        model.addAttribute("details", "Vi kommer snart att erbjuda flera betalningsmetoder som kort, Swish, Klarna och bankgiro för din bekvämlighet.");
        model.addAttribute("returnUrl", "/contact");
        return "coming-soon";
    }

    @GetMapping("/kundtjanst")
    public String kundtjanst(Model model) {
        logger.debug("Kundtjänst page requested");
        model.addAttribute("pageTitle", "Kundtjänst - CtrlBuy");
        model.addAttribute("feature", "Kundtjänst");
        model.addAttribute("icon", "headset");
        model.addAttribute("message", "Direktkontakt med vår kundtjänst för personlig hjälp och support.");
        model.addAttribute("details", "Kundtjänstfunktionen utvecklas för närvarande. Snart kan du chatta direkt med våra supportagenter.");
        model.addAttribute("returnUrl", "/contact");
        return "coming-soon";
    }

    // 🆕 NYA MAPPNINGAR FÖR SNABBLÄNKARNA
    @GetMapping("/spara-bestallning")
    public String sparaBestallning(Model model, Authentication authentication) {
        logger.debug("Spåra beställning page requested");
        model.addAttribute("pageTitle", "Spåra beställning - CtrlBuy");
        model.addAttribute("feature", "Beställningsspårning");
        model.addAttribute("icon", "truck");
        model.addAttribute("message", "Realtidsspårning av dina leveranser, SMS-notifieringar och interaktiv karta. Håll koll på dina paket från lager till dörr!");
        model.addAttribute("returnUrl", "/min-profil");
        return "coming-soon";
    }

    @GetMapping("/returer")
    public String returer(Model model, Authentication authentication) {
        logger.debug("Returer page requested");
        model.addAttribute("pageTitle", "Returer - CtrlBuy");
        model.addAttribute("feature", "Returer");
        model.addAttribute("icon", "undo");
        model.addAttribute("message", "Enkla digitala returetiketter, automatisk återbetalning och kostnadsfri retur för defekta produkter. Allt inom 30 dagar!");
        model.addAttribute("returnUrl", "/min-profil");
        return "coming-soon";
    }

    @GetMapping("/garantivillkor")
    public String garantivillkor(Model model, Authentication authentication) {
        logger.debug("Garantivillkor page requested");
        model.addAttribute("pageTitle", "Garantivillkor - CtrlBuy");
        model.addAttribute("feature", "Garantivillkor");
        model.addAttribute("icon", "shield-alt");
        model.addAttribute("message", "Minst 2 års garanti på alla produkter, möjlighet till utökad garanti upp till 5 år och snabb garantihantering!");
        model.addAttribute("returnUrl", "/min-profil");
        return "coming-soon";
    }

    // Uppdatera även contact-sidan
    @GetMapping("/contact")
    public String contact(Model model) {
        logger.debug("Contact page requested");
        model.addAttribute("pageTitle", "Kontakta oss - CtrlBuy");
        return "contact"; // Nu använder vi den nya contact.html
    }
}