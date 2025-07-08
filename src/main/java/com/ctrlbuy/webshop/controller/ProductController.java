package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = {"/products", "/produkter"})
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Model model) {

        try {
            List<Product> products;

            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search.trim());
                model.addAttribute("pageTitle", "Sökresultat för: " + search);
                model.addAttribute("searchTerm", search);
            } else if (category != null && !category.trim().isEmpty() && !category.equals("Alla")) {
                products = productService.getProductsByCategory(category);
                model.addAttribute("pageTitle", "Produkter i kategorin: " + category);
                model.addAttribute("selectedCategory", category);
            } else {
                products = productService.getAllProducts();
                model.addAttribute("pageTitle", "Alla produkter");
            }

            model.addAttribute("products", products);
            log.info("✅ Visar {} produkter på produktsidan", products.size());

        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av produkter: {}", e.getMessage());
            model.addAttribute("error", "Ett fel uppstod vid hämtning av produkter.");
            model.addAttribute("products", List.of());
        }

        return "products";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        try {
            Optional<Product> productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Produkten hittades inte.");
                return "redirect:/products";
            }

            Product product = productOpt.get();

            // Lägg till produktdata
            model.addAttribute("product", product);
            model.addAttribute("title", product.getName() + " - CTRL+BUY Solutions");

            // Recensioner (placeholder)
            try {
                model.addAttribute("reviews", List.of());
                model.addAttribute("reviewCount", 0);
                model.addAttribute("averageRating", 0.0);
            } catch (Exception e) {
                model.addAttribute("reviews", List.of());
                model.addAttribute("reviewCount", 0);
                model.addAttribute("averageRating", 0.0);
            }

            // Relaterade produkter - ✅ BEHÅLL BEGRÄNSNING HÄR (bara för relaterade)
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                try {
                    List<Product> relatedProducts = productService.getProductsByCategory(product.getCategory())
                            .stream()
                            .filter(p -> !p.getId().equals(id))
                            .limit(4)  // ✅ BEHÅLL - bara 4 relaterade produkter
                            .toList();
                    model.addAttribute("relatedProducts", relatedProducts);
                } catch (Exception e) {
                    model.addAttribute("relatedProducts", List.of());
                }
            } else {
                model.addAttribute("relatedProducts", List.of());
            }

            // ✅ REA-LOGIK - kompatibel med befintlig databas
            boolean isOnSale = false;
            try {
                // Försök med nya metoder först
                isOnSale = product.isOnSale();
            } catch (Exception e) {
                // Fallback: kolla direktattribut
                isOnSale = product.getOnSale() != null && product.getOnSale();
            }

            model.addAttribute("onSale", isOnSale);

            if (isOnSale) {
                try {
                    // Försök använda nya REA-metoderna först
                    BigDecimal currentPrice = product.getCurrentPrice();
                    BigDecimal originalPrice = product.getOriginalDisplayPrice();
                    BigDecimal savings = product.getSavings();
                    BigDecimal discountPercentage = product.getDiscountPercentage();

                    model.addAttribute("currentPrice", currentPrice);
                    model.addAttribute("originalPrice", originalPrice);
                    model.addAttribute("salePrice", currentPrice);
                    model.addAttribute("savings", savings);
                    model.addAttribute("discountPercentage", discountPercentage);
                    model.addAttribute("saleDescription", product.getSaleDescription());

                } catch (Exception e) {
                    // Fallback: använd grundläggande fält
                    BigDecimal currentPrice = product.getPrice();
                    BigDecimal salePrice = product.getSalePrice();
                    BigDecimal displayPrice = salePrice != null ? salePrice : currentPrice;

                    model.addAttribute("currentPrice", displayPrice);
                    model.addAttribute("originalPrice", product.getOriginalPrice());
                    model.addAttribute("salePrice", displayPrice);
                }
            }

            // Lagerstatus
            Integer stockQty = product.getStockQuantity();
            boolean inStock = stockQty != null && stockQty > 0;
            boolean lowStock = stockQty != null && stockQty < 5 && stockQty > 0;

            model.addAttribute("inStock", inStock);
            model.addAttribute("lowStock", lowStock);

        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av produktdetail för ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ett fel uppstod vid hämtning av produkten.");
            return "redirect:/products";
        }

        return "product-detail";
    }

    // API endpoint för produktsökning (AJAX) - ✅ BEHÅLL BEGRÄNSNING HÄR (för prestanda)
    @GetMapping("/api/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String q) {

        if (q == null || q.trim().length() < 2) {
            return List.of();
        }

        try {
            return productService.searchActiveProducts(q.trim())
                    .stream()
                    .limit(10)  // ✅ BEHÅLL - bara 10 i AJAX-sök för prestanda
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    // Kategori-endpoint
    @GetMapping("/category/{category}")
    public String viewCategory(@PathVariable String category, Model model) {
        return listProducts(category, null, model);
    }

    // ✅ FIXAT REA-SIDA - visar ALLA rea-produkter
    @GetMapping("/sale")
    public String viewSaleProducts(Model model) {
        try {
            // ✅ TA BORT .limit() - visa ALLA rea-produkter
            List<Product> saleProducts = productService.getProductsOnSale();

            model.addAttribute("products", saleProducts);
            model.addAttribute("pageTitle", "Produkter på REA");
            model.addAttribute("selectedCategory", "sale");

            log.info("✅ Visar {} produkter på REA", saleProducts.size());

            // Debug-logging för varje REA-produkt
            if (!saleProducts.isEmpty()) {
                saleProducts.forEach(product -> {
                    try {
                        log.debug("REA-produkt: {} - {}", product.getName(), product.getCurrentPrice());
                    } catch (Exception e) {
                        log.warn("Kunde inte logga REA-produkt: {}", e.getMessage());
                    }
                });
            } else {
                log.warn("Inga REA-produkter hittades");
            }

        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av REA-produkter: {}", e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("error", "Kunde inte ladda rea-produkter");
        }
        return "products";
    }

    // ✅ UPPDATERAD: Populära produkter - kan visa fler än 12
    @GetMapping("/popular")
    public String viewPopularProducts(Model model) {
        try {
            // ✅ ÄNDRAT: Visa alla populära produkter istället för bara 12
            List<Product> popularProducts = productService.getPopularProducts(50); // Ökat från 12 till 50
            model.addAttribute("products", popularProducts);
            model.addAttribute("pageTitle", "Populära produkter");
            model.addAttribute("selectedCategory", "popular");
            log.info("✅ Visar {} populära produkter", popularProducts.size());
        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av populära produkter: {}", e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("error", "Kunde inte ladda populära produkter");
        }
        return "products";
    }

    // ✅ UPPDATERAD: Nya produkter - kan visa fler än 12
    @GetMapping("/new")
    public String viewNewProducts(Model model) {
        try {
            // ✅ ÄNDRAT: Visa alla nya produkter istället för bara 12
            List<Product> newProducts = productService.getNewestProducts(50); // Ökat från 12 till 50
            model.addAttribute("products", newProducts);
            model.addAttribute("pageTitle", "Nya produkter");
            model.addAttribute("selectedCategory", "new");
            log.info("✅ Visar {} nya produkter", newProducts.size());
        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av nya produkter: {}", e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("error", "Kunde inte ladda nya produkter");
        }
        return "products";
    }

    // Kontrollera lagerstatus (AJAX)
    @GetMapping("/{id}/stock")
    @ResponseBody
    public boolean checkStock(@PathVariable Long id) {
        try {
            return productService.getProductByIdWithoutView(id)
                    .map(product -> product.getStockQuantity() != null && product.getStockQuantity() > 0)
                    .orElse(false);
        } catch (Exception e) {
            log.error("❌ Fel vid kontroll av lagerstatus för produkt {}: {}", id, e.getMessage());
            return false;
        }
    }

    // ✅ GRUNDLÄGGANDE REA-STATISTIK (kompatibel med befintlig databas)
    @GetMapping("/sale/stats")
    @ResponseBody
    public String getSaleStats() {
        try {
            List<Product> saleProducts = productService.getProductsOnSale();
            return String.format("REA-statistik: %d produkter på rea", saleProducts.size());
        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av REA-statistik: {}", e.getMessage());
            return "Kunde inte hämta REA-statistik";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("❌ Oväntat fel i ProductController: {}", e.getMessage(), e);
        model.addAttribute("error", "Ett oväntat fel inträffade vid hämtning av produkter.");
        model.addAttribute("products", List.of());
        return "products";
    }
}

// ===============================================
// ✅ LÄGG TILL DENNA CONTROLLER-KLASS SEPARAT
// ===============================================

@Controller
@RequiredArgsConstructor
@Slf4j
class ReaController {

    private final ProductService productService;

    // ✅ NY REA-MAPPING för /rea URL
    @GetMapping("/rea")
    public String getProductsOnSale(Model model, Authentication authentication) {
        log.info("=== REA CONTROLLER START ===");

        try {
            // Hämta alla produkter på rea
            List<Product> saleProducts = productService.getProductsOnSale();
            log.info("Hittade {} produkter på rea", saleProducts.size());

            // Lägg till i model
            model.addAttribute("saleProducts", saleProducts);
            model.addAttribute("title", "REA - Spara pengar på våra bästa produkter!");

            // Beräkna total besparing
            BigDecimal totalSavings = BigDecimal.ZERO;
            for (Product product : saleProducts) {
                if (product.getOriginalPrice() != null && product.getSalePrice() != null) {
                    BigDecimal saving = product.getOriginalPrice().subtract(product.getSalePrice());
                    totalSavings = totalSavings.add(saving);
                } else if (product.getOriginalPrice() != null && product.getPrice() != null) {
                    BigDecimal saving = product.getOriginalPrice().subtract(product.getPrice());
                    totalSavings = totalSavings.add(saving);
                }
            }
            model.addAttribute("totalSavings", totalSavings);

            // Användarinfo om inloggad
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("username", username);
                log.info("REA-sida visad för inloggad användare: {}", username);
            } else {
                model.addAttribute("isLoggedIn", false);
                log.info("REA-sida visad för anonym användare");
            }

            log.info("=== REA CONTROLLER END ===");
            return "rea"; // Returnerar rea.html template

        } catch (Exception e) {
            log.error("Fel vid hämtning av REA-produkter: ", e);
            model.addAttribute("error", "Kunde inte ladda REA-produkter");
            return "error";
        }
    }
}