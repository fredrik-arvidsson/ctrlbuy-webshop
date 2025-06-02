package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")  // Kör INTE under test-profil
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("DataInitializer: Kör data-initialisering...");

            // Se till att metoden existsByUsernameOrEmail finns i UserRepository
            if (userRepository.findByUsername("admin").isEmpty() &&
                    userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");

                // Två alternativ för lösenordshantering:

                // Alternativ 1: Använd PasswordEncoder (rekommenderas)
                admin.setPassword(passwordEncoder.encode("password"));

                // Alternativ 2: Behåll {noop} prefixet för klartext (endast för utveckling)
                // admin.setPassword("{noop}password");

                admin.setActive(true);
                admin.addRole("ROLE_ADMIN");  // Här använder vi addRole-metoden
                userRepository.save(admin);
                System.out.println("Admin user created.");
            }

            // Skapa en testanvändare för enklare inloggning
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@example.com");

                // Använd samma hantering som för admin
                user.setPassword(passwordEncoder.encode("password"));
                // ELLER: user.setPassword("{noop}password");

                user.setActive(true);
                user.addRole("ROLE_USER");
                userRepository.save(user);
                System.out.println("Test user created.");
            }
        };
    }

    @Bean
    @Profile("test")  // Kör endast under test-profil
    public CommandLineRunner testInit() {
        return args -> {
            System.out.println("Test CommandLineRunner: Skippar data-initialisering");
        };
    }
}