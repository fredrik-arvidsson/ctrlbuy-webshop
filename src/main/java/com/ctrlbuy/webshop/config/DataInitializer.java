package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.model.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsernameOrEmail("admin", "admin@example.com")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword("{noop}password"); // använd riktig password encoder i produktion
                admin.setActive(true);
                admin.addRole("ROLE_ADMIN");  // Här använder vi addRole-metoden
                userRepository.save(admin);
                System.out.println("Admin user created.");
            }
        };
    }
}