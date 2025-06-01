package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * Admin Dashboard - huvudsida efter inloggning
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers();

        // Beräkna statistik
        long totalUsers = users.size();
        long verifiedUsers = users.stream()
                .filter(User::isEmailVerified)
                .count();
        long pendingUsers = totalUsers - verifiedUsers;

        // Lägg till data i model för Thymeleaf
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("verifiedUsers", verifiedUsers);
        model.addAttribute("pendingUsers", pendingUsers);

        return "admin/dashboard";
    }

    /**
     * Redirect från /admin till dashboard
     */
    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Visa alla användare (detaljerad lista)
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Ta bort användare via POST
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        try {
            userService.deleteUser(id);
            model.addAttribute("message", "Användare borttagen!");
            model.addAttribute("messageType", "success");
        } catch (Exception e) {
            model.addAttribute("message", "Fel vid borttagning: " + e.getMessage());
            model.addAttribute("messageType", "error");
        }
        return "redirect:/admin/users";
    }

    /**
     * Ta bort användare via e-post (för snabb borttagning)
     */
    @PostMapping("/users/delete-by-email")
    public String deleteUserByEmail(@RequestParam("email") String email, Model model) {
        try {
            userService.deleteUserByEmail(email);
            model.addAttribute("message", "Användare med e-post " + email + " borttagen!");
            model.addAttribute("messageType", "success");
        } catch (Exception e) {
            model.addAttribute("message", "Fel vid borttagning: " + e.getMessage());
            model.addAttribute("messageType", "error");
        }
        return "redirect:/admin/users";
    }

    /**
     * Aktivera/inaktivera användare
     */
    @PostMapping("/users/toggle-active/{id}")
    public String toggleUserActive(@PathVariable Long id) {
        try {
            userService.toggleUserActive(id);
        } catch (Exception e) {
            // Ignorera fel för nu
        }
        return "redirect:/admin/users";
    }

    /**
     * Återställ verifiering (sätt emailVerified = false)
     */
    @PostMapping("/users/reset-verification/{id}")
    public String resetUserVerification(@PathVariable Long id) {
        try {
            userService.resetUserVerification(id);
        } catch (Exception e) {
            // Ignorera fel för nu
        }
        return "redirect:/admin/users";
    }
}