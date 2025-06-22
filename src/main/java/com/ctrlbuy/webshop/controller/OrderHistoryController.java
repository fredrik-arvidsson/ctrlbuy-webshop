package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.OrderService;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderHistoryController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    /**
     * Visa orderhistorik med paginering
     */
    @GetMapping
    public String viewOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            log.error("Användare inte hittad: {}", authentication.getName());
            return "redirect:/login";
        }

        User user = userOpt.get();

        // Hämta orders med paginering
        Page<Order> orderPage = orderService.getOrdersByUserWithPagination(user, page, size);

        // Beräkna statistik
        Long totalOrders = orderService.countOrdersByUser(user);
        Double totalSpent = orderService.getTotalSpentByUser(user);
        Optional<Order> latestOrder = orderService.getLatestOrderByUser(user);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSpent", totalSpent != null ? totalSpent : 0.0);
        model.addAttribute("latestOrder", latestOrder.orElse(null));
        model.addAttribute("user", user);

        log.info("Visar orderhistorik för användare: {} ({} orders)", user.getUsername(), totalOrders);
        return "order-history";
    }

    /**
     * Visa detaljer för specifik order
     */
    @GetMapping("/{orderId}")
    public String viewOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Optional<Order> orderOpt = orderService.getOrderByIdAndUser(orderId, user);

        if (orderOpt.isEmpty()) {
            log.warn("Order {} inte hittad eller tillhör inte användare {}", orderId, user.getUsername());
            redirectAttributes.addFlashAttribute("error", "Order inte hittad eller tillhör inte dig.");
            return "redirect:/orders";
        }

        Order order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("user", user);

        log.info("Visar orderdetaljer för order: {} (användare: {})", order.getOrderNumber(), user.getUsername());
        return "order-details";
    }

    /**
     * Sök order via ordernummer
     */
    @PostMapping("/search")
    public String searchOrder(
            @RequestParam String orderNumber,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Optional<Order> orderOpt = orderService.getOrderByOrderNumberAndUser(orderNumber.trim(), user);

        if (orderOpt.isEmpty()) {
            log.warn("Order med nummer {} inte hittad för användare {}", orderNumber, user.getUsername());
            redirectAttributes.addFlashAttribute("error", "Order med nummer " + orderNumber + " inte hittad.");
            return "redirect:/orders";
        }

        log.info("Order hittad via sök: {} (användare: {})", orderNumber, user.getUsername());
        return "redirect:/orders/" + orderOpt.get().getId();
    }

    /**
     * Svenska rutt för orderhistorik
     */
    @GetMapping("/mina-ordrar")
    public String viewOrderHistorySwedish(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {
        return viewOrderHistory(page, size, authentication, model);
    }

    /**
     * Orderdetaljer via svensk rutt
     */
    @GetMapping("/mina-ordrar/{orderId}")
    public String viewOrderDetailsSwedish(
            @PathVariable Long orderId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        return viewOrderDetails(orderId, authentication, model, redirectAttributes);
    }
}