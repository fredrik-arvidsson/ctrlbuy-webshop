package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final ProductService productService;
    private static final String CART_SESSION_KEY = "shopping_cart";

    // Visa kundvagn - ENGELSKA URL
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model, Authentication auth) {
        return viewCartSwedish(session, model, auth);
    }

    // SVENSK URL - DIREKT MAPPING (inte under /cart)
    @GetMapping("/varukorg")
    public String viewCartSwedish(HttpSession session, Model model, Authentication auth) {
        log.debug("Visar kundvagn via svensk URL för användare: {}",
                auth != null ? auth.getName() : "anonym");

        try {
            List<CartItem> cartItems = getCartItemsFromSession(session);

            // VIKTIGT: Beräkna totaler för templaten
            BigDecimal subtotal = cartItems.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Frakt: 49 kr om under 499 kr, annars gratis
            BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0 ?
                    BigDecimal.ZERO : new BigDecimal("49");

            BigDecimal total = subtotal.add(shipping);

            int cartItemCount = cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            // Lägg till EXAKT det som templaten förväntar sig
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);      // För templaten
            model.addAttribute("shipping", shipping);      // För templaten
            model.addAttribute("total", total);           // För templaten
            model.addAttribute("cartTotal", total);       // Backup
            model.addAttribute("cartItemCount", cartItemCount);

            log.debug("Cart contains {} items, subtotal: {} kr, shipping: {} kr, total: {} kr",
                    cartItemCount, subtotal, shipping, total);

        } catch (Exception e) {
            log.error("Error loading cart", e);
            model.addAttribute("errorMessage", "Ett fel uppstod vid hämtning av kundvagnen.");
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("subtotal", BigDecimal.ZERO);
            model.addAttribute("shipping", BigDecimal.ZERO);
            model.addAttribute("total", BigDecimal.ZERO);
            model.addAttribute("cartItemCount", 0);
        }

        return "cart/view";
    }

    // Lägg till produkt i kundvagn - STÖDER BÅDE FORM OCH AJAX
    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        log.debug("Lägger till produkt {} i kundvagn, kvantitet: {}", productId, quantity);

        try {
            Optional<Product> productOpt = productService.getProductByIdWithoutView(productId);

            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten hittades inte");
                return "redirect:/varukorg";
            }

            Product product = productOpt.get();

            // Kontrollera lager
            if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Otillräckligt lager. Endast " + (product.getStockQuantity() != null ? product.getStockQuantity() : 0) + " tillgängliga");
                return "redirect:/produkter"; // Tillbaka till produktsidan så man kan fortsätta handla
            }

            // Lägg till i kundvagn
            List<CartItem> cartItems = getCartItemsFromSession(session);

            // Hitta befintlig item eller skapa ny
            CartItem existingItem = cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > product.getStockQuantity()) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Kan inte lägga till fler. Max " + product.getStockQuantity() + " tillgängliga");
                    return "redirect:/produkter"; // Tillbaka till produktsidan
                }
                existingItem.setQuantity(newQuantity);
                existingItem.updatePrice();
            } else {
                cartItems.add(new CartItem(product, quantity));
            }

            // Spara i session
            session.setAttribute(CART_SESSION_KEY, cartItems);

            redirectAttributes.addFlashAttribute("successMessage",
                    product.getName() + " har lagts till i kundvagnen");

            log.info("Produkt {} tillagd i kundvagn", product.getName());

        } catch (Exception e) {
            log.error("Fel vid tillägg i kundvagn för produkt: " + productId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ett fel uppstod vid tillägg i kundvagn");
        }

        // VIKTIGT: Redirect till produktsidan så man kan fortsätta handla
        return "redirect:/produkter";
    }

    // Uppdatera kvantitet - FORM-BASERAD
    @PostMapping("/cart/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        log.debug("Uppdaterar kvantitet för produkt {} till {}", productId, quantity);

        try {
            if (quantity <= 0) {
                return removeFromCart(productId, session, redirectAttributes);
            }

            List<CartItem> cartItems = getCartItemsFromSession(session);

            CartItem item = cartItems.stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (item == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten finns inte i kundvagnen");
                return "redirect:/varukorg";
            }

            // Kontrollera lager
            Optional<Product> productOpt = productService.getProductByIdWithoutView(productId);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten hittades inte");
                return "redirect:/varukorg";
            }

            Product product = productOpt.get();
            if (quantity > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Otillräckligt lager. Max " + product.getStockQuantity() + " tillgängliga");
                return "redirect:/varukorg";
            }

            // Uppdatera kvantitet
            item.setQuantity(quantity);
            item.updatePrice();

            session.setAttribute(CART_SESSION_KEY, cartItems);

            redirectAttributes.addFlashAttribute("successMessage", "Kvantiteten har uppdaterats");

        } catch (Exception e) {
            log.error("Fel vid uppdatering av kundvagn för produkt: " + productId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ett fel uppstod vid uppdatering");
        }

        return "redirect:/varukorg";
    }

    // Ta bort från kundvagn - FORM-BASERAD
    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        log.debug("Tar bort produkt {} från kundvagn", productId);

        try {
            List<CartItem> cartItems = getCartItemsFromSession(session);

            boolean removed = cartItems.removeIf(item -> item.getProduct().getId().equals(productId));

            if (removed) {
                session.setAttribute(CART_SESSION_KEY, cartItems);
                redirectAttributes.addFlashAttribute("successMessage", "Produkten har tagits bort från kundvagnen");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Produkten fanns inte i kundvagnen");
            }

        } catch (Exception e) {
            log.error("Fel vid borttagning från kundvagn för produkt: " + productId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ett fel uppstod vid borttagning");
        }

        return "redirect:/varukorg";
    }

    // Rensa kundvagn
    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        log.debug("Rensar kundvagn");

        try {
            session.removeAttribute(CART_SESSION_KEY);
            redirectAttributes.addFlashAttribute("successMessage", "Kundvagnen har rensats");

        } catch (Exception e) {
            log.error("Fel vid rensning av kundvagn", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ett fel uppstod vid rensning av kundvagn");
        }

        return "redirect:/varukorg";
    }

    // AJAX-ENDPOINTS FÖR MODERN FUNKTIONALITET

    // Lägg till produkt i kundvagn (AJAX)
    @PostMapping("/cart/add/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCartAjax(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Product> productOpt = productService.getProductByIdWithoutView(productId);

            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Produkten hittades inte");
                return ResponseEntity.badRequest().body(response);
            }

            Product product = productOpt.get();

            if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                response.put("success", false);
                response.put("message", "Otillräckligt lager");
                response.put("availableStock", product.getStockQuantity());
                return ResponseEntity.badRequest().body(response);
            }

            List<CartItem> cartItems = getCartItemsFromSession(session);

            CartItem existingItem = cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > product.getStockQuantity()) {
                    response.put("success", false);
                    response.put("message", "Kan inte lägga till fler. Max " + product.getStockQuantity() + " tillgängliga");
                    return ResponseEntity.badRequest().body(response);
                }
                existingItem.setQuantity(newQuantity);
                existingItem.updatePrice();
            } else {
                cartItems.add(new CartItem(product, quantity));
            }

            session.setAttribute(CART_SESSION_KEY, cartItems);

            // Beräkna nya totaler
            int totalItems = cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            BigDecimal total = cartItems.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("success", true);
            response.put("message", "Produkten har lagts till i kundvagnen");
            response.put("cartCount", totalItems);
            response.put("cartTotal", total);

        } catch (Exception e) {
            log.error("Fel vid AJAX-tillägg i kundvagn för produkt: " + productId, e);
            response.put("success", false);
            response.put("message", "Ett fel uppstod vid tillägg i kundvagn");
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // Hämta antal items i kundvagn (AJAX)
    @GetMapping("/cart/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartCount(HttpSession session) {
        List<CartItem> cartItems = getCartItemsFromSession(session);

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("count", totalItems);

        return ResponseEntity.ok(response);
    }

    // Helper metod för att hämta cart items från session
    @SuppressWarnings("unchecked")
    private List<CartItem> getCartItemsFromSession(HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cartItems);
        }
        return cartItems;
    }

    // CartItem class som är kompatibel med din template
    public static class CartItem {
        private Product product;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public CartItem(Product product, Integer quantity) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = product.getPrice();
            updatePrice();
        }

        public void updatePrice() {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        // Getters and setters
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            updatePrice();
        }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            updatePrice();
        }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
}