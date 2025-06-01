package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = {"/products", "/produkter"}) // Hanterar både engelska och svenska URL:er
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // Produktlista med filter och sortering
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "false") boolean onSale,
            Model model) {

        log.info("=== PRODUKTCONTROLLER DEBUG START ===");
        log.info("Request mapping: /products");
        log.info("Method: listProducts");
        log.info("Parameters - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        log.info("Parameters - category: {}, search: {}, onSale: {}", category, search, onSale);

        try {
            // Skapa sortering
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Product> productPage;
            List<Product> products;

            // Filtrera baserat på sökparametrar
            if (onSale) {
                // Visa endast produkter på rea
                products = productService.getProductsOnSale();
                // Konvertera till Page för konsistens
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), products.size());
                List<Product> pageContent = start < products.size() ?
                        products.subList(start, end) : List.of();
                productPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, products.size());
                model.addAttribute("pageTitle", "Produkter på rea");
                log.info("Loading products on sale: {} products found", products.size());
            } else if (search != null && !search.trim().isEmpty()) {
                // Sökresultat
                productPage = productService.searchProducts(search.trim(), pageable);
                model.addAttribute("pageTitle", "Sökresultat för: " + search);
                model.addAttribute("searchTerm", search);
                log.info("Searching for products with query: {}, found: {} products", search, productPage.getTotalElements());
            } else if (category != null && !category.trim().isEmpty()) {
                // Filtrera per kategori
                productPage = productService.findByCategory(category, pageable);
                model.addAttribute("pageTitle", "Produkter i kategorin: " + category);
                model.addAttribute("selectedCategory", category);
                log.info("Filtering products by category: {}, found: {} products", category, productPage.getTotalElements());
            } else {
                // Alla produkter
                productPage = productService.findAllActive(pageable);
                model.addAttribute("pageTitle", "Alla produkter");
                log.info("Loading all active products, found: {} products", productPage.getTotalElements());
            }

            // Lägg till produktdata
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalElements", productPage.getTotalElements());

            // Lägg till kategorier för filter
            model.addAttribute("categories", productService.getAllCategories());

            // Lägg till populära och nya produkter för sidebar
            model.addAttribute("popularProducts", productService.getPopularProducts(5));
            model.addAttribute("newestProducts", productService.getNewestProducts(5));

            // Filterparametrar
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("inStock", inStock);
            model.addAttribute("onSale", onSale);

            log.info("Products loaded successfully: {} products on page {} of {}",
                    productPage.getContent().size(), page + 1, productPage.getTotalPages());
            log.info("Model attributes added: {}", model.asMap().keySet());
            log.info("About to return template: products/list");
            log.info("=== PRODUKTCONTROLLER DEBUG END ===");

        } catch (Exception e) {
            log.error("ERROR in listProducts: ", e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av produkter.");
            model.addAttribute("products", List.of());
            model.addAttribute("categories", List.of());
            log.info("Exception occurred, returning template: products/list");
        }

        String templateName = "products/list";
        log.info("FINAL: Returning template name: {}", templateName);
        return templateName;
    }

    // Produktdetaljer
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Visar produktdetaljer för ID: {}", id);

        try {
            Optional<Product> productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                log.warn("Produkt med ID {} hittades inte", id);
                redirectAttributes.addFlashAttribute("error", "Produkten hittades inte.");
                return "redirect:/products";
            }

            Product product = productOpt.get();
            model.addAttribute("product", product);

            // Lägg till relaterade produkter
            if (product.getCategory() != null) {
                List<Product> relatedProducts = productService.getProductsByCategory(product.getCategory())
                        .stream()
                        .filter(p -> !p.getId().equals(id))
                        .limit(4)
                        .toList();
                model.addAttribute("relatedProducts", relatedProducts);
            }

            // TEMPORÄR LÖSNING: Simulera rea och rabatt tills sale_price kolumn läggs till
            boolean isOnSale = product.getName().toLowerCase().contains("rea") ||
                    product.getName().toLowerCase().contains("sale") ||
                    product.getDescription() != null && product.getDescription().toLowerCase().contains("kampanj");

            if (isOnSale) {
                // Simulera 20% rabatt
                BigDecimal salePrice = product.getPrice().multiply(new BigDecimal("0.8"));
                BigDecimal savings = product.getPrice().subtract(salePrice);
                BigDecimal discountPercentage = new BigDecimal("20");

                model.addAttribute("discountPercentage", discountPercentage);
                model.addAttribute("savings", savings);
                model.addAttribute("salePrice", salePrice);
            }

            // Lagerstatus
            boolean inStock = product.getStockQuantity() != null && product.getStockQuantity() > 0;
            boolean lowStock = product.getStockQuantity() != null && product.getStockQuantity() < 5 && product.getStockQuantity() > 0;

            model.addAttribute("inStock", inStock);
            model.addAttribute("lowStock", lowStock);
            model.addAttribute("onSale", isOnSale);

            log.debug("Displaying product: {} (ID: {})", product.getName(), id);

        } catch (Exception e) {
            log.error("Error loading product with id {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ett fel uppstod vid hämtning av produkten.");
            return "redirect:/products";
        }

        return "products/detail";
    }

    // API endpoint för produktsökning (AJAX)
    @GetMapping("/api/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String q) {
        log.debug("API-sökning efter: {}", q);

        if (q == null || q.trim().length() < 2) {
            return List.of();
        }

        return productService.searchActiveProducts(q.trim())
                .stream()
                .limit(10)
                .toList();
    }

    // Kategori-endpoint
    @GetMapping("/category/{category}")
    public String viewCategory(@PathVariable String category, Model model) {
        return listProducts(0, 12, "name", "asc", category, null, null, null, null, false, model);
    }

    // Rea-sida
    @GetMapping("/sale")
    public String viewSaleProducts(Model model) {
        return listProducts(0, 12, "price", "asc", null, null, null, null, null, true, model);
    }

    // Populära produkter
    @GetMapping("/popular")
    public String viewPopularProducts(Model model) {
        log.debug("Visar populära produkter");

        try {
            List<Product> popularProducts = productService.getPopularProducts(20);
            model.addAttribute("products", popularProducts);
            model.addAttribute("pageTitle", "Populära produkter");
            model.addAttribute("categories", productService.getAllCategories());

            // Lägg till tomma pagination-värden för att undvika template-fel
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalElements", (long) popularProducts.size());

        } catch (Exception e) {
            log.error("Error loading popular products: {}", e.getMessage(), e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av populära produkter.");
            model.addAttribute("products", List.of());
        }

        return "products/list";
    }

    // Nya produkter
    @GetMapping("/new")
    public String viewNewProducts(Model model) {
        log.debug("Visar nya produkter");

        try {
            List<Product> newProducts = productService.getNewestProducts(20);
            model.addAttribute("products", newProducts);
            model.addAttribute("pageTitle", "Nya produkter");
            model.addAttribute("categories", productService.getAllCategories());

            // Lägg till tomma pagination-värden
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalElements", (long) newProducts.size());

        } catch (Exception e) {
            log.error("Error loading new products: {}", e.getMessage(), e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av nya produkter.");
            model.addAttribute("products", List.of());
        }

        return "products/list";
    }

    // Quick view för modal (AJAX)
    @GetMapping("/{id}/quick-view")
    @ResponseBody
    public Product quickViewProduct(@PathVariable Long id) {
        log.debug("Quick view för produkt: {}", id);

        return productService.getProductByIdWithoutView(id)
                .orElse(null);
    }

    // Kontrollera lagerstatus (AJAX)
    @GetMapping("/{id}/stock")
    @ResponseBody
    public boolean checkStock(@PathVariable Long id) {
        return productService.getProductByIdWithoutView(id)
                .map(product -> product.getStockQuantity() != null && product.getStockQuantity() > 0)
                .orElse(false);
    }

    // ÄLDRE API ENDPOINT FÖR BAKÅTKOMPATIBILITET
    @GetMapping("/api")
    @ResponseBody
    public Page<Product> getProductsApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        log.debug("API call for products - page: {}, category: {}, search: {}", page, category, search);

        try {
            Pageable pageable = PageRequest.of(page, size);

            if (search != null && !search.trim().isEmpty()) {
                return productService.searchProducts(search.trim(), pageable);
            } else if (category != null && !category.trim().isEmpty()) {
                return productService.findByCategory(category, pageable);
            } else {
                return productService.findAllActive(pageable);
            }
        } catch (Exception e) {
            log.error("Error in products API: {}", e.getMessage(), e);
            // Returnera tom Page vid fel
            return Page.empty();
        }
    }

    // Error handling
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("Fel i ProductController", e);
        model.addAttribute("error", "Ett oväntat fel inträffade.");
        model.addAttribute("products", List.of());
        model.addAttribute("categories", List.of());
        return "products/list";
    }
}