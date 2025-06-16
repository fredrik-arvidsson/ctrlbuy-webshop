package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Value("${ADMIN_EMAIL:admin@ctrlbuy.com}")
    private String adminEmail;

    @Value("${DEV_ADMIN_PASSWORD:dev123}")
    private String devAdminPassword;

    @Bean
    @Profile("!test")  // Kör INTE under test-profil
    public CommandLineRunner init(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("DataInitializer: Kör data-initialisering...");

            // ✅ SKAPA BACKUP ADMIN
            if (userRepository.findByUsername(adminUsername).isEmpty() &&
                    userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setFirstName("Admin");
                admin.setLastName("Användare");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setActive(true);
                admin.setEmailVerified(true);  // Admin är förverifierad
                admin.addRole("ROLE_ADMIN");  // ✅ Backup admin
                userRepository.save(admin);
                logger.info("✅ Backup admin created successfully");
            }

            // ✅ SKAPA HUVUDADMIN (UTVECKLARE)
            if (userRepository.findByUsername("developer").isEmpty() &&
                    userRepository.findByEmail("developer@ctrlbuy.com").isEmpty()) {
                User developer = new User();
                developer.setUsername("developer");
                developer.setEmail("developer@ctrlbuy.com");
                developer.setFirstName("Developer");
                developer.setLastName("Admin");
                developer.setPassword(passwordEncoder.encode(devAdminPassword));
                developer.setActive(true);
                developer.setEmailVerified(true);  // Förverifierad
                developer.addRole("ROLE_ADMIN");  // ✅ Utvecklare som admin
                userRepository.save(developer);
                logger.info("✅ Developer admin created successfully");
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
                logger.info("✅ Test user created successfully");
            }

            // ✅ TA BORT GAMLA ADMIN@EXAMPLE.COM OM DEN FINNS
            userRepository.findByEmail("admin@example.com").ifPresent(oldAdmin -> {
                if (!oldAdmin.getEmail().equals(adminEmail)) {
                    try {
                        userRepository.delete(oldAdmin);
                        logger.info("🗑️ Old admin@example.com removed");
                    } catch (Exception e) {
                        logger.warn("⚠️ Could not remove old admin: " + e.getMessage());
                        oldAdmin.setActive(false);
                        oldAdmin.setUsername("old_admin_" + System.currentTimeMillis());
                        oldAdmin.setEmail("deactivated_" + System.currentTimeMillis() + "@example.com");
                        userRepository.save(oldAdmin);
                        logger.info("🔒 Old admin deactivated instead");
                    }
                }
            });

            logger.info("🎉 Data initialization completed!");
            logger.info("📝 Admin credentials configured via environment variables");
            logger.info("📝 Developer admin created successfully");
        };
    }

    @Bean
    @Profile("test")  // Kör endast under test-profil
    public CommandLineRunner testInit() {
        return args -> {
            logger.info("Test CommandLineRunner: Skipping data initialization");
        };
    }
}