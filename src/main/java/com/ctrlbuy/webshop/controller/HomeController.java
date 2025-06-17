package com.ctrlbuy.webshop.controller;

import java.util.List;
import java.util.Collections;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.service.ProductService;
import com.ctrlbuy.webshop.model.Product;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication, HttpServletRequest request) {
        try {
            model.addAttribute("title", "Hem - CTRL+BUY Solutions");
            
            // Ladda produkter för startsidan
            List<Product> featuredProducts = productRepository.findAll()
                    .stream()
                    .limit(6)
                    .toList();
            model.addAttribute("featuredProducts", featuredProducts);
            
            // Hantera autentisering
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("username", authentication.getName());
                
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
                model.addAttribute("isAdmin", isAdmin);
            } else {
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("isAdmin", false);
            }
            
            return "home";
            
        } catch (Exception e) {
            logger.error("Error in home controller: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        return "redirect:/";
    }

    @GetMapping("/about") 
    public String about(Model model) {
        model.addAttribute("title", "Om oss - CtrlBuy");
        return "about";
    }

    @GetMapping("/min-profil")
    public String minProfil(Authentication authentication, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        boolean isAdmin = "admin".equals(role) || 
                (authentication != null && "admin".equals(authentication.getName()));
                
        if (isAdmin) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/user/profil";
    }
}
