package com.ctrlbuy.webshop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping("/")
    public String welcome() {
        return "Välkommen till CtrlBuy Webshop!";
    }

    @GetMapping("/info")
    public String info() {
        return "Detta är en informations-sida";
    }
}