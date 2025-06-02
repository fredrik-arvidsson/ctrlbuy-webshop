package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.OrderService;
import com.ctrlbuy.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String showCheckout(Model model, HttpSession session, Authentication auth) {
        try {
            // Kontrollera att användaren är inloggad
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }

            User currentUser = userService.findByUsername(auth.getName()).orElse(null);
            model.addAttribute("user", currentUser);

            // Hämta varukorg från SESSION (samma som CartController)
            @SuppressWarnings("unchecked")
            List<CartController.CartItem> cartItems = (List<CartController.CartItem>)
                    session.getAttribute("shopping_cart");

            log.debug("DEBUG CHECKOUT: Cart items from session = {}", cartItems != null ? cartItems.size() : "null");

            if (cartItems == null || cartItems.isEmpty()) {
                log.debug("DEBUG CHECKOUT: Cart is null or empty, redirecting to cart");
                model.addAttribute("error", "Din varukorg är tom");
                return "redirect:/varukorg";
            }

            // Beräkna totaler från session-data
            BigDecimal subtotal = cartItems.stream()
                    .map(CartController.CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shipping = BigDecimal.valueOf(49.00);
            BigDecimal total = subtotal.add(shipping);

            log.debug("DEBUG CHECKOUT: Proceeding to checkout with {} items, total: {}",
                    cartItems.size(), total);

            // Lägg till i model (samma struktur som innan)
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);

            return "checkout";

        } catch (Exception e) {
            log.error("Error in checkout: ", e);
            model.addAttribute("error", "Ett fel uppstod vid checkout");
            return "redirect:/varukorg";
        }
    }

    @PostMapping("/process")
    public String processOrder(
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String postalCode,
            @RequestParam String phone,
            @RequestParam(required = false) String notes,
            @RequestParam String paymentMethod,
            HttpSession session,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            // Kontrollera inloggning
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }

            User currentUser = userService.findByUsername(auth.getName()).orElse(null);

            // Hämta varukorg från SESSION
            @SuppressWarnings("unchecked")
            List<CartController.CartItem> cartItems = (List<CartController.CartItem>)
                    session.getAttribute("shopping_cart");

            if (cartItems == null || cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Din varukorg är tom");
                return "redirect:/varukorg";
            }

            // För nu - skapa enkel beställningsbekräftelse
            // Du kan senare integrera med din OrderService om du vill
            log.info("Processing order for user: {} with {} items",
                    currentUser != null ? currentUser.getUsername() : "unknown",
                    cartItems.size());

            // Beräkna total för loggning
            BigDecimal orderTotal = cartItems.stream()
                    .map(CartController.CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(BigDecimal.valueOf(49.00)); // shipping

            log.info("Order total: {} kr", orderTotal);

            // Rensa session-varukorg
            session.removeAttribute("shopping_cart");

            // Success meddelande
            redirectAttributes.addFlashAttribute("success",
                    "Tack för din beställning! Total: " + orderTotal + " kr");

            return "redirect:/";

        } catch (Exception e) {
            log.error("Error processing order: ", e);
            redirectAttributes.addFlashAttribute("error",
                    "Ett fel uppstod vid beställningen: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public String showConfirmation(@PathVariable Long orderId, Model model) {
        try {
            // Om du vill använda OrderService senare
            Order order = orderService.findById(orderId);

            if (order == null) {
                model.addAttribute("error", "Beställningen kunde inte hittas");
                return "redirect:/";
            }

            model.addAttribute("order", order);
            return "confirmation";

        } catch (Exception e) {
            log.error("Error showing confirmation: ", e);
            model.addAttribute("error", "Ett fel uppstod");
            return "redirect:/";
        }
    }
}