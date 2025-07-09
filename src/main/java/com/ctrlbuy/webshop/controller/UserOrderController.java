package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.OrderService;
import com.ctrlbuy.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * UserOrderController - Förenklad version med endast befintliga metoder
 * ✅ ANVÄNDER ENDAST: getOrdersByUserId() och findById()
 */
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class UserOrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Visa användarens beställningar - FÖRENKLAD
     */
    @GetMapping
    public String viewUserOrders(Authentication authentication, Model model) {

        try {
            // Kontrollera om användaren är inloggad
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Oanvändare försöker komma åt beställningar");
                return "redirect:/login";
            }

            // Hämta användaren
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                log.error("Användare inte hittad: {}", username);
                return "redirect:/login";
            }

            User user = userOpt.get();
            log.info("Hämtar beställningar för användare: {}", username);

            // Hämta användarens beställningar
            List<Order> userOrders = orderService.getOrdersByUserId(user.getId());

            log.info("Hittade {} beställningar för användare {}", userOrders.size(), username);

            // Lägg till data till modellen
            model.addAttribute("orders", userOrders);
            model.addAttribute("totalElements", userOrders.size());
            model.addAttribute("user", user);

            return "orders";

        } catch (Exception e) {
            log.error("Fel vid hämtning av beställningar: ", e);
            model.addAttribute("error", "Kunde inte hämta beställningar");
            return "orders";
        }
    }

    /**
     * Visa detaljer för en specifik beställning - FÖRENKLAD
     */
    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId,
                                   Authentication authentication,
                                   Model model) {

        try {
            // Kontrollera om användaren är inloggad
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            // Hämta användaren
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                return "redirect:/login";
            }

            User user = userOpt.get();

            // Hämta beställningen
            Order order = orderService.findById(orderId);

            if (order == null) {
                log.warn("Beställning inte hittad: {}", orderId);
                model.addAttribute("error", "Beställningen kunde inte hittas");
                return "orders";
            }

            // Kontrollera att beställningen tillhör användaren
            if (!order.getUserId().equals(user.getId())) {
                log.warn("Användare {} försöker komma åt order {} som inte tillhör dem",
                        username, orderId);
                model.addAttribute("error", "Du har inte behörighet att se denna beställning");
                return "orders";
            }

            // Lägg till data till modellen
            model.addAttribute("order", order);
            model.addAttribute("user", user);

            return "order-details";

        } catch (Exception e) {
            log.error("Fel vid hämtning av beställningsdetaljer: ", e);
            model.addAttribute("error", "Kunde inte hämta beställningsdetaljer");
            return "orders";
        }
    }
}