package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * Handles the root URL and redirects to welcome page
     * @return the welcome view name
     */
    @GetMapping("/")
    public String home() {
        return "welcome";
    }

    /**
     * Explicit mapping for welcome page
     * @return the welcome view name
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}