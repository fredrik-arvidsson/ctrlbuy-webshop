package com.ctrlbuy.webshop.controller;

import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ctrlbuy.webshop.service.ProductService;
import com.ctrlbuy.webshop.service.LoggingService;
import com.ctrlbuy.webshop.entity.Product;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired(required = false)
    private ProductService productService;

    @Autowired(required = false)
    private LoggingService loggingService;

    /**
     * BULLETPROOF ROOT ENDPOINT - FUNGERAR ALLTID
     */
    @GetMapping("/")
    public String root(Model model, Authentication authentication, HttpServletRequest request) {
        try {
            logger.info("=== BULLETPROOF ROOT START ===");

            // STEG 1: SÄTT ALLA ATTRIBUT FÖRST (GARANTERAR ATT SIDAN LADDAS)
            model.addAttribute("title", "Välkommen till CTRL+BUY Solutions");
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("featuredProducts", Collections.emptyList());
            model.addAttribute("totalProductCount", 0);

            logger.info("✅ Grundläggande attribut satta");

            // STEG 2: KOLLA AUTENTISERING
            boolean isAuthenticated = false;
            try {
                isAuthenticated = authentication != null &&
                        authentication.isAuthenticated() &&
                        !authentication.getName().equals("anonymousUser");
                logger.info("Autentiseringsstatus: {}", isAuthenticated);
            } catch (Exception e) {
                logger.warn("Fel vid autentiseringskontroll: {}", e.getMessage());
            }

            if (isAuthenticated) {
                logger.info("Omdirigerar autentiserad användare till /home");
                return "redirect:/home";
            }

            // STEG 3: FÖRSÖK LADDA PRODUKTER (MEN KRASCHA ALDRIG)
            try {
                if (productService != null) {
                    logger.info("Försöker ladda produkter...");
                    List<Product> allProducts = productService.getAllProducts();

                    if (allProducts != null && !allProducts.isEmpty()) {
                        logger.info("Hittade {} produkter", allProducts.size());

                        // Försök filtrera featured products säkert
                        List<Product> featuredProducts = Collections.emptyList();
                        try {
                            featuredProducts = allProducts.stream()
                                    .filter(product -> {
                                        try {
                                            return product != null && product.isFeatured();
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .limit(6)
                                    .toList();
                        } catch (Exception e) {
                            logger.warn("Fel vid featured-filtrering: {}", e.getMessage());
                        }

                        // Om inga featured, ta första 6
                        if (featuredProducts.isEmpty() && allProducts.size() > 0) {
                            try {
                                featuredProducts = allProducts.stream().limit(6).toList();
                            } catch (Exception e) {
                                logger.warn("Fel vid första-6-filtrering: {}", e.getMessage());
                            }
                        }

                        model.addAttribute("featuredProducts", featuredProducts);
                        model.addAttribute("totalProductCount", allProducts.size());
                        logger.info("✅ Produkter laddade: {} featured av {} totalt", featuredProducts.size(), allProducts.size());
                    } else {
                        logger.warn("Inga produkter hittades");
                    }
                } else {
                    logger.warn("ProductService är null");
                }
            } catch (Exception productError) {
                logger.error("FEL VID PRODUKTLADDNING (men fortsätter): {}", productError.getMessage());
                logger.debug("Full produktfel: ", productError);
            }

            // STEG 4: LOGGA ÅTKOMST (SÄKERT)
            try {
                if (loggingService != null) {
                    loggingService.logUserAction("anonymous", "PUBLIC_PAGE_VIEW", "Anonym användare besökte startsidan");
                }
            } catch (Exception e) {
                logger.warn("Fel vid loggning: {}", e.getMessage());
            }

            logger.info("✅ Returnerar home template för anonym användare");
            return "home";

        } catch (Exception criticalError) {
            logger.error("KRITISKT FEL I ROOT CONTROLLER: ", criticalError);

            // ABSOLUT SISTA UTVÄG - GARANTERAD FUNKTION
            try {
                model.addAttribute("title", "CTRL+BUY Solutions");
                model.addAttribute("featuredProducts", Collections.emptyList());
                model.addAttribute("totalProductCount", 0);
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("isAdmin", false);
                model.addAttribute("error", "Systemet startar upp - försök igen om ett ögonblick");

                return "home";
            } catch (Exception finalError) {
                logger.error("ÄVEN FALLBACK MISSLYCKADES: ", finalError);
                throw new RuntimeException("Totalt systemfel", finalError);
            }
        }
    }

    /**
     * DEBUG ENDPOINT - TESTA OM CONTROLLER FUNGERAR
     */
    @GetMapping("/test-simple")
    @ResponseBody
    public String testSimple() {
        return "SUCCESS: HomeController fungerar! Tid: " + System.currentTimeMillis();
    }

    /**
     * DEBUG ENDPOINT - TESTA TEMPLATE UTAN PRODUKTER
     */
    @GetMapping("/test-home")
    public String testHome(Model model) {
        try {
            model.addAttribute("title", "TEST - CTRL+BUY Solutions");
            model.addAttribute("featuredProducts", Collections.emptyList());
            model.addAttribute("totalProductCount", 0);
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("isAdmin", false);

            logger.info("✅ Test home template med tom data");
            return "home";

        } catch (Exception e) {
            logger.error("Fel i test-home: ", e);
            throw e;
        }
    }

    /**
     * DEBUG ENDPOINT - TESTA PRODUKTSERVICE
     */
    @GetMapping("/test-products")
    @ResponseBody
    public Map<String, Object> testProducts() {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("productServiceNull", productService == null);

            if (productService != null) {
                List<Product> products = productService.getAllProducts();
                result.put("productCount", products != null ? products.size() : 0);
                result.put("success", true);

                if (products != null && !products.isEmpty()) {
                    Product first = products.get(0);
                    result.put("firstProductName", first.getName());
                    result.put("firstProductFeatured", first.isFeatured());
                }
            } else {
                result.put("error", "ProductService är null");
                result.put("success", false);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exception", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * SÄKER HOME ENDPOINT FÖR INLOGGADE
     */
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        try {
            logger.info("=== HOME ENDPOINT ===");

            // Kontrollera autentisering
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getName().equals("anonymousUser")) {
                logger.warn("Ej autentiserad åtkomst till /home");
                return "redirect:/login?error=Du måste logga in först";
            }

            String username = authentication.getName();
            logger.info("Autentiserad användare: {}", username);

            // Sätt grundattribut
            model.addAttribute("title", "Hem - CTRL+BUY Solutions");
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", username);
            model.addAttribute("user", username);

            // Kontrollera admin-status säkert
            boolean isAdmin = false;
            try {
                isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            } catch (Exception e) {
                logger.warn("Fel vid admin-kontroll: {}", e.getMessage());
            }
            model.addAttribute("isAdmin", isAdmin);

            // Ladda produkter säkert
            try {
                if (productService != null) {
                    List<Product> products = productService.getAllProducts();
                    if (products != null) {
                        List<Product> featured = products.stream().limit(6).toList();
                        model.addAttribute("featuredProducts", featured);
                        model.addAttribute("totalProductCount", products.size());
                    } else {
                        model.addAttribute("featuredProducts", Collections.emptyList());
                        model.addAttribute("totalProductCount", 0);
                    }
                } else {
                    model.addAttribute("featuredProducts", Collections.emptyList());
                    model.addAttribute("totalProductCount", 0);
                }
            } catch (Exception e) {
                logger.warn("Fel vid produktladdning för inloggad användare: {}", e.getMessage());
                model.addAttribute("featuredProducts", Collections.emptyList());
                model.addAttribute("totalProductCount", 0);
            }

            logger.info("✅ Returnerar home för autentiserad användare");
            return "home";

        } catch (Exception e) {
            logger.error("Fel i home endpoint: ", e);

            // Fallback för inloggade
            model.addAttribute("title", "Hem - CTRL+BUY Solutions");
            model.addAttribute("featuredProducts", Collections.emptyList());
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("isAdmin", false);

            return "home";
        }
    }

    // ÖVRIGA ENDPOINTS (SÄKRA VERSIONER)

    @GetMapping("/welcome")
    public String welcome() {
        return "redirect:/";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Om oss - CTRL+BUY Solutions");
        return "about";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("title", "Integritetspolicy - CTRL+BUY Solutions");
        return "coming-soon";
    }
}