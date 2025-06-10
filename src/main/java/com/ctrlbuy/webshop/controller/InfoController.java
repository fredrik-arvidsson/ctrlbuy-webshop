package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/info")
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @GetMapping("/leverans")
    public String leveransinformation(Model model) {
        logger.debug("Delivery information page requested");
        model.addAttribute("pageTitle", "Leveransinformation - CtrlBuy");
        model.addAttribute("feature", "Leveransinformation");
        model.addAttribute("icon", "truck");
        model.addAttribute("message", "Information om frakt, leveranstider och spårning av ditt paket.");
        model.addAttribute("details", "Här kommer du snart att kunna läsa om våra leveransalternativ, fraktkostnader och hur du spårar ditt paket. Vi erbjuder flera leveransmetoder för din bekvämlighet.");
        model.addAttribute("returnUrl", "/info/kontakt");
        return "coming-soon";
    }

    @GetMapping("/betalning")
    public String betalningsmetoder(Model model) {
        logger.debug("Payment methods page requested");
        model.addAttribute("pageTitle", "Betalningsmetoder - CtrlBuy");
        model.addAttribute("feature", "Betalningsmetoder");
        model.addAttribute("icon", "credit-card");
        model.addAttribute("message", "Information om våra betalningsalternativ och hur betalningen fungerar.");
        model.addAttribute("details", "Vi kommer snart att erbjuda flera betalningsmetoder som kort, Swish, Klarna och bankgiro för din bekvämlighet. Alla betalningar är säkra och krypterade.");
        model.addAttribute("returnUrl", "/info/kontakt");
        return "coming-soon";
    }

    @GetMapping("/retur-policy")
    public String returerReklamationer(Model model) {
        logger.debug("Return policy page requested");
        model.addAttribute("pageTitle", "Retur & Reklamationspolicy - CtrlBuy");
        model.addAttribute("feature", "Retur & Reklamationspolicy");
        model.addAttribute("icon", "undo-alt");
        model.addAttribute("message", "Här kan du läsa om vår returpolicy och hur du gör reklamationer.");
        model.addAttribute("details", "Vi erbjuder generös returrätt och enkel reklamationsprocess. Detaljerad information om villkor och procedurer kommer snart att finnas tillgänglig här.");
        model.addAttribute("returnUrl", "/info/kontakt");
        return "coming-soon";
    }

    @GetMapping("/kontakt")
    public String contact(Model model) {
        logger.debug("Contact info page requested");
        model.addAttribute("pageTitle", "Kontakta oss - CtrlBuy");
        model.addAttribute("title", "Kontakta oss - CtrlBuy");
        return "contact";
    }

    @GetMapping("/integritet")
    public String integritetspolicy(Model model) {
        logger.debug("Privacy policy page requested");
        model.addAttribute("pageTitle", "Integritetspolicy - CtrlBuy");
        model.addAttribute("feature", "Integritetspolicy");
        model.addAttribute("icon", "user-shield");
        model.addAttribute("message", "Information om hur vi hanterar dina personuppgifter enligt GDPR.");
        model.addAttribute("details", "Vi värnar om din integritet och följer alla GDPR-regler. Detaljerad information om våra rutiner för personuppgifter kommer snart.");
        model.addAttribute("returnUrl", "/info/kontakt");
        return "coming-soon";
    }

    @GetMapping("/allmanna-villkor")
    public String allmännaVillkor(Model model) {
        logger.debug("Terms and conditions page requested");
        model.addAttribute("pageTitle", "Allmänna villkor - CtrlBuy");
        model.addAttribute("feature", "Allmänna villkor");
        model.addAttribute("icon", "file-contract");
        model.addAttribute("message", "Våra allmänna villkor för köp och användning av tjänsten.");
        model.addAttribute("details", "Fullständiga villkor och användaravtal kommer snart att publiceras här för full transparens.");
        model.addAttribute("returnUrl", "/info/kontakt");
        return "coming-soon";
    }
}