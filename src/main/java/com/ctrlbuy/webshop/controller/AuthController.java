package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.model.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Konstruktorbaserad injection
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /login - visar login-sidan
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // GET /register - visar registreringssidan
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // POST /register - hanterar registreringsformuläret
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Användarnamnet är redan taget.");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole("USER"); // lägg till roll korrekt
        user.setActive(true);
        userRepository.save(user);

        return "redirect:/login?registered"; // redirect till login-sidan efter registrering
    }
}

