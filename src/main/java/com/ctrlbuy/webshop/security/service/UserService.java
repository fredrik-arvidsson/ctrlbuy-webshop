package com.ctrlbuy.webshop.security.service;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(RegisterRequest registerRequest) {
        // Kontrollera om användaren redan finns
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Användare med e-post " + registerRequest.getEmail() + " finns redan");
        }

        // Skapa ny användare med konstruktorn
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        // Spara användaren i databasen
        return userRepository.save(user);
    }

    // Här kan du lägga till fler användarrelaterade metoder vid behov
}