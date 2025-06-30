package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        log.info("=== PRODUKTCONTROLLER DEBUG START ===");
        log.info("Parameters - category: {}, search: {}", category, search);

        try {
            List<Product> products;

            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search.trim());
                model.addAttribute("pageTitle", "Sökresultat för: " + search);
                model.addAttribute("searchTerm", search);
                log.info("Searching for products with query: {}, found: {} products", search, products.size());
            } else if (category != null && !category.trim().isEmpty() && !category.equals("Alla")) {
                products = productService.getProductsByCategory(category);
                model.addAttribute("pageTitle", "Produkter i kategorin: " + category);
                model.addAttribute("selectedCategory", category);
                log.info("Filtering products by category: {}, found: {} products", category, products.size());
            } else {
                products = productService.getAllProducts();
                model.addAttribute("pageTitle", "Alla produkter");
                log.info("Loading all products, found: {} products", products.size());
            }

            model.addAttribute("products", products);
            log.info("Products loaded successfully: {} products", products.size());

        } catch (Exception e) {
            log.error("ERROR in listProducts: ", e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av produkter.");
            model.addAttribute("products", List.of());
        }

        log.info("=== PRODUKTCONTROLLER DEBUG END ===");
        return "products";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        log.info("=== PRODUKTDETALJER DEBUG START ===");
        log.info("Visar produktdetaljer för ID: {}", id);

        try {
            Optional<Product> productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                log.warn("Produkt med ID {} hittades inte", id);
                redirectAttributes.addFlashAttribute("error", "Produkten hittades inte.");
                return "redirect:/products";
            }

            Product product = productOpt.get();
            log.info("Produkt hittad: {} (ID: {})", product.getName(), product.getId());

            // Lägg till produktdata
            model.addAttribute("product", product);
            model.addAttribute("title", product.getName() + " - CTRL+BUY Solutions");

            // Recensioner (placeholder)
            try {
                model.addAttribute("reviews", List.of());
                model.addAttribute("reviewCount", 0);
                model.addAttribute("averageRating", 0.0);
                log.debug("Recensionsdata initialiserad");
            } catch (Exception e) {
                log.warn("Could not load reviews for product {}: {}", product.getName(), e.getMessage());
                model.addAttribute("reviews", List.of());
                model.addAttribute("reviewCount", 0);
                model.addAttribute("averageRating", 0.0);
            }

            // Relaterade produkter
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                try {
                    List<Product> relatedProducts = productService.getProductsByCategory(product.getCategory())
                            .stream()
                            .filter(p -> !p.getId().equals(id))
                            .limit(4)
                            .toList();
                    model.addAttribute("relatedProducts", relatedProducts);
                    log.info("Found {} related products för kategori: {}", relatedProducts.size(), product.getCategory());
                } catch (Exception e) {
                    log.warn("Could not load related products: {}", e.getMessage());
                    model.addAttribute("relatedProducts", List.of());
                }
            } else {
                log.warn("Ingen kategori för produkt {}, inga relaterade produkter", product.getName());
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

                    log.info("Produkt på rea: {} - Ordinarie: {} kr, Rea: {} kr, Rabatt: {}%",
                            product.getName(), originalPrice, currentPrice, discountPercentage);
                } catch (Exception e) {
                    // Fallback: använd grundläggande fält
                    log.warn("Använder fallback REA-logik för {}: {}", product.getName(), e.getMessage());

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

            log.info("Lagerstatus - I lager: {}, Lågt lager: {}, Antal: {}",
                    inStock, lowStock, stockQty);
            log.info("Produktdetaljer redo för visning: {}", product.getName());

        } catch (Exception e) {
            log.error("Error loading product with id {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ett fel uppstod vid hämtning av produkten.");
            return "redirect:/products";
        }

        log.info("=== PRODUKTDETALJER DEBUG END ===");
        return "product-detail";
    }

    // API endpoint för produktsökning (AJAX)
    @GetMapping("/api/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String q) {
        log.debug("API-sökning efter: {}", q);

        if (q == null || q.trim().length() < 2) {
            return List.of();
        }

        try {
            return productService.searchActiveProducts(q.trim())
                    .stream()
                    .limit(10)
                    .toList();
        } catch (Exception e) {
            log.warn("Search method error: {}", e.getMessage());
            return List.of();
        }
    }

    // Kategori-endpoint
    @GetMapping("/category/{category}")
    public String viewCategory(@PathVariable String category, Model model) {
        return listProducts(category, null, model);
    }

    // ✅ FIXAT REA-SIDA - använder den uppdaterade getProductsOnSale() metoden
    @GetMapping("/sale")
    public String viewSaleProducts(Model model) {
        log.info("=== REA-PRODUKTER DEBUG START ===");
        try {
            // ✅ ANVÄND den fixade metoden från ProductService
            List<Product> saleProducts = productService.getProductsOnSale();

            model.addAttribute("products", saleProducts);
            model.addAttribute("pageTitle", "Produkter på REA");
            model.addAttribute("selectedCategory", "sale");

            log.info("✅ REA-CONTROLLER: Found {} products on sale", saleProducts.size());

            // Debug-logging för varje REA-produkt
            if (!saleProducts.isEmpty()) {
                saleProducts.forEach(product -> {
                    try {
                        log.debug("REA-produkt: {} - Ordinarie: {} kr, REA: {} kr",
                                product.getName(),
                                product.getOriginalPrice(),
                                product.getPrice());
                    } catch (Exception e) {
                        log.debug("REA-produkt: {} - Basic info", product.getName());
                    }
                });
            } else {
                log.warn("✅ REA-CONTROLLER: Inga REA-produkter hittades!");
            }

        } catch (Exception e) {
            log.error("✅ REA-CONTROLLER: Error loading sale products: {}", e.getMessage(), e);
            model.addAttribute("products", List.of());
            model.addAttribute("error", "Kunde inte ladda rea-produkter");
        }
        log.info("=== REA-PRODUKTER DEBUG END ===");
        return "products";
    }

    // Populära produkter
    @GetMapping("/popular")
    public String viewPopularProducts(Model model) {
        log.debug("Loading popular products");
        try {
            List<Product> popularProducts = productService.getPopularProducts(12);
            model.addAttribute("products", popularProducts);
            model.addAttribute("pageTitle", "Populära produkter");
            model.addAttribute("selectedCategory", "popular");
            log.info("Found {} popular products", popularProducts.size());
        } catch (Exception e) {
            log.error("Error loading popular products: {}", e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("error", "Kunde inte ladda populära produkter");
        }
        return "products";
    }

    // Nya produkter
    @GetMapping("/new")
    public String viewNewProducts(Model model) {
        log.debug("Loading new products");
        try {
            List<Product> newProducts = productService.getNewestProducts(12);
            model.addAttribute("products", newProducts);
            model.addAttribute("pageTitle", "Nya produkter");
            model.addAttribute("selectedCategory", "new");
            log.info("Found {} new products", newProducts.size());
        } catch (Exception e) {
            log.error("Error loading new products: {}", e.getMessage());
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
            log.error("Error checking stock for product {}: {}", id, e.getMessage());
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
            log.error("Error getting sale stats: {}", e.getMessage());
            return "Kunde inte hämta REA-statistik";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("Fel i ProductController: {}", e.getMessage(), e);
        model.addAttribute("error", "Ett oväntat fel inträffade vid hämtning av produkter.");
        model.addAttribute("products", List.of());
        return "products";
    }
}