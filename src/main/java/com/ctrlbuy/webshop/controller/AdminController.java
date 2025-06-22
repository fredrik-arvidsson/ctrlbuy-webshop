package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import com.ctrlbuy.webshop.service.UserService;
import com.ctrlbuy.webshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Admin Dashboard - huvudsida efter inloggning
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers();

        // Beräkna statistik
        long totalUsers = users.size();
        long activeUsers = users.stream()
                .filter(User::isActive)
                .count();
        long inactiveUsers = totalUsers - activeUsers;
        long verifiedUsers = users.stream()
                .filter(User::isEmailVerified)
                .count();

        // Lägg till data i model för Thymeleaf
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("inactiveUsers", inactiveUsers);
        model.addAttribute("verifiedUsers", verifiedUsers);

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
     * ✅ UPPDATERAD: Visa användare med filter för aktiva/inaktiva
     */
    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "active") String filter, Model model) {
        List<User> users;
        String pageTitle;

        switch (filter.toLowerCase()) {
            case "inactive":
                users = userService.getInactiveUsers();
                pageTitle = "Inaktiva användare";
                break;
            case "all":
                users = userService.getAllUsers();
                pageTitle = "Alla användare";
                break;
            default: // "active"
                users = userService.getActiveUsers();
                pageTitle = "Aktiva användare";
                break;
        }

        model.addAttribute("users", users);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("title", "Kundhantering - CtrlBuy Admin");

        // Statistik för filter-knappar
        long totalUsers = userService.countAllUsers();
        long activeUsers = userService.countActiveUsers();
        long inactiveUsers = totalUsers - activeUsers;
        long verifiedUsers = users.stream()
                .filter(User::isEmailVerified)
                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("inactiveUsers", inactiveUsers);
        model.addAttribute("verifiedUsers", verifiedUsers);

        return "admin/users";
    }

    /**
     * ✅ UPPDATERAD: Inaktivera användare (mjuk borttagning) med e-postnotifiering
     */
    @PostMapping("/users/deactivate/{id}")
    @ResponseBody
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användaren finns inte"));
            }

            String username = user.getUsername();

            // Förhindra att admin inaktiverar sig själv
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && username.equals(auth.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Du kan inte inaktivera dig själv"));
            }

            // Förhindra inaktivering av huvudadmin "fredrik"
            if ("fredrik".equals(username)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Huvudadmin kan inte inaktiveras"));
            }

            // Inaktivera användaren (mjuk borttagning)
            user.setActive(false);
            userService.save(user);

            // Skicka e-postnotifiering till användaren
            try {
                String adminUsername = auth != null ? auth.getName() : "System";
                emailService.sendAccountDeactivationNotification(user, adminUsername, reason);
            } catch (Exception emailError) {
                // Logga men låt inte e-post-fel stoppa inaktiveringen
                System.err.println("Kunde inte skicka inaktiveringsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "Användare '" + username + "' har inaktiverats (e-post " + user.getEmail() + " är nu blockerad)"
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "error", "Fel vid inaktivering: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPPDATERAD: Reaktivera användare med e-postnotifiering
     */
    @PostMapping("/users/reactivate/{id}")
    @ResponseBody
    public ResponseEntity<?> reactivateUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användaren finns inte"));
            }

            // Reaktivera användaren
            user.setActive(true);
            userService.save(user);

            // Skicka e-postnotifiering till användaren
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String adminUsername = auth != null ? auth.getName() : "System";
                emailService.sendAccountReactivationNotification(user, adminUsername);
            } catch (Exception emailError) {
                // Logga men låt inte e-post-fel stoppa reaktiveringen
                System.err.println("Kunde inte skicka reaktiveringsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "Användare '" + user.getUsername() + "' har reaktiverats"
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "error", "Fel vid reaktivering: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPPDATERAD: Snabb inaktivering via e-post med notifiering
     */
    @PostMapping("/users/quick-deactivate")
    @ResponseBody
    public ResponseEntity<?> quickDeactivateByEmail(@RequestParam("email") String email, @RequestParam(required = false) String reason) {
        try {
            // Hitta användare via e-post
            Optional<User> userOpt = userService.findByEmail(email);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användare med e-post " + email + " hittades inte"));
            }

            User user = userOpt.get();
            String username = user.getUsername();

            // Kontrollera att det inte är admin själv
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && username.equals(auth.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Du kan inte inaktivera dig själv"));
            }

            // Förhindra inaktivering av huvudadmin "fredrik"
            if ("fredrik".equals(username)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Huvudadmin kan inte inaktiveras"));
            }

            // Kontrollera om användaren redan är inaktiv
            if (!user.isActive()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användare '" + username + "' är redan inaktiv"));
            }

            // Inaktivera användaren
            user.setActive(false);
            userService.save(user);

            // Skicka e-postnotifiering till användaren
            try {
                String adminUsername = auth != null ? auth.getName() : "System";
                emailService.sendAccountDeactivationNotification(user, adminUsername, reason);
            } catch (Exception emailError) {
                // Logga men låt inte e-post-fel stoppa inaktiveringen
                System.err.println("Kunde inte skicka inaktiveringsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "Användare '" + username + "' (" + email + ") har inaktiverats"
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "error", "Fel vid inaktivering: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPPDATERAD: Toggle active (för snabb växling) med e-postnotifiering
     */
    @PostMapping("/users/toggle-active/{id}")
    @ResponseBody
    public ResponseEntity<?> toggleUserActive(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användaren finns inte"));
            }

            String username = user.getUsername();
            boolean wasActive = user.isActive();

            // Förhindra att admin togglar sig själv
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && username.equals(auth.getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Du kan inte ändra din egen status"));
            }

            // Förhindra inaktivering av huvudadmin "fredrik"
            if ("fredrik".equals(username) && wasActive) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Huvudadmin kan inte inaktiveras"));
            }

            // Växla status
            userService.toggleUserActive(id);
            String action = wasActive ? "inaktiverad" : "aktiverad";

            // Skicka e-postnotifiering
            try {
                String adminUsername = auth != null ? auth.getName() : "System";
                if (wasActive) {
                    // Blev inaktiverad
                    emailService.sendAccountDeactivationNotification(user, adminUsername, null);
                } else {
                    // Blev aktiverad
                    emailService.sendAccountReactivationNotification(user, adminUsername);
                }
            } catch (Exception emailError) {
                // Logga men låt inte e-post-fel stoppa statusändringen
                System.err.println("Kunde inte skicka statusändringsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "Användare '" + username + "' har " + action
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "error", "Fel vid statusändring: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPPDATERAD: Skicka om verifieringsmail
     */
    @PostMapping("/users/{id}/reset-verification")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetUserVerification(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Användare hittades inte"));

            // Kontrollera om användaren redan är verifierad
            if (user.isEmailVerified()) {
                response.put("success", false);
                response.put("message", "Användaren är redan verifierad");
                return ResponseEntity.ok(response);
            }

            // Generera ny token
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            // Skicka mail
            boolean emailSent = emailService.sendVerificationEmail(user.getEmail(), token);

            if (emailSent) {
                response.put("success", true);
                response.put("message", "Verifieringsmail har skickats om till " + user.getEmail());
            } else {
                response.put("success", false);
                response.put("message", "Kunde inte skicka verifieringsmail. Kontrollera e-postkonfigurationen.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Fel: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ✅ UPPDATERAD: Uppdatera email-adress (nu med kontroll av ALLA e-poster)
     */
    @PostMapping("/users/update-email/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUserEmail(@PathVariable Long id,
                                             @RequestParam String email) {
        try {
            // Validera email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Ogiltigt email-format"));
            }

            // Uppdatera användare
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Användaren finns inte"));
            }

            String oldEmail = user.getEmail();

            // Kolla om det är samma e-post (tillåt)
            if (email.equals(oldEmail)) {
                return ResponseEntity.ok()
                        .body(Map.of(
                                "success", true,
                                "message", "E-post är redan " + email + " (ingen ändring)"
                        ));
            }

            // ✅ UPPDATERAD: Kolla om email redan används (inkluderar inaktiva användare)
            if (userService.existsByEmailIncludingInactive(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Email-adressen används redan (även av inaktiva användare)"));
            }

            user.setEmail(email);
            userService.save(user);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "Email uppdaterad från " + oldEmail + " till " + email
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "error", "Fel vid uppdatering: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPPDATERAD: Permanent borttagning av användare (form-baserad) med e-postnotifiering
     */
    @PostMapping("/users/{id}/delete-permanently")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePermanently(@PathVariable Long id, @RequestParam(required = false) String reason, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Användare hittades inte");
                return "redirect:/admin/users?filter=inactive";
            }

            String userInfo = user.getUsername() + " (" + user.getEmail() + ")";

            // Skicka e-postnotifiering INNAN borttagning
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String adminUsername = auth != null ? auth.getName() : "System";
                emailService.sendAccountDeletionNotification(user, adminUsername, reason);
            } catch (Exception emailError) {
                // Logga men fortsätt med borttagning
                System.err.println("Kunde inte skicka borttagningsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            // Anropa uppdaterade metoden (kastar nu exceptions)
            userService.deletePermanently(id);

            redirectAttributes.addFlashAttribute("success",
                    "Användare " + userInfo + " har tagits bort permanent från databasen");

        } catch (RuntimeException e) {
            // Business logic errors (t.ex. "Admin kan inte tas bort")
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Tekniska fel
            redirectAttributes.addFlashAttribute("error",
                    "Tekniskt fel vid permanent borttagning: " + e.getMessage());
        }

        return "redirect:/admin/users?filter=inactive";
    }

    /**
     * ✅ UPPDATERAD: Permanent borttagning av användare (AJAX-baserad) med e-postnotifiering
     * Hanterar nu exceptions från UserService
     */
    @PostMapping("/users/{id}/delete-permanently-ajax")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deletePermanentlyAjax(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Användare hittades inte"));
            }

            String userInfo = user.getUsername() + " (" + user.getEmail() + ")";

            // Skicka e-postnotifiering INNAN borttagning
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String adminUsername = auth != null ? auth.getName() : "System";
                emailService.sendAccountDeletionNotification(user, adminUsername, reason);
            } catch (Exception emailError) {
                // Logga men fortsätt med borttagning
                System.err.println("Kunde inte skicka borttagningsnotifiering till " + user.getEmail() + ": " + emailError.getMessage());
            }

            // Anropa uppdaterade metoden (kastar nu exceptions istället för att returnera boolean)
            userService.deletePermanently(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Användare " + userInfo + " har tagits bort permanent från databasen"
            ));

        } catch (RuntimeException e) {
            // Business logic errors (t.ex. "Admin kan inte tas bort", "Endast inaktiva användare")
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));

        } catch (Exception e) {
            // Tekniska fel (databas, foreign keys, etc.)
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Tekniskt fel vid borttagning: " + e.getMessage()));
        }
    }
}