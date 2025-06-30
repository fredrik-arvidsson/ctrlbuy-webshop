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
    public String root(Model model, Authentication authentication, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.trace("=== ROOT CONTROLLER START ===");
            logger.trace("Request URL: {}", request.getRequestURL());
            logger.trace("Request Method: {}", request.getMethod());

            // ✅ ÄNDRING: Hämta produkter för alla användare (både inloggade och icke-inloggade)
            try {
                logger.trace("Loading featured products for public page...");
                List<Product> featuredProducts = productService.getAllProducts()
                        .stream()
                        .filter(Product::isFeatured) // ✅ Använd isFeatured() metoden från Product
                        .limit(6) // ✅ UPPDATERAT: Visa 6 featured produkter
                        .toList();

                // Om inga featured produkter finns, ta de 6 första produkterna
                if (featuredProducts.isEmpty()) {
                    logger.trace("No featured products found, loading first 6 products instead");
                    featuredProducts = productService.getAllProducts()
                            .stream()
                            .limit(6)
                            .toList();
                }

                model.addAttribute("featuredProducts", featuredProducts);
                logger.trace("Successfully loaded {} featured products", featuredProducts.size());

                // ✅ NY FUNKTIONALITET: Lägg till totalt antal produkter
                int totalProductCount = productService.getAllProducts().size();
                model.addAttribute("totalProductCount", totalProductCount);
                logger.trace("Total products in database: {}", totalProductCount);

                // Debug: logga produktnamn för verifiering
                featuredProducts.forEach(product ->
                        logger.trace("Featured product: ID={}, Name={}, Price={}, Featured={}",
                                product.getId(), product.getName(), product.getCurrentPrice(), product.isFeatured()));

            } catch (Exception e) {
                logger.warn("Could not load featured products: {}", e.getMessage());
                loggingService.logError("loadFeaturedProductsPublic", e);
                // Fortsätt utan produkter om det misslyckas
                model.addAttribute("featuredProducts", Collections.emptyList());
                model.addAttribute("totalProductCount", 0);
            }

            // ÄNDRING: Visa offentlig startsida för icke-inloggade användare
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getName().equals("anonymousUser")) {
                logger.trace("User not authenticated, showing public welcome page");
                loggingService.logUserAction("anonymous", "PUBLIC_PAGE_VIEW",
                        "Anonymous user accessed public welcome page");
                return "home";  // ✅ ÄNDRAT: Visa home istället för welcome-public
            }

            // Användaren är inloggad - omdirigera till dashboard
            logger.trace("User is authenticated: {}, redirecting to home dashboard", authentication.getName());
            String username = getCurrentUsername(authentication);
            loggingService.logUserAction(username, "AUTHENTICATED_ROOT_ACCESS",
                    "Authenticated user accessed root, redirecting to dashboard");
            return "redirect:/home";

        } catch (Exception e) {
            logger.error("Exception in root controller: ", e);
            loggingService.logError("rootController", e);
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("root", duration);
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.trace("=== HOME CONTROLLER START ===");
            logger.trace("Request URL: {}", request.getRequestURL());
            logger.trace("Request Method: {}", request.getMethod());
            logger.trace("Request Headers: {}", Collections.list(request.getHeaderNames()));

            // Kolla om användaren är inloggad
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getName().equals("anonymousUser")) {
                logger.trace("User not authenticated, redirecting to login");
                loggingService.logUserAction("anonymous", "UNAUTHENTICATED_HOME_ACCESS",
                        "Unauthenticated user accessed home URL, redirecting to login");
                return "redirect:/login";
            }

            // Användaren är inloggad - visa home-sidan
            logger.trace("User is authenticated: {}", authentication.getName());

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

                    // ✅ NY FUNKTIONALITET: Lägg till totalt antal produkter för inloggade användare också
                    int totalProductCount = productService.getAllProducts().size();
                    model.addAttribute("totalProductCount", totalProductCount);
                    logger.trace("Total products available: {}", totalProductCount);

                    // Logga featured product views
                    loggingService.logUserAction(username, "FEATURED_PRODUCTS_VIEW",
                            "Viewed " + featuredProducts.size() + " featured products out of " + totalProductCount + " total");

                    // Debug: logga första produktens bildURL
                    if (!featuredProducts.isEmpty()) {
                        logger.trace("First product image URL: {}", featuredProducts.get(0).getImageUrl());
                    }
                } catch (Exception e) {
                    logger.warn("Could not load featured products: {}", e.getMessage());
                    loggingService.logError("loadFeaturedProducts", e);
                    // Fortsätt utan produkter om det misslyckas
                    model.addAttribute("totalProductCount", 0);
                }

                // Hantera autentisering - vi vet redan att användaren är inloggad
                logger.trace("Authentication found: {}", authentication.getClass().getSimpleName());
                logger.trace("Is authenticated: {}", authentication.isAuthenticated());
                logger.trace("Principal: {}", authentication.getPrincipal());
                logger.trace("Name: {}", authentication.getName());
                logger.trace("Authorities: {}", authentication.getAuthorities());

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