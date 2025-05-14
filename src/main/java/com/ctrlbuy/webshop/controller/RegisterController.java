package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.ctrlbuy.webshop.security.service.UserService;
import java.util.stream.Collectors;

@Controller
public class RegisterController {

    private final UserService userService; // Du behöver implementera denna service

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest,
                               BindingResult bindingResult, Model model) {
        // Kontrollera om lösenorden matchar
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest",
                    "Lösenorden matchar inte");
        }

        // Om det finns valideringsfel, visa registreringsformuläret igen
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            return "register";
        }

        // Försök registrera användaren
        try {
            userService.registerNewUser(registerRequest);
            return "redirect:/login?registered";
        } catch (Exception e) {
            // Om något går fel (t.ex. användarnamnet är redan taget)
            model.addAttribute("errors", java.util.Collections.singletonList(e.getMessage()));
            return "register";
        }
    }
}