package com.ctrlbuy.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/diagnostic")
public class DiagnosticController {

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("message", "Diagnostics test is working!");
        return "home";
    }
}
