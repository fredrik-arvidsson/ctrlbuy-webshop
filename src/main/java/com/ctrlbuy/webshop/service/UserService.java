package com.ctrlbuy.webshop.service;

// ===== DTO IMPORTS =====
import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;

// ===== SECURITY IMPORTS =====
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;

// ===== JPA IMPORTS =====
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// ===== SPRING IMPORTS =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ===== LOGGING IMPORTS =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ===== JAVA STANDARD LIBRARY IMPORTS =====
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // ===== REGISTRATION METHODS =====

    public RegistrationResult registerUser(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Registration failed - email already exists: {}", request.getEmail());
                return new RegistrationResult(false, "Email already exists");
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getId());

            return new RegistrationResult(true, "User registered successfully");

        } catch (Exception e) {
            logger.error("Error during user registration for email: {}", request.getEmail(), e);
            return new RegistrationResult(false, "Registration failed: " + e.getMessage());
        }
    }

    @Transactional
    public RegistrationResult registerNewUserWithToken(RegisterRequest request) {
        logger.info("Registering new user with token for email: {}", request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Användarnamnet är redan taget");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-postadressen är redan registrerad");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(true);
        user.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return new RegistrationResult(savedUser, token);
    }

    // ===== USER RETRIEVAL METHODS =====

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByEnabledFalse();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmailUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findByIdOptional(Long id) {
        return userRepository.findById(id);
    }

    public User findByResetToken(String token) {
        return userRepository.findByResetToken(token).orElse(null);
    }

    // ===== USER COUNT METHODS =====

    public long countAllUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    // ===== USER EXISTENCE CHECKS =====

    public boolean existsByUsername(String username) {
        return userRepository.findByUsernameAndActiveTrue(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email).isPresent();
    }

    public boolean existsByEmailIncludingInactive(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsernameIncludingInactive(String username) {
        return userRepository.existsByUsername(username);
    }

    // ===== USER SAVE METHODS =====

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // ===== USER STATUS MANAGEMENT =====

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        logger.info("User {} status toggled to: {}", userId, user.isEnabled());
    }

    @Transactional
    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User finns inte"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        logger.info("User {} active status toggled to: {}", userId, user.isActive());
    }

    @Transactional
    public boolean deactivateUser(long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if ("admin".equals(user.getUsername())) {
                return false;
            }

            if (user.getRoles() != null) {
                String roles = user.getRoles().toString().toLowerCase();
                if (roles.contains("admin") || roles.contains("role_admin")) {
                    return false;
                }
            }

            user.setActive(false);
            userRepository.save(user);
            logger.info("User {} deactivated", userId);
            return true;
        } catch (Exception e) {
            logger.error("Error deactivating user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    // ===== USER DELETION =====

    @Transactional
    public void deletePermanently(Long userId) {
        logger.info("🗑️ Startar permanent borttagning av user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Användare med ID " + userId + " hittades inte"));

        logger.info("👤 Hittat användare: {} {}", user.getFirstName(), user.getLastName());

        if ("fredrik".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("⛔ Kan inte radera huvudadmin 'fredrik'");
        }

        if (user.isActive()) {
            throw new RuntimeException("⛔ Kan endast radera inaktiva användare. Inaktivera användaren först.");
        }

        try {
            logger.info("🧹 Raderar order_items för användarens orders...");
            int deletedOrderItems = entityManager.createNativeQuery(
                            "DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE user_id = ?)")
                    .setParameter(1, userId)
                    .executeUpdate();
            logger.info("🧹 Raderade {} order_items", deletedOrderItems);

            logger.info("🧹 Raderar orders...");
            int deletedOrders = entityManager.createNativeQuery(
                            "DELETE FROM orders WHERE user_id = ?")
                    .setParameter(1, userId)
                    .executeUpdate();
            logger.info("🧹 Raderade {} orders", deletedOrders);

            // KOMMENTERAT BORT - REVIEWS-TABELLEN EXISTERAR INTE
            // logger.info("🧹 Raderar reviews...");
            // int deletedReviews = entityManager.createNativeQuery(
            //                 "DELETE FROM reviews WHERE user_id = ?")
            //         .setParameter(1, userId)
            //         .executeUpdate();
            // logger.info("🧹 Raderade {} reviews", deletedReviews);

            logger.info("🧹 Raderar user_roles...");
            int deletedRoles = entityManager.createNativeQuery(
                            "DELETE FROM user_roles WHERE user_id = ?")
                    .setParameter(1, userId)
                    .executeUpdate();
            logger.info("🧹 Raderade {} user_roles", deletedRoles);

            logger.info("🧹 Raderar användaren...");
            userRepository.deleteById(userId);

            logger.info("✅ Användare {} permanent borttagen!", userId);

        } catch (Exception e) {
            logger.error("❌ Fel vid permanent borttagning av användare {}: {}", userId, e.getMessage());
            throw new RuntimeException("Kunde inte radera användaren: " + e.getMessage(), e);
        }
    }

    // ===== EMAIL VERIFICATION =====

    @Transactional
    public boolean verifyEmail(String token) {
        logger.info("Attempting to verify email with token: {}", token);

        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            logger.warn("Invalid verification token: {}", token);
            return false;
        }

        User user = userOpt.get();
        if (!user.isVerificationTokenValid()) {
            logger.warn("Verification token expired for user: {}", user.getEmail());
            return false;
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        logger.info("Email verified successfully for user: {}", user.getEmail());
        return true;
    }

    @Transactional
    public String createNewVerificationToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        logger.info("New verification token created for user: {}", email);
        return newToken;
    }

    @Transactional
    public void resetUserVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        logger.info("User {} verification reset", userId);
    }

    // ===== PASSWORD RESET METHODS =====

    @Transactional
    public boolean initiatePasswordReset(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmailAndActiveTrue(email);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            logger.info("Password reset initiated for user: {}", email);
            return true;
        } catch (Exception e) {
            logger.error("Error initiating password reset for: {}", email, e);
            return false;
        }
    }

    @Transactional
    public String generateResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Användare med email " + email + " hittades inte");
        }

        User user = userOpt.get();

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        logger.info("Reset token generated for user: {}", email);
        return resetToken;
    }

    public boolean isValidResetToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            return false;
        }

        return userOpt.get().isResetTokenValid();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            logger.warn("Invalid reset token: {}", token);
            return false;
        }

        User user = userOpt.get();
        if (!user.isResetTokenValid()) {
            logger.warn("Reset token expired for user: {}", user.getEmail());
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        logger.info("Password reset successfully for user: {}", user.getEmail());
        return true;
    }

    // ===== SPRING SECURITY INTEGRATION =====

    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // ===== ADMIN AND ROLE MANAGEMENT =====

    public boolean isUserAdmin(long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    if ("admin".equals(user.getUsername())) {
                        return true;
                    }

                    if (user.getRoles() != null) {
                        String roles = user.getRoles().toString().toLowerCase();
                        return roles.contains("admin") || roles.contains("role_admin");
                    }

                    return false;
                })
                .orElse(false);
    }

    public boolean isUserAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if ("admin".equals(username) || "admin".equals(user.getUsername())) {
                        return true;
                    }

                    if (user.getRoles() != null) {
                        String roles = user.getRoles().toString().toLowerCase();
                        return roles.contains("admin") || roles.contains("role_admin");
                    }

                    return false;
                })
                .orElse(false);
    }

    // ===== UTILITY METHODS =====

    public String getFirstNameByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getFirstName)
                .orElse("Användare");
    }

    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long verifiedUsers = userRepository.countByEmailVerifiedTrue();

        return new UserStats(totalUsers, activeUsers, verifiedUsers);
    }

    // ===== INNER CLASSES =====

    /**
     * UserStats class for providing user statistics
     */
    public static class UserStats {
        private final long totalUsers;
        private final long activeUsers;
        private final long verifiedUsers;
        private final long inactiveUsers;
        private final long unverifiedUsers;

        public UserStats(long totalUsers, long activeUsers, long verifiedUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.verifiedUsers = verifiedUsers;
            this.inactiveUsers = totalUsers - activeUsers;
            this.unverifiedUsers = totalUsers - verifiedUsers;
        }

        // Standard getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getVerifiedUsers() { return verifiedUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public long getUnverifiedUsers() { return unverifiedUsers; }

        // Alternative method names for compatibility
        public long totalUsers() { return totalUsers; }
        public long activeUsers() { return activeUsers; }
        public long verifiedUsers() { return verifiedUsers; }
        public long inactiveUsers() { return inactiveUsers; }
        public long unverifiedUsers() { return unverifiedUsers; }

        @Override
        public String toString() {
            return "UserStats{totalUsers=" + totalUsers +
                    ", activeUsers=" + activeUsers +
                    ", verifiedUsers=" + verifiedUsers +
                    ", inactiveUsers=" + inactiveUsers +
                    ", unverifiedUsers=" + unverifiedUsers + "}";
        }
    }
}