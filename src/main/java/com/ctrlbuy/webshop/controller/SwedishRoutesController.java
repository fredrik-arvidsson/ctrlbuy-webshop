package com.ctrlbuy.webshop.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class SwedishRoutesController {

    private final CartController cartController;

    @GetMapping("/varukorg")
    public String viewCart(HttpSession session, Model model, Authentication auth) {
        return cartController.viewCart(session, model, auth);
    }
}