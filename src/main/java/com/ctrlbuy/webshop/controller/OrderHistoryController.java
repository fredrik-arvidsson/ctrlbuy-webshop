package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Order;
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

        return "order-history";
    }

    /**
     * CUSTOMER ORDERS ROUTE - FIX FÖR /customer/orders
     */
    @GetMapping("/customer/orders")
    public String viewCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {
        return viewOrderHistory(page, size, authentication, model);
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
            redirectAttributes.addFlashAttribute("error", "Order inte hittad eller tillhör inte dig.");
            return "redirect:/orders";
        }

        Order order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("user", user);

        return "order-details";
    }

    /**
     * CUSTOMER ORDER DETAILS - FIX FÖR /customer/orders/{orderId}
     */
    @GetMapping("/customer/orders/{orderId}")
    public String viewCustomerOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        return viewOrderDetails(orderId, authentication, model, redirectAttributes);
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
            redirectAttributes.addFlashAttribute("error", "Order med nummer " + orderNumber + " inte hittad.");
            return "redirect:/orders";
        }

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