package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Cart;
import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.CartService;
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

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String showCheckout(Model model, HttpSession session, Authentication auth) {
        try {
            // Hämta aktuell användare om inloggad
            User currentUser = null;
            if (auth != null && auth.isAuthenticated()) {
                currentUser = userService.findByUsername(auth.getName()).orElse(null);
                model.addAttribute("user", currentUser);
            }

            // Hämta varukorg
            Cart cart = cartService.getCurrentCart(currentUser, session.getId());

            if (cart == null || cart.getItems().isEmpty()) {
                model.addAttribute("error", "Din varukorg är tom");
                return "redirect:/cart";
            }

            // Beräkna totaler
            BigDecimal subtotal = cart.getTotalAmount();
            BigDecimal shipping = BigDecimal.valueOf(49.00); // Fast frakt
            BigDecimal total = subtotal.add(shipping);

            // Lägg till i model
            model.addAttribute("cart", cart);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);

            return "checkout/checkout";

        } catch (Exception e) {
            log.error("Error in checkout: ", e);
            model.addAttribute("error", "Ett fel uppstod vid checkout");
            return "redirect:/cart";
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
            // Hämta aktuell användare om inloggad
            User currentUser = null;
            if (auth != null && auth.isAuthenticated()) {
                currentUser = userService.findByUsername(auth.getName()).orElse(null);
            }

            // Hämta varukorg
            Cart cart = cartService.getCurrentCart(currentUser, session.getId());

            if (cart == null || cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Din varukorg är tom");
                return "redirect:/cart";
            }

            // Skapa beställning
            Order order = orderService.createOrder(
                    cart, email, firstName, lastName, address,
                    city, postalCode, phone, notes, paymentMethod, auth
            );

            // Rensa varukorgen
            cartService.clearCart(cart);

            // Redirect till bekräftelse
            return "redirect:/checkout/confirmation/" + order.getId();

        } catch (Exception e) {
            log.error("Error processing order: ", e);
            redirectAttributes.addFlashAttribute("error", "Ett fel uppstod vid beställningen: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public String showConfirmation(@PathVariable Long orderId, Model model) {
        try {
            Order order = orderService.findById(orderId);

            if (order == null) {
                model.addAttribute("error", "Beställningen kunde inte hittas");
                return "redirect:/";
            }

            model.addAttribute("order", order);
            return "checkout/confirmation";

        } catch (Exception e) {
            log.error("Error showing confirmation: ", e);
            model.addAttribute("error", "Ett fel uppstod");
            return "redirect:/";
        }
    }
}