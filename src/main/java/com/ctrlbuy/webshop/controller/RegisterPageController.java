package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.model.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RegisterPageController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerViaForm(@ModelAttribute("registerRequest") @Valid RegisterRequest request,
                                  BindingResult bindingResult,
                                  Model model) {
        // Valideringsfel från DTO
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Lösenorden matchar inte.");
            return "register";
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            model.addAttribute("error", "Användarnamnet är redan taget.");
            return "register";
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(true);  // Uppdaterad metod
        newUser.addRole("ROLE_USER");

        userRepository.save(newUser);

        return "redirect:/login";
    }
}
