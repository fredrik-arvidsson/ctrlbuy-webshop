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
        logger.trace("=== HOME CONTROLLER START ===");
        logger.trace("Request URL: {}", request.getRequestURL());
        logger.trace("Request Method: {}", request.getMethod());
        logger.trace("Request Headers: {}", Collections.list(request.getHeaderNames()));

        try {
            // Sätt grundläggande titel
            model.addAttribute("title", "Hem - CTRL+BUY Solutions");
            logger.trace("Added title to model");

            // Hantera produkter för startsidan
            try {
                logger.trace("Loading featured products...");
                List<Product> featuredProducts = productRepository.findAll()
                        .stream()
                        .limit(6)  // Visa 6 produkter på startsidan
                        .toList();
                model.addAttribute("featuredProducts", featuredProducts);
                logger.trace("Successfully loaded {} featured products", featuredProducts.size());
            } catch (Exception e) {
                logger.warn("Could not load featured products: {}", e.getMessage());
                // Fortsätt utan produkter om det misslyckas
            }

            // Hantera autentisering
            if (authentication != null) {
                logger.trace("Authentication found: {}", authentication.getClass().getSimpleName());
                logger.trace("Is authenticated: {}", authentication.isAuthenticated());
                logger.trace("Principal: {}", authentication.getPrincipal());
                logger.trace("Name: {}", authentication.getName());
                logger.trace("Authorities: {}", authentication.getAuthorities());

                if (authentication.isAuthenticated() &&
                        !authentication.getName().equals("anonymousUser")) {

                    String username = authentication.getName();
                    logger.trace("Setting user as logged in: {}", username);

                    model.addAttribute("isLoggedIn", true);
                    model.addAttribute("username", username);
                    model.addAttribute("user", username); // För Thymeleaf kompatibilitet

                    // Kontrollera admin-status
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(authority -> {
                                String auth = authority.getAuthority();
                                logger.trace("Checking authority: {}", auth);
                                return "ROLE_ADMIN".equals(auth);
                            });

                    logger.trace("User {} is admin: {}", username, isAdmin);
                    model.addAttribute("isAdmin", isAdmin);

                } else {
                    logger.trace("User not authenticated or is anonymous");
                    model.addAttribute("isLoggedIn", false);
                    model.addAttribute("isAdmin", false);
                }
            } else {
                logger.trace("No authentication found");
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("isAdmin", false);
            }

            logger.trace("Model attributes before return: {}", model.asMap().keySet());
            logger.trace("Returning template: home");
            return "home";

        } catch (Exception e) {
            logger.error("=== EXCEPTION IN HOME CONTROLLER ===");
            logger.error("Exception class: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Exception cause: {}", e.getCause() != null ? e.getCause().getMessage() : "No cause");
            logger.error("Full stack trace: ", e);
            logger.error("Model state when error occurred: {}", model.asMap());
            logger.error("Authentication state: {}", authentication != null ? authentication.toString() : "null");
            logger.error("====================================");

            // Rethrow för att Spring ska hantera det
            throw e;

        } finally {
            logger.trace("=== HOME CONTROLLER END ===");
        }
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        logger.debug("Welcome endpoint called, redirecting to home");
        return "redirect:/";
    }

    @GetMapping("/about")
    public String about(Model model) {
        logger.debug("About page requested");
        model.addAttribute("title", "Om oss - CtrlBuy");
        return "about";
    }

    // ❌ TAR BORT DENNA METOD FÖR ATT UNDVIKA KONFLIKT
    // ProductController hanterar nu /products istället
    /*
    @GetMapping("/products")
    public String products(Model model) {
        logger.debug("Products page requested");
        model.addAttribute("title", "Produkter - CtrlBuy");

        try {
            List<Product> allProducts = productRepository.findAll();
            model.addAttribute("products", allProducts);
            logger.debug("Loaded {} products for products page", allProducts.size());
        } catch (Exception e) {
            logger.error("Error loading products: {}", e.getMessage());
            model.addAttribute("products", Collections.emptyList());
        }

        return "products";
    }
    */

    @GetMapping("/min-profil")
    public String minProfil(Authentication authentication, HttpSession session) {
        logger.debug("=== MIN-PROFIL DEBUG ===");
        logger.debug("Username: {}", authentication != null ? authentication.getName() : "null");
        logger.debug("Session userRole: {}", session.getAttribute("userRole"));
        if (authentication != null) {
            logger.debug("Authorities: {}", authentication.getAuthorities());
        }
        logger.debug("========================");

        // Tillfällig lösning: kolla både session och username
        String role = (String) session.getAttribute("userRole");
        boolean isAdmin = "admin".equals(role) ||
                (authentication != null && "admin".equals(authentication.getName()));

        if (isAdmin) {
            logger.debug("Redirecting admin to dashboard");
            return "redirect:/admin/dashboard";
        }

        logger.debug("Redirecting regular user to user profile");
        return "redirect:/user/profil";
    }
}