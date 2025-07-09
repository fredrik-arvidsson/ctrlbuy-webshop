package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;

// ===== JPA IMPORTS =====
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

// ===== SPRING IMPORTS =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// ===== LOGGING IMPORTS =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 🛡️ RAILWAY COMPATIBLE UserDetailsServiceImpl
 *
 * Uppdaterad från dummy implementation till fullständig implementation
 * som använder EntityManager för säkra queries utan Spring Data JPA
 * query generation problem.
 *
 * OBS: Om du redan har CustomUserDetailsService med @Primary,
 * kan denna fungera som backup eller specifik implementation.
 */
@Service("userDetailsServiceImpl")  // Specifikt bean namn för att undvika konflikter
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 🔐 RAILWAY SAFE loadUserByUsername
     *
     * Ersätter "Dummy implementation" med fullständig implementation
     * som använder EntityManager för säkra database queries.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("🔐 UserDetailsServiceImpl: Autentiserar användare: {}", username);

        try {
            // 🛡️ SAFE QUERY: Använd EntityManager istället för repository
            User user = findUserSafe(username);

            if (user == null) {
                logger.warn("⚠️ Användare '{}' hittades inte i UserDetailsServiceImpl", username);
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Kontrollera att användaren är aktiv
            if (!user.isEnabled()) {
                logger.warn("⚠️ Användare '{}' är inaktiverad", username);
                throw new UsernameNotFoundException("User account is disabled: " + username);
            }

            logger.info("✅ UserDetailsServiceImpl: Användare hittades: {} (ID: {})",
                    user.getUsername(), user.getId());

            return buildUserDetails(user);

        } catch (UsernameNotFoundException e) {
            // Re-throw UsernameNotFoundException
            throw e;
        } catch (Exception e) {
            logger.error("❌ UserDetailsServiceImpl: Fel vid autentisering av '{}': {}",
                    username, e.getMessage(), e);
            throw new UsernameNotFoundException("Authentication error for user: " + username, e);
        }
    }

    /**
     * 🛡️ SAFE findUserSafe - Hitta användare med EntityManager
     *
     * Söker först med username, sedan med email som fallback.
     * Använder explicit JPQL för att undvika Spring Data JPA query generation.
     */
    private User findUserSafe(String identifier) {
        try {
            logger.info("🔍 UserDetailsServiceImpl: Söker användare: {}", identifier);

            // FÖRSÖK 1: Hitta med username
            Query usernameQuery = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :identifier AND u.enabled = true", User.class);
            usernameQuery.setParameter("identifier", identifier);

            List<User> usernameResults = usernameQuery.getResultList();

            if (!usernameResults.isEmpty()) {
                User user = usernameResults.get(0);
                logger.info("✅ Användare hittades med username: {}", user.getUsername());
                return user;
            }

            logger.info("🔄 Username inte hittat, försöker med email: {}", identifier);

            // FÖRSÖK 2: Hitta med email
            Query emailQuery = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :identifier AND u.enabled = true", User.class);
            emailQuery.setParameter("identifier", identifier);

            List<User> emailResults = emailQuery.getResultList();

            if (!emailResults.isEmpty()) {
                User user = emailResults.get(0);
                logger.info("✅ Användare hittades med email: {} (username: {})",
                        user.getEmail(), user.getUsername());
                return user;
            }

            logger.warn("⚠️ Ingen aktiv användare hittades med identifier: {}", identifier);
            return null;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av användare '{}': {}", identifier, e.getMessage());
            return null;
        }
    }

    /**
     * 🔨 buildUserDetails - Bygg Spring Security UserDetails
     *
     * Skapar UserDetails med proper authorities och account status.
     */
    private UserDetails buildUserDetails(User user) {
        logger.info("🔨 UserDetailsServiceImpl: Bygger UserDetails för: {}", user.getUsername());

        // ===== AUTHORITIES HANTERING =====
        List<GrantedAuthority> authorities = new ArrayList<>();
        boolean isAdmin = false;

        // METOD 1: Single role field
        if (user.getRole() != null) {
            String role = user.getRole().toString();
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(roleWithPrefix));

            logger.info("📋 Single role tillagd: {}", roleWithPrefix);

            if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(roleWithPrefix)) {
                isAdmin = true;
            }
        }

        // METOD 2: Roles list
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (String role : user.getRoles()) {
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                // Undvik duplicates
                boolean exists = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals(roleWithPrefix));

                if (!exists) {
                    authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
                    logger.info("📋 Role från lista tillagd: {}", roleWithPrefix);
                }

                if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(roleWithPrefix)) {
                    isAdmin = true;
                }
            }
        }

        // METOD 3: Admin username detection
        String username = user.getUsername().toLowerCase();
        if ("admin".equals(username) || "superadmin".equals(username) || "fredrik".equals(username)) {
            isAdmin = true;
            logger.info("🔑 Admin användare identifierad: {}", user.getUsername());

            boolean hasAdminRole = authorities.stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

            if (!hasAdminRole) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                logger.info("📋 ROLE_ADMIN tillagd för admin användare");
            }
        }

        // FALLBACK: Minimum ROLE_USER
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.info("📋 Fallback: ROLE_USER tillagd");
        } else {
            // Alla ska ha ROLE_USER som base
            boolean hasUserRole = authorities.stream()
                    .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority()));

            if (!hasUserRole) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                logger.info("📋 ROLE_USER tillagd som base role");
            }
        }

        logger.info("👤 Användare är admin: {}", isAdmin);
        logger.info("📋 Slutgiltiga authorities: {}", authorities);

        // ===== SKAPA SPRING SECURITY USERDETAILS =====
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())  // ✅ Använd isEnabled() för Railway compatibility
                .build();

        logger.info("✅ UserDetailsServiceImpl: UserDetails skapat för: {}", user.getUsername());

        return userDetails;
    }

    /**
     * 🔍 Extra helper methods för debugging och validation
     */

    /**
     * Kontrollera om användare existerar
     */
    public boolean userExists(String identifier) {
        try {
            User user = findUserSafe(identifier);
            return user != null;
        } catch (Exception e) {
            logger.error("❌ Fel vid kontroll av användare '{}': {}", identifier, e.getMessage());
            return false;
        }
    }

    /**
     * Hämta användare för andra services
     */
    public User getUser(String identifier) {
        try {
            return findUserSafe(identifier);
        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av användare '{}': {}", identifier, e.getMessage());
            return null;
        }
    }

    /**
     * Kontrollera användarstatus
     */
    public boolean isUserActive(String identifier) {
        try {
            User user = findUserSafe(identifier);
            if (user == null) {
                return false;
            }
            return user.isEnabled();
        } catch (Exception e) {
            logger.error("❌ Fel vid statuscontroll för '{}': {}", identifier, e.getMessage());
            return false;
        }
    }

    /**
     * Debug method för att logga alla aktiva användare
     */
    public void logAllActiveUsers() {
        try {
            Query query = entityManager.createQuery(
                    "SELECT u.username FROM User u WHERE u.enabled = true ORDER BY u.username");

            List<String> usernames = query.getResultList();
            logger.info("📊 UserDetailsServiceImpl: Aktiva användare ({}): {}",
                    usernames.size(), usernames);

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av aktiva användare: {}", e.getMessage());
        }
    }
}