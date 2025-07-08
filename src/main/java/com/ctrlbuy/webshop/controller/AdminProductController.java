package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products-management")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showAllProducts(Model model) {
        List<Product> allProducts = productService.getAllProducts();

        // 🔍 DEBUG - logga hur många produkter vi faktiskt får
        System.out.println("=== ADMIN PRODUCT DEBUG ===");
        System.out.println("Antal produkter från service: " + allProducts.size());

        if (!allProducts.isEmpty()) {
            System.out.println("Första produkten: " + allProducts.get(0).getName());
            System.out.println("Sista produkten: " + allProducts.get(allProducts.size() - 1).getName());
        }

        // Beräkna statistik
        long totalProducts = allProducts.size();
        long activeProducts = allProducts.stream()
                .filter(Product::isActive)
                .count();
        long onSaleProducts = allProducts.stream()
                .filter(Product::isOnSale)
                .count();
        long lowStockProducts = allProducts.stream()
                .mapToLong(p -> p.getId() % 10 < 3 ? 1 : 0) // Simulerad låg lager-check
                .sum();

        model.addAttribute("products", allProducts);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("onSaleProducts", onSaleProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);

        // 🔍 DEBUG - logga vad som skickas till template
        System.out.println("Skickar " + allProducts.size() + " produkter till template");
        System.out.println("============================");

        return "admin/products/list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        System.out.println("🎯 DEBUG: showAddProductForm called - rendering admin/products/add");

        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("categories", getAvailableCategories());
        model.addAttribute("pageTitle", "Lägg till Ny Produkt");

        return "admin/products/add";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getAvailableCategories());
            model.addAttribute("pageTitle", "Lägg till Ny Produkt");
            return "admin/products/add";
        }

        try {
            // Sätt default värden
            product.setActive(true);
            if (product.getSalePrice() != null && product.getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
                product.setOnSale(true);
            } else {
                product.setOnSale(false);
                product.setSalePrice(null);
            }

            // Spara produkten (använder befintlig save-metod)
            Product savedProduct = productService.saveProduct(product);

            System.out.println("✅ Ny produkt sparad: " + savedProduct.getName() + " (ID: " + savedProduct.getId() + ")");

            redirectAttributes.addFlashAttribute("successMessage",
                    "Produkten '" + product.getName() + "' har lagts till framgångsrikt!");

            return "redirect:/admin/products-management";
        } catch (Exception e) {
            System.out.println("❌ Fel vid sparande av produkt: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ett fel uppstod när produkten skulle sparas: " + e.getMessage());
            return "redirect:/admin/products-management/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // FIX: Optional<Product> conversion error - RAD 120
            Product product = productService.getProductById(id).orElse(null);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten kunde inte hittas!");
                return "redirect:/admin/products-management";
            }

            model.addAttribute("product", product);
            model.addAttribute("categories", getAvailableCategories());
            model.addAttribute("pageTitle", "Redigera Produkt");
            return "admin/products/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ett fel uppstod: " + e.getMessage());
            return "redirect:/admin/products-management";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getAvailableCategories());
            model.addAttribute("pageTitle", "Redigera Produkt");
            return "admin/products/edit";
        }

        try {
            // FIX: Optional<Product> conversion error - RAD 150
            Optional<Product> optionalProduct = productService.getProductById(id);
            if (!optionalProduct.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten kunde inte hittas!");
                return "redirect:/admin/products-management";
            }
            Product existingProduct = optionalProduct.get();

            // Uppdatera befintlig produkt med nya värden
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setOriginalPrice(product.getOriginalPrice());

            // Hantera REA-pris
            if (product.getSalePrice() != null && product.getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
                existingProduct.setSalePrice(product.getSalePrice());
                existingProduct.setOnSale(true);
            } else {
                existingProduct.setSalePrice(null);
                existingProduct.setOnSale(false);
            }

            existingProduct.setActive(product.isActive());

            productService.saveProduct(existingProduct);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Produkten '" + product.getName() + "' har uppdaterats framgångsrikt!");

            return "redirect:/admin/products-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ett fel uppstod när produkten skulle uppdateras: " + e.getMessage());
            return "redirect:/admin/products-management/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            // FIX: Optional<Product> conversion error - RAD 190
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            String productName = product.getName();
            productService.deleteProduct(id);

            System.out.println("🗑️ Produkt borttagen: " + productName + " (ID: " + id + ")");
            return ResponseEntity.ok("Produkten '" + productName + "' har tagits bort framgångsrikt!");

        } catch (Exception e) {
            System.out.println("❌ Fel vid borttagning av produkt: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ett fel uppstod när produkten skulle tas bort: " + e.getMessage());
        }
    }

    @PostMapping("/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleProductStatus(@PathVariable Long id) {
        try {
            // FIX: Optional<Product> conversion error - RAD 211
            Product product = productService.getProductById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().body("Produkten kunde inte hittas!");
            }

            boolean newStatus = !product.isActive();
            product.setActive(newStatus);
            productService.saveProduct(product);

            String status = newStatus ? "aktiverad" : "inaktiverad";
            System.out.println("🔄 Produktstatus ändrad: " + product.getName() + " → " + status);
            return ResponseEntity.ok("Produkten '" + product.getName() + "' har " + status + " framgångsrikt!");

        } catch (Exception e) {
            System.out.println("❌ Fel vid statusändring: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ett fel uppstod när produktstatus skulle ändras: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public String showCategoriesPage(Model model) {
        System.out.println("🏷️ DEBUG: showCategoriesPage called - rendering admin/products/categories");

        List<Product> allProducts = productService.getAllProducts();

        // Räkna produkter per kategori
        Map<String, Long> categoryStats = allProducts.stream()
                .filter(p -> p.getCategory() != null && !p.getCategory().isEmpty())
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        System.out.println("📊 Kategoristatistik: " + categoryStats);

        model.addAttribute("categories", getAvailableCategories());
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("pageTitle", "Hantera Kategorier");

        return "admin/products/categories";
    }

    @PostMapping("/categories")
    public String addCategory(@RequestParam String categoryName,
                              @RequestParam(required = false) String categoryIcon,
                              RedirectAttributes redirectAttributes) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Kategorinamn får inte vara tomt!");
                return "redirect:/admin/products-management/categories";
            }

            String trimmedName = categoryName.trim();

            // Kontrollera om kategorin redan finns
            if (getAvailableCategories().contains(trimmedName)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Kategorin '" + trimmedName + "' finns redan!");
                return "redirect:/admin/products-management/categories";
            }

            // Här skulle vi normalt spara till databas, men för nu loggar vi bara
            System.out.println("➕ Ny kategori skapad: " + trimmedName +
                    (categoryIcon != null ? " (ikon: " + categoryIcon + ")" : ""));

            redirectAttributes.addFlashAttribute("successMessage",
                    "Kategorin '" + trimmedName + "' har lagts till framgångsrikt!");

            return "redirect:/admin/products-management/categories";
        } catch (Exception e) {
            System.out.println("❌ Fel vid skapande av kategori: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ett fel uppstod när kategorin skulle skapas: " + e.getMessage());
            return "redirect:/admin/products-management/categories";
        }
    }

    // API-endpoints för AJAX-anrop och sökning
    @GetMapping("/api/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String query) {
        List<Product> allProducts = productService.getAllProducts();
        String lowerQuery = query.toLowerCase();

        return allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerQuery)) ||
                        (p.getCategory() != null && p.getCategory().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/filter")
    @ResponseBody
    public List<Product> filterProducts(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String status) {
        List<Product> allProducts = productService.getAllProducts();

        return allProducts.stream()
                .filter(p -> category == null || category.isEmpty() ||
                        (p.getCategory() != null && p.getCategory().equals(category)))
                .filter(p -> status == null || status.isEmpty() ||
                        (status.equals("active") && p.isActive() && !p.isOnSale()) ||
                        (status.equals("inactive") && !p.isActive()) ||
                        (status.equals("sale") && p.isOnSale()))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Object> getProductStats() {
        try {
            List<Product> allProducts = productService.getAllProducts();

            var stats = new Object() {
                public final long totalProducts = allProducts.size();
                public final long activeProducts = allProducts.stream().filter(Product::isActive).count();
                public final long onSaleProducts = allProducts.stream().filter(Product::isOnSale).count();
                public final long lowStockProducts = allProducts.stream().mapToLong(p -> p.getId() % 10 < 3 ? 1 : 0).sum();
                public final double totalValue = allProducts.stream()
                        .filter(p -> p.getOriginalPrice() != null)
                        .mapToDouble(p -> p.getOriginalPrice().doubleValue())
                        .sum();
                public final Map<String, Long> categoryBreakdown = allProducts.stream()
                        .filter(p -> p.getCategory() != null)
                        .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
            };

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("❌ Fel vid hämtning av statistik: " + e.getMessage());
            return ResponseEntity.badRequest().body("Kunde inte hämta statistik: " + e.getMessage());
        }
    }

    // Hjälpmetoder
    private List<String> getAvailableCategories() {
        return List.of(
                "Smartphones",
                "Laptops",
                "Tablets",
                "Audio",
                "Gaming",
                "Smartwatches",
                "Accessories",
                "Smart Home",
                "Cameras"
        );
    }

    private boolean isValidCategory(String category) {
        return getAvailableCategories().contains(category);
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}