package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;

// ===== JPA IMPORTS =====
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
 * 🛡️ RAILWAY COMPATIBLE CustomUserDetailsService
 *
 * FIXED VERSION som använder EntityManager istället för Spring Data JPA
 * repository methods för att undvika "u1_0.createdAt" query generation problem.
 */
@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 🔐 RAILWAY SAFE loadUserByUsername
     *
     * Använder EntityManager med explicit JPQL queries istället för
     * Spring Data JPA method names som genererar problematiska column aliases.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("🔐 Spring Security försöker autentisera: {}", username);

        try {
            // 🛡️ SÄKER METOD 1: Först försök hitta med username
            User user = findUserByUsernameOrEmailSafe(username);

            if (user != null) {
                logger.info("✅ Användare hittades: {} (ID: {})", user.getUsername(), user.getId());
                logger.info("📊 Användare aktiv: {}, Email verifierad: {}", user.isActive(), user.isEmailVerified());

                // Logga roller för debugging
                if (user.getRole() != null) {
                    logger.info("📋 Användarens single role: {}", user.getRole());
                }
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                    logger.info("📋 Användarens roles lista: {}", user.getRoles());
                }

                return createUserDetails(user);
            }

            // Om ingen användare hittades
            logger.warn("⚠️ Användare hittades inte: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);

        } catch (UsernameNotFoundException e) {
            // Re-throw UsernameNotFoundException
            throw e;
        } catch (Exception e) {
            logger.error("❌ Fel vid autentisering av '{}': {}", username, e.getMessage(), e);
            throw new UsernameNotFoundException("Autentiseringsfel för: " + username, e);
        }
    }

    /**
     * 🛡️ SAFE findUserByUsernameOrEmailSafe
     *
     * Använder EntityManager för att undvika Spring Data JPA query generation.
     * Söker först med username, sedan med email om username inte hittas.
     */
    private User findUserByUsernameOrEmailSafe(String usernameOrEmail) {
        try {
            logger.info("🔍 Söker användare med username/email: {}", usernameOrEmail);

            // STEG 1: Försök hitta med username
            Query usernameQuery = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :identifier", User.class);
            usernameQuery.setParameter("identifier", usernameOrEmail);

            List<User> usernameResults = usernameQuery.getResultList();

            if (!usernameResults.isEmpty()) {
                User user = usernameResults.get(0);
                logger.info("✅ Användare hittades med username: {}", user.getUsername());
                return user;
            }

            logger.info("🔄 Username '{}' hittades inte, försöker med email...", usernameOrEmail);

            // STEG 2: Försök hitta med email
            Query emailQuery = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :identifier", User.class);
            emailQuery.setParameter("identifier", usernameOrEmail);

            List<User> emailResults = emailQuery.getResultList();

            if (!emailResults.isEmpty()) {
                User user = emailResults.get(0);
                logger.info("✅ Användare hittades med email: {} (username: {})",
                        user.getEmail(), user.getUsername());
                return user;
            }

            logger.warn("⚠️ Ingen användare hittades med identifier: {}", usernameOrEmail);
            return null;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av användare '{}': {}", usernameOrEmail, e.getMessage());
            return null;
        }
    }

    /**
     * 🔐 Enhanced createUserDetails med förbättrad rollhantering
     */
    private UserDetails createUserDetails(User user) {
        logger.info("🔨 Skapar UserDetails för användare: {}", user.getUsername());

        // ✅ FÖRBÄTTRAD: Skapa authorities manuellt med rätt ROLE_ prefix
        List<GrantedAuthority> authorities = new ArrayList<>();
        boolean isAdmin = false;

        // ===== ROLLHANTERING =====

        // METOD 1: Kolla single role field (om du har det)
        if (user.getRole() != null) {
            String role = user.getRole().toString(); // ADMIN eller USER
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(roleWithPrefix));

            logger.info("📋 Single role tillagd: {}", roleWithPrefix);

            if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(roleWithPrefix)) {
                isAdmin = true;
            }
        }

        // METOD 2: Kolla roles list (String List)
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (String role : user.getRoles()) {
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                // Undvik duplicates
                boolean alreadyExists = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals(roleWithPrefix));

                if (!alreadyExists) {
                    authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
                    logger.info("📋 Role från lista tillagd: {}", roleWithPrefix);
                }

                if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(roleWithPrefix)) {
                    isAdmin = true;
                }
            }
        }

        // METOD 3: Special case för admin username (backup)
        if ("admin".equals(user.getUsername()) ||
                "superadmin".equals(user.getUsername()) ||
                "fredrik".equals(user.getUsername())) {

            isAdmin = true;
            logger.info("🔑 Admin användare identifierad via username: {}", user.getUsername());

            // Se till att admin har ROLE_ADMIN
            boolean hasAdminRole = authorities.stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

            if (!hasAdminRole) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                logger.info("📋 ROLE_ADMIN tillagd för admin användare");
            }
        }

        // FALLBACK: Alla användare ska ha åtminstone ROLE_USER
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.info("📋 Fallback: ROLE_USER tillagd (inga andra roller hittades)");
        } else {
            // Se till att alla har ROLE_USER (om de inte bara har admin)
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

        // ✅ FIXAT: Använd isEnabled() istället för isActive() för Railway compatibility
        boolean isAccountEnabled = user.isEnabled(); // DETTA ÄR KEY FIXEN!

        logger.info("🔐 Account enabled: {}", isAccountEnabled);
        logger.info("📧 Email verified: {}", user.isEmailVerified());

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!isAccountEnabled)  // ✅ ANVÄND isEnabled() som matchar User entity
                .build();

        logger.info("✅ UserDetails skapat för användare: {}", user.getUsername());

        return userDetails;
    }

    /**
     * 🔍 SAFE validateUserExists - för extra validering
     */
    public boolean validateUserExists(String usernameOrEmail) {
        try {
            User user = findUserByUsernameOrEmailSafe(usernameOrEmail);
            return user != null;
        } catch (Exception e) {
            logger.error("❌ Fel vid validering av användare '{}': {}", usernameOrEmail, e.getMessage());
            return false;
        }
    }

    /**
     * 🔍 SAFE getUserByUsername - för andra services
     */
    public User getUserByUsername(String username) {
        try {
            return findUserByUsernameOrEmailSafe(username);
        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av användare '{}': {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * 🔐 SAFE isUserActive - för status kontroll
     */
    public boolean isUserActive(String username) {
        try {
            User user = findUserByUsernameOrEmailSafe(username);
            if (user == null) {
                return false;
            }

            boolean isActive = user.isEnabled(); // ✅ ANVÄND isEnabled()
            logger.info("📊 Användare '{}' är aktiv: {}", username, isActive);
            return isActive;

        } catch (Exception e) {
            logger.error("❌ Fel vid aktivitetskontroll för '{}': {}", username, e.getMessage());
            return false;
        }
    }
}