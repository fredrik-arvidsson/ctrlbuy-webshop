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
    public CommandLineRunner init(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("DataInitializer: Kör data-initialisering...");

            // ✅ SKAPA BACKUP ADMIN
            if (userRepository.findByUsername("admin").isEmpty() &&
                    userRepository.findByEmail("admin@ctrlbuy.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@ctrlbuy.com");  // Professionell email
                admin.setFirstName("Admin");
                admin.setLastName("Användare");
                admin.setPassword(passwordEncoder.encode("admin123"));  // Starkare lösenord
                admin.setActive(true);
                admin.setEmailVerified(true);  // Admin är förverifierad
                admin.addRole("ROLE_ADMIN");  // ✅ Backup admin
                userRepository.save(admin);
                System.out.println("✅ Backup admin skapad - Användarnamn: admin, Lösenord: admin123");
            }

            // ✅ SKAPA FREDRIK (DU) SOM HUVUDADMIN
            if (userRepository.findByUsername("fredrik").isEmpty() &&
                    userRepository.findByEmail("fredrik.g.arvidsson@gmail.com").isEmpty()) {
                User fredrik = new User();
                fredrik.setUsername("fredrik");
                fredrik.setEmail("fredrik.g.arvidsson@gmail.com");
                fredrik.setFirstName("Fredrik");
                fredrik.setLastName("Arvidsson");
                fredrik.setPassword(passwordEncoder.encode("password123"));
                fredrik.setActive(true);
                fredrik.setEmailVerified(true);  // Förverifierad
                fredrik.addRole("ROLE_ADMIN");  // ✅ DU är admin!
                userRepository.save(fredrik);
                System.out.println("✅ Fredrik skapad som HUVUDADMIN");
            }

            // ✅ TESTANVÄNDARE FÖR UTVECKLING
            if (userRepository.findByUsername("user").isEmpty() &&
                    userRepository.findByEmail("user@example.com").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setFirstName("Test");
                user.setLastName("Användare");
                user.setPassword(passwordEncoder.encode("password"));
                user.setActive(true);
                user.setEmailVerified(false);  // Behöver verifiering
                user.addRole("ROLE_USER");  // ✅ Test user
                userRepository.save(user);
                System.out.println("✅ Testanvändare skapad (ej verifierad)");
            }

            // ✅ TA BORT GAMLA ADMIN@EXAMPLE.COM OM DEN FINNS
            userRepository.findByEmail("admin@example.com").ifPresent(oldAdmin -> {
                if (!oldAdmin.getEmail().equals("admin@ctrlbuy.com")) {
                    try {
                        userRepository.delete(oldAdmin);
                        System.out.println("🗑️ Gamla admin@example.com borttagen");
                    } catch (Exception e) {
                        System.err.println("⚠️ Kunde inte ta bort gamla admin: " + e.getMessage());
                        oldAdmin.setActive(false);
                        oldAdmin.setUsername("old_admin_" + System.currentTimeMillis());
                        oldAdmin.setEmail("deactivated_" + System.currentTimeMillis() + "@example.com");
                        userRepository.save(oldAdmin);
                        System.out.println("🔒 Gamla admin deaktiverad istället");
                    }
                }
            });

            System.out.println("🎉 Data-initialisering slutförd!");
            System.out.println("📝 Fredrik (HUVUDADMIN): användarnamn='fredrik', lösenord='password123'");
            System.out.println("📝 Backup admin: användarnamn='admin', lösenord='admin123'");
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