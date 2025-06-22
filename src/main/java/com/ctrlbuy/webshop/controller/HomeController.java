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
import com.ctrlbuy.webshop.service.LoggingService;
import com.ctrlbuy.webshop.model.Product;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.trace("=== HOME CONTROLLER START ===");
            logger.trace("Request URL: {}", request.getRequestURL());
            logger.trace("Request Method: {}", request.getMethod());
            logger.trace("Request Headers: {}", Collections.list(request.getHeaderNames()));

            // Logga page view
            String username = getCurrentUsername(authentication);
            loggingService.logUserAction(username, "PAGE_VIEW", "Home page accessed");

            try {
                // Sätt grundläggande titel
                model.addAttribute("title", "Hem - CTRL+BUY Solutions");
                logger.trace("Added title to model");

                // Hantera produkter för startsidan - NU MED PRODUCTSERVICE!
                try {
                    logger.trace("Loading featured products with ProductService...");
                    // ✅ ÄNDRING: Använd ProductService istället för ProductRepository
                    List<Product> featuredProducts = productService.getAllProducts()
                            .stream()
                            .limit(6)  // Visa 6 produkter på startsidan
                            .toList();
                    model.addAttribute("featuredProducts", featuredProducts);
                    logger.trace("Successfully loaded {} featured products with images", featuredProducts.size());

                    // Logga featured product views
                    loggingService.logUserAction(username, "FEATURED_PRODUCTS_VIEW",
                            "Viewed " + featuredProducts.size() + " featured products");

                    // Debug: logga första produktens bildURL
                    if (!featuredProducts.isEmpty()) {
                        logger.trace("First product image URL: {}", featuredProducts.get(0).getImageUrl());
                    }
                } catch (Exception e) {
                    logger.warn("Could not load featured products: {}", e.getMessage());
                    loggingService.logError("loadFeaturedProducts", e);
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

                        String authenticatedUsername = authentication.getName();
                        logger.trace("Setting user as logged in: {}", authenticatedUsername);

                        model.addAttribute("isLoggedIn", true);
                        model.addAttribute("username", authenticatedUsername);
                        model.addAttribute("user", authenticatedUsername); // För Thymeleaf kompatibilitet

                        // Logga authenticated user activity
                        loggingService.logUserAction(authenticatedUsername, "AUTHENTICATED_ACCESS",
                                "Authenticated user accessed home page");

                        // Kontrollera admin-status
                        boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(authority -> {
                                    String auth = authority.getAuthority();
                                    logger.trace("Checking authority: {}", auth);
                                    return "ROLE_ADMIN".equals(auth);
                                });

                        logger.trace("User {} is admin: {}", authenticatedUsername, isAdmin);
                        model.addAttribute("isAdmin", isAdmin);

                        if (isAdmin) {
                            loggingService.logUserAction(authenticatedUsername, "ADMIN_ACCESS",
                                    "Admin user accessed home page");
                        }

                    } else {
                        logger.trace("User not authenticated or is anonymous");
                        model.addAttribute("isLoggedIn", false);
                        model.addAttribute("isAdmin", false);
                        loggingService.logUserAction("anonymous", "ANONYMOUS_ACCESS",
                                "Anonymous user accessed home page");
                    }
                } else {
                    logger.trace("No authentication found");
                    model.addAttribute("isLoggedIn", false);
                    model.addAttribute("isAdmin", false);
                    loggingService.logUserAction("anonymous", "UNAUTHENTICATED_ACCESS",
                            "Unauthenticated user accessed home page");
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

                // Logga error med monitoring
                loggingService.logError("homeController", e);

                // Rethrow för att Spring ska hantera det
                throw e;

            } finally {
                logger.trace("=== HOME CONTROLLER END ===");
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logPerformance("home", duration);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("home", duration);
            throw e;
        }
    }

    @GetMapping("/welcome")
    public String welcome(Authentication authentication) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Welcome endpoint called, redirecting to home");
            String username = getCurrentUsername(authentication);
            loggingService.logUserAction(username, "WELCOME_REDIRECT", "User accessed welcome endpoint");
            return "redirect:/";
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("welcome", duration);
        }
    }

    @GetMapping("/about")
    public String about(Model model, Authentication authentication) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("About page requested");
            String username = getCurrentUsername(authentication);
            model.addAttribute("title", "Om oss - CtrlBuy");

            loggingService.logUserAction(username, "PAGE_VIEW", "About page accessed");

            return "about";
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("about", duration);
        }
    }

    @GetMapping("/privacy")
    public String privacy(Model model, Authentication authentication) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Privacy page requested");
            String username = getCurrentUsername(authentication);
            model.addAttribute("title", "Integritetspolicy - CTRL+BUY Solutions");

            loggingService.logUserAction(username, "PAGE_VIEW", "Privacy page accessed");

            return "coming-soon";
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("privacy", duration);
        }
    }

    @GetMapping("/min-profil")
    public String minProfil(Authentication authentication, HttpSession session) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("=== MIN-PROFIL DEBUG ===");
            String username = getCurrentUsername(authentication);
            logger.debug("Username: {}", username);
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
                loggingService.logUserAction(username, "ADMIN_REDIRECT", "Admin redirected to dashboard");
                return "redirect:/admin/dashboard";
            }

            logger.debug("Redirecting regular user to user profile");
            loggingService.logUserAction(username, "USER_REDIRECT", "User redirected to profile");
            return "redirect:/user/profil";

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("minProfil", duration);
        }
    }

    // Hjälpmetod för att få aktuellt användarnamn
    private String getCurrentUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "anonymous";
    }
}