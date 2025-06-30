package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminSettingsController {

    @GetMapping("/settings")
    public String adminSettings(Model model) {
        model.addAttribute("pageTitle", "🔧 Systeminställningar");
        model.addAttribute("subtitle", "Administrativ konfiguration och systemhantering");
        model.addAttribute("comingSoonMessage", "Inställningar kommer snart!");
        model.addAttribute("description", "Vi arbetar hårt för att ge dig den bästa administrativa upplevelsen");
        model.addAttribute("progressText", "~85% färdig");
        model.addAttribute("progressValue", "85");
        model.addAttribute("features", new String[]{
                "🔐 Säkerhetsinställningar och användarhantering",
                "📧 E-post och notifikationskonfiguration",
                "💾 Databas och backup-inställningar",
                "🎨 UI/UX anpassningar och branding",
                "📊 Analytics och rapportinställningar",
                "🌍 Internationalisering och språkinställningar"
        });

        return "admin/coming-soon";
    }

    @GetMapping("/logs")
    public String systemLogs(Model model) {
        model.addAttribute("pageTitle", "📋 Systemloggar");
        model.addAttribute("subtitle", "Real-time loggning och systemövervakning");
        model.addAttribute("comingSoonMessage", "Systemloggar kommer snart!");
        model.addAttribute("description", "Vi arbetar hårt för att ge dig den bästa administrativa upplevelsen");
        model.addAttribute("progressText", "~75% färdig");
        model.addAttribute("progressValue", "75");
        model.addAttribute("features", new String[]{
                "📈 Real-time systemloggar och felhantering",
                "🔍 Avancerad sökning och filtrering av loggar",
                "📊 Performance metrics och systemhälsa",
                "⚠️ Automatiska varningar och alerting",
                "📥 Export och backup av loggdata",
                "🕒 Historik och trendanalys"
        });

        return "admin/coming-soon";
    }

    @GetMapping("/email-test")
    public String emailTest(Model model) {
        model.addAttribute("pageTitle", "📧 E-post Testing");
        model.addAttribute("subtitle", "Testa e-postfunktionalitet och mallar");
        model.addAttribute("comingSoonMessage", "E-post testing kommer snart!");
        model.addAttribute("description", "Vi arbetar hårt för att ge dig den bästa administrativa upplevelsen");
        model.addAttribute("progressText", "~90% färdig");
        model.addAttribute("progressValue", "90");
        model.addAttribute("features", new String[]{
                "✉️ Testa orderbekräftelser och receipts",
                "🔔 Notifikationsmallar och personalisering",
                "📝 SMTP-konfiguration och debugging",
                "📊 E-post delivery tracking och analytics",
                "🎨 Mallredigerare för HTML e-post",
                "🧪 A/B testing av e-postmallar"
        });

        return "admin/coming-soon";
    }
}