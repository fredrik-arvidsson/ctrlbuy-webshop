package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String adminOrders(Model model) {
        try {
            logger.info("Hämtar alla orders för admin");
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);

            // Calculate total revenue safely
            double totalRevenue = orders.stream()
                    .filter(order -> order.getTotalAmount() != null)
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalOrders", orders.size());

            logger.info("Admin orders: {} orders, total revenue: {} kr", orders.size(), totalRevenue);
            return "admin/orders";
        } catch (Exception e) {
            logger.error("Fel vid hämtning av admin orders", e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av beställningar");
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("totalRevenue", 0.0);
            model.addAttribute("totalOrders", 0);
            return "admin/orders";
        }
    }

    // 🔧 TEMPORÄR TESTMETOD - TA BORT EFTER FELSÖKNING
    @GetMapping("/orders/test")
    public String testOrders(Model model) {
        logger.info("TEST: Temporär orders test-metod");
        model.addAttribute("orders", new ArrayList<>());
        model.addAttribute("totalRevenue", 0.0);
        model.addAttribute("totalOrders", 0);
        model.addAttribute("message", "TEST: Template fungerar - huvudmetoden har problem");
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        try {
            logger.info("Hämtar order med ID: {}", id);
            Order order = orderService.getOrderById(id);
            if (order == null) {
                logger.warn("Order med ID {} hittades inte", id);
                model.addAttribute("error", "Beställning hittades inte");
                return "redirect:/admin/orders";
            }
            model.addAttribute("order", order);
            logger.info("Order {} hittades för admin", order.getOrderNumber());
            return "admin/order-details";
        } catch (Exception e) {
            logger.error("Fel vid hämtning av order med ID: {}", id, e);
            model.addAttribute("error", "Ett fel uppstod vid hämtning av beställning");
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            logger.info("Uppdaterar order {} status till: {}", id, status);
            orderService.updateOrderStatus(id, status);
            logger.info("Order {} status uppdaterad till: {}", id, status);
            return "redirect:/admin/orders/" + id;
        } catch (Exception e) {
            logger.error("Fel vid uppdatering av order {} status", id, e);
            return "redirect:/admin/orders";
        }
    }

    // 🔧 EXTRA DEBUG METOD
    @GetMapping("/orders/debug")
    @ResponseBody
    public String debugOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return "DEBUG: Hittade " + orders.size() + " orders i databasen. " +
                    "OrderService fungerar korrekt. Problem är troligen i template.";
        } catch (Exception e) {
            return "DEBUG ERROR: " + e.getMessage() + " - Problem i OrderService eller databas.";
        }
    }
}