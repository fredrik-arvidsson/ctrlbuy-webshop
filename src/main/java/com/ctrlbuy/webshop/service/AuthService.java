package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.dto.UserStats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 🛡️ MINIMAL AuthService - utan duplicerade klasser
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final LoggingService loggingService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthService(LoggingService loggingService,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       @Autowired(required = false) AuthenticationManager authenticationManager) {
        this.loggingService = loggingService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 🔐 Autentisera användare
     */
    public boolean authenticate(String username, String password) {
        logger.info("🔐 Autentiserar användare: {}", username);

        try {
            if (!validateCredentials(username, password)) {
                return false;
            }

            User user = findUserForAuthentication(username);
            if (user == null || !user.isEnabled()) {
                return false;
            }

            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            if (passwordMatches) {
                logger.info("✅ Autentisering lyckades för: {}", username);
                loggingService.logUserAction(username, "LOGIN", "Successful authentication");
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("❌ Autentiseringsfel för '{}': {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * 🚀 Registrera användare
     */
    public boolean registerUser(String username, String password, String email) {
        logger.info("🚀 Registrerar användare: {}", username);

        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setPassword(password);
            request.setConfirmPassword(password);
            request.setEmail(email);
            request.setFirstName("");
            request.setLastName("");

            RegistrationResult result = userService.registerUser(request);

            if (result.isSuccess()) {
                logger.info("✅ Registrering lyckades för: {}", username);
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("❌ Registreringsfel för '{}': {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * 🛡️ Hitta användare för autentisering
     */
    private User findUserForAuthentication(String identifier) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier", User.class);
            query.setParameter("identifier", identifier);

            @SuppressWarnings("unchecked")
            List<User> results = query.getResultList();

            return results.isEmpty() ? null : results.get(0);

        } catch (Exception e) {
            logger.error("❌ Fel vid användaresökning: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 🔍 Validera inloggningsuppgifter
     */
    public boolean validateCredentials(String username, String password) {
        return username != null && !username.trim().isEmpty() &&
                password != null && password.length() >= 6;
    }

    /**
     * 🔍 Kontrollera om användare existerar
     */
    public boolean userExists(String identifier) {
        return findUserForAuthentication(identifier) != null;
    }

    /**
     * 🔍 Kontrollera om användare är aktiv
     */
    public boolean isUserActive(String identifier) {
        User user = findUserForAuthentication(identifier);
        return user != null && user.isEnabled();
    }

    /**
     * 🔑 Kontrollera admin-rättigheter
     */
    public boolean isUserAdmin(String username) {
        User user = findUserForAuthentication(username);
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    /**
     * 📊 Hämta användarstatistik
     */
    public UserStats getUserStats() {
        try {
            Query countQuery = entityManager.createQuery("SELECT COUNT(u) FROM User u");
            long total = (Long) countQuery.getSingleResult();

            Query activeQuery = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.enabled = true");
            long active = (Long) activeQuery.getSingleResult();

            return UserStats.basic(total, active);
        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av användarstatistik: {}", e.getMessage());
            return UserStats.empty();
        }
    }

    /**
     * 📊 Logga användaraktivitet
     */
    public void logUserActivity(String username, String activity, String details) {
        logger.info("📊 User activity: {} - {} - {}", username, activity, details);
        loggingService.logUserAction(username, activity, details);
    }
}