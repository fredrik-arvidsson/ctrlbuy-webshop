package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_EXPIRY_HOURS = 1; // Token giltig i 1 timme

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection istället för @Autowired
    public PasswordResetService(UserRepository userRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Begär lösenordsåterställning för given email
     */
    @Transactional
    public boolean requestPasswordReset(String email) {
        try {
            logger.info("Begäran om lösenordsåterställning för email: {}", email);

            // Kontrollera om användaren finns
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Ingen användare hittad med email: {}", email);
                // Vi returnerar true ändå för säkerhet (information disclosure)
                return true;
            }

            User user = userOpt.get();

            // Generera ny token och sätt utgångsdatum
            String token = generateSecureToken();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

            // Spara användaren med ny token
            userRepository.save(user);

            // Skicka email - fånga exception om det misslyckas
            try {
                emailService.sendPasswordResetEmail(email, token, user.getFirstName());
                logger.info("Återställningsemail skickat till: {}", email);
                return true;
            } catch (Exception emailException) {
                logger.error("Misslyckades att skicka email till: {}", email, emailException);
                return false;
            }

        } catch (Exception e) {
            logger.error("Fel vid begäran om lösenordsåterställning för email: {}", email, e);
            return false;
        }
    }

    /**
     * Återställ lösenord med token
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        try {
            logger.info("Försöker återställa lösenord med token");

            // Hitta användare baserat på token
            Optional<User> userOpt = userRepository.findByResetToken(token);
            if (userOpt.isEmpty()) {
                logger.warn("Ogiltigt token");
                return false;
            }

            User user = userOpt.get();

            // Kontrollera om token är giltigt
            if (!user.isResetTokenValid()) {
                logger.warn("Token är utgånget för användare: {}", user.getEmail());
                return false;
            }

            // Uppdatera lösenord
            user.setPassword(passwordEncoder.encode(newPassword));

            // Rensa återställningstoken
            user.setResetToken(null);
            user.setResetTokenExpiry(null);

            // Spara användaren
            userRepository.save(user);

            logger.info("Lösenord återställt för användare: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            logger.error("Fel vid återställning av lösenord", e);
            return false;
        }
    }

    /**
     * Validera token
     */
    public boolean isValidResetToken(String token) {
        try {
            Optional<User> userOpt = userRepository.findByResetToken(token);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            return user.isResetTokenValid();

        } catch (Exception e) {
            logger.error("Fel vid validering av token", e);
            return false;
        }
    }

    /**
     * Rensa gamla/utgångna tokens (kan köras som scheduled task)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            // Hitta alla användare med utgångna tokens och rensa dem
            userRepository.findAll().forEach(user -> {
                if (user.getResetToken() != null && !user.isResetTokenValid()) {
                    user.setResetToken(null);
                    user.setResetTokenExpiry(null);
                    userRepository.save(user);
                }
            });
            logger.info("Rensade gamla tokens");
        } catch (Exception e) {
            logger.error("Fel vid rensning av gamla tokens", e);
        }
    }

    /**
     * Generera säker token
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        StringBuilder token = new StringBuilder();
        for (byte b : bytes) {
            token.append(String.format("%02x", b));
        }

        return token.toString();
    }
}