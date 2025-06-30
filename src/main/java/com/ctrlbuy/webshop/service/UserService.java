package com.ctrlbuy.webshop.service;

// ===== DTO IMPORTS =====
import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;

// ===== SECURITY IMPORTS =====
import com.ctrlbuy.webshop.security.entity.User;
// Role-entitet behövs inte längre - User använder List<String> för roller
import com.ctrlbuy.webshop.security.repository.UserRepository;
// RoleRepository behövs inte längre - User hanterar roller som strings

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

    // RoleRepository behövs inte längre - User använder List<String> för roller

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // ===== REGISTRATION METHODS =====

    public RegistrationResult registerUser(RegisterRequest request) {
        logger.info("Attempting to register user with username: {} and email: {}", request.getUsername(), request.getEmail());

        try {
            // 🔍 Kontrollera att username inte är null
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.error("Username is null or empty in RegisterRequest!");
                return new RegistrationResult(false, "Användarnamn är obligatoriskt");
            }

            // 🔍 Kontrollera att email inte är null
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                logger.error("Email is null or empty in RegisterRequest!");
                return new RegistrationResult(false, "E-postadress är obligatorisk");
            }

            // Kontrollera om username redan finns
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                logger.warn("Registration failed - username already exists: {}", request.getUsername());
                return new RegistrationResult(false, "Användarnamnet är redan upptaget");
            }

            // Kontrollera om email redan finns
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Registration failed - email already exists: {}", request.getEmail());
                return new RegistrationResult(false, "E-postadressen är redan registrerad");
            }

            // 🛠️ Skapa User-objekt med ALLA obligatoriska fält
            User user = new User();
            user.setUsername(request.getUsername());     // ✅ DETTA SAKNADES!
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setActive(true);                        // ✅ Sätt active istället för enabled
            user.setEmailVerified(true);                 // ✅ Aktivera direkt utan email-verifiering
            user.setCreatedAt(LocalDateTime.now());

            logger.info("Creating user with username: '{}', email: '{}', active: {}, emailVerified: {}",
                    user.getUsername(), user.getEmail(), user.isActive(), user.isEmailVerified());

            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {} and username: {}", savedUser.getId(), savedUser.getUsername());

            return new RegistrationResult(true, "Användare registrerad framgångsrikt");

        } catch (Exception e) {
            logger.error("Error during user registration for username: {} and email: {}", request.getUsername(), request.getEmail(), e);
            return new RegistrationResult(false, "Registrering misslyckades: " + e.getMessage());
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

    // ===== PASSWORD UPDATE METHODS ===== 🔥 NYA METODER

    /**
     * Uppdaterar lösenord för en användare (för admin-panelen)
     * @param userId Användar-ID
     * @param newPassword Nytt lösenord (ohashad)
     * @return true om lyckad, false om misslyckad
     */
    @Transactional
    public boolean updatePassword(Long userId, String newPassword) {
        try {
            logger.info("🔐 Uppdaterar lösenord för userId: {}", userId);

            // Validera input
            if (userId == null) {
                logger.error("❌ UserId är null");
                return false;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.error("❌ Nytt lösenord är tomt");
                return false;
            }

            // Hitta användaren
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                logger.error("❌ Användare med ID {} hittades inte", userId);
                return false;
            }

            User user = userOpt.get();
            logger.info("👤 Hittat användare: {} ({})", user.getUsername(), user.getEmail());

            // Hasha nya lösenordet
            String hashedPassword = passwordEncoder.encode(newPassword.trim());
            logger.info("🔒 Lösenord hashat");

            // Uppdatera lösenord
            user.setPassword(hashedPassword);

            // Spara användaren
            User savedUser = userRepository.save(user);
            logger.info("✅ Lösenord uppdaterat för användare: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid uppdatering av lösenord för userId {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Uppdaterar lösenord för en användare med användarnamn
     * @param username Användarnamn
     * @param newPassword Nytt lösenord (ohashad)
     * @return true om lyckad, false om misslyckad
     */
    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        try {
            logger.info("🔐 Uppdaterar lösenord för användarnamn: {}", username);

            // Validera input
            if (username == null || username.trim().isEmpty()) {
                logger.error("❌ Användarnamn är tomt");
                return false;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.error("❌ Nytt lösenord är tomt");
                return false;
            }

            // Hitta användaren
            Optional<User> userOpt = userRepository.findByUsername(username.trim());
            if (userOpt.isEmpty()) {
                logger.error("❌ Användare med användarnamn '{}' hittades inte", username);
                return false;
            }

            User user = userOpt.get();
            logger.info("👤 Hittat användare: {} (ID: {}, Email: {})", user.getUsername(), user.getId(), user.getEmail());

            // Hasha nya lösenordet
            String hashedPassword = passwordEncoder.encode(newPassword.trim());
            logger.info("🔒 Lösenord hashat");

            // Uppdatera lösenord
            user.setPassword(hashedPassword);

            // Spara användaren
            User savedUser = userRepository.save(user);
            logger.info("✅ Lösenord uppdaterat för användare: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid uppdatering av lösenord för användarnamn {}: {}", username, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Uppdaterar lösenord med gammalt lösenord-verifiering (för användarens egen profil)
     * @param username Användarnamn
     * @param currentPassword Nuvarande lösenord (för verifiering)
     * @param newPassword Nytt lösenord
     * @return true om lyckad, false om misslyckad
     * @throws IllegalArgumentException om gammalt lösenord är fel
     */
    @Transactional
    public boolean updatePassword(String username, String currentPassword, String newPassword) throws IllegalArgumentException {
        try {
            logger.info("🔐 Uppdaterar lösenord med verifiering för användarnamn: {}", username);

            // Validera input
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Användarnamn är obligatoriskt");
            }

            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Nuvarande lösenord är obligatoriskt");
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Nytt lösenord är obligatoriskt");
            }

            // Hitta användaren
            Optional<User> userOpt = userRepository.findByUsername(username.trim());
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("Användare hittades inte");
            }

            User user = userOpt.get();
            logger.info("👤 Hittat användare: {} (ID: {})", user.getUsername(), user.getId());

            // Verifiera nuvarande lösenord
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.warn("⚠️ Fel nuvarande lösenord för användare: {}", username);
                throw new IllegalArgumentException("Nuvarande lösenord är felaktigt");
            }

            // Kontrollera att nytt lösenord är annorlunda
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new IllegalArgumentException("Nytt lösenord måste vara annorlunda än det nuvarande");
            }

            // Hasha nya lösenordet
            String hashedPassword = passwordEncoder.encode(newPassword.trim());
            logger.info("🔒 Nytt lösenord hashat");

            // Uppdatera lösenord
            user.setPassword(hashedPassword);

            // Spara användaren
            User savedUser = userRepository.save(user);
            logger.info("✅ Lösenord uppdaterat med verifiering för användare: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

            return true;

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Validering misslyckades för lösenordsuppdatering för {}: {}", username, e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("❌ Fel vid verifierad lösenordsuppdatering för {}: {}", username, e.getMessage(), e);
            return false;
        }
    }

    // ===== ROLE MANAGEMENT METHODS ===== 🔥 NYA METODER FÖR ROLES

    /**
     * Ger admin-rättigheter till en användare (ANPASSAD FÖR STRING ROLES)
     * @param userId Användar-ID
     * @return true om lyckad, false om misslyckad
     */
    @Transactional
    public boolean addAdminRole(Long userId) {
        try {
            logger.info("🔑 Försöker ge admin-rättigheter till userId: {}", userId);

            User user = findById(userId);
            if (user == null) {
                logger.error("❌ Användare med ID {} hittades inte", userId);
                return false;
            }

            logger.info("👤 Hittat användare: {} ({})", user.getUsername(), user.getEmail());

            // Kontrollera om användaren redan har admin-roll (STRING VERSION)
            boolean hasAdminRole = user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equalsIgnoreCase(role) ||
                            "ROLE_ADMIN".equalsIgnoreCase(role));

            if (hasAdminRole) {
                logger.info("ℹ️ Användare {} har redan admin-rättigheter", user.getUsername());
                return true; // Redan admin, räknas som lyckat
            }

            // Lägg till admin-roll (STRING VERSION)
            user.addRole("ADMIN");
            save(user);

            logger.info("✅ Admin-rättigheter tillagda för användare: {}", user.getUsername());
            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid tillägg av admin-roll för userId {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tar bort admin-rättigheter från en användare (ANPASSAD FÖR STRING ROLES)
     * @param userId Användar-ID
     * @return true om lyckad, false om misslyckad
     */
    @Transactional
    public boolean removeAdminRole(Long userId) {
        try {
            logger.info("🔑 Försöker ta bort admin-rättigheter från userId: {}", userId);

            User user = findById(userId);
            if (user == null) {
                logger.error("❌ Användare med ID {} hittades inte", userId);
                return false;
            }

            logger.info("👤 Hittat användare: {} ({})", user.getUsername(), user.getEmail());

            // Skydda huvudadmin
            if ("fredrik".equalsIgnoreCase(user.getUsername())) {
                logger.warn("⛔ Kan inte ta bort admin-rättigheter från huvudadmin 'fredrik'");
                return false;
            }

            // Ta bort admin-roller (STRING VERSION)
            boolean removedAny = user.getRoles().removeIf(role ->
                    "ADMIN".equalsIgnoreCase(role) ||
                            "ROLE_ADMIN".equalsIgnoreCase(role));

            if (removedAny) {
                save(user);
                logger.info("✅ Admin-rättigheter borttagna för användare: {}", user.getUsername());
            } else {
                logger.info("ℹ️ Användare {} hade inga admin-rättigheter att ta bort", user.getUsername());
            }

            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid borttagning av admin-roll för userId {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Kontrollerar om en användare har admin-rättigheter (ANPASSAD FÖR STRING ROLES)
     * @param userId Användar-ID
     * @return true om användaren är admin
     */
    public boolean hasAdminRole(Long userId) {
        try {
            User user = findById(userId);
            if (user == null) {
                return false;
            }

            // Kolla användarnamn först (fallback för huvudadmin)
            if ("fredrik".equalsIgnoreCase(user.getUsername()) ||
                    "admin".equalsIgnoreCase(user.getUsername())) {
                return true;
            }

            // Kolla roller (STRING VERSION)
            return user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equalsIgnoreCase(role) ||
                            "ROLE_ADMIN".equalsIgnoreCase(role));

        } catch (Exception e) {
            logger.error("❌ Fel vid kontroll av admin-roll för userId {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Kontrollerar om en användare har admin-rättigheter med användarnamn (ANPASSAD FÖR STRING ROLES)
     * @param username Användarnamn
     * @return true om användaren är admin
     */
    public boolean hasAdminRole(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return false;
            }

            // Kolla användarnamn först (fallback för huvudadmin)
            if ("fredrik".equalsIgnoreCase(username.trim()) ||
                    "admin".equalsIgnoreCase(username.trim())) {
                return true;
            }

            Optional<User> userOpt = findByUsername(username.trim());
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();

            // Kolla roller (STRING VERSION)
            return user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equalsIgnoreCase(role) ||
                            "ROLE_ADMIN".equalsIgnoreCase(role));

        } catch (Exception e) {
            logger.error("❌ Fel vid kontroll av admin-roll för username {}: {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Växlar admin-status för en användare
     * @param userId Användar-ID
     * @return true om lyckad, false om misslyckad
     */
    @Transactional
    public boolean toggleAdminRole(Long userId) {
        try {
            logger.info("🔄 Växlar admin-status för userId: {}", userId);

            if (hasAdminRole(userId)) {
                logger.info("👤 Användare har admin-rättigheter, tar bort dem...");
                return removeAdminRole(userId);
            } else {
                logger.info("👤 Användare saknar admin-rättigheter, lägger till dem...");
                return addAdminRole(userId);
            }

        } catch (Exception e) {
            logger.error("❌ Fel vid växling av admin-roll för userId {}: {}", userId, e.getMessage());
            return false;
        }
    }

    // ===== USER RETRIEVAL METHODS =====

    public List<User> getAllUsers() {
        logger.info("🔍 Hämtar alla användare");
        List<User> users = userRepository.findAll();
        logger.info("🔍 Hittade {} användare totalt", users.size());
        return users;
    }

    public List<User> getActiveUsers() {
        logger.info("🔍 Hämtar aktiva användare");
        List<User> users = userRepository.findByActiveTrue();
        logger.info("🔍 Hittade {} aktiva användare", users.size());
        return users;
    }

    public List<User> getInactiveUsers() {
        logger.info("🔍 Hämtar inaktiva användare");
        List<User> users = userRepository.findByActiveFalse(); // ✅ FIXAT: Använd 'active' istället för 'enabled'
        logger.info("🔍 Hittade {} inaktiva användare", users.size());
        return users;
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
        long count = userRepository.count();
        logger.info("📊 Totalt antal användare: {}", count);
        return count;
    }

    public long countActiveUsers() {
        long count = userRepository.countByActiveTrue();
        logger.info("📊 Antal aktiva användare: {}", count);
        return count;
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
        boolean oldStatus = user.isActive();
        user.setActive(!user.isActive());
        userRepository.save(user);
        logger.info("User {} active status toggled from {} to: {}", userId, oldStatus, user.isActive());
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

    /**
     * SÄKER LÖSENORDSÅTERSTÄLLNING - Kräver BÅDE användarnamn OCH email
     * @param username Användarnamn
     * @param email E-postadress
     * @return Reset-token om användare hittas, annars null
     */
    @Transactional
    public String generateResetTokenWithUsernameAndEmail(String username, String email) {
        try {
            logger.info("🔐 Försöker generera reset-token för användarnamn: {} och email: {}", username, email);

            // Hitta användare med BÅDE username OCH email
            User user = userRepository.findByUsernameAndEmail(username, email);

            if (user == null) {
                logger.warn("⚠️ Ingen användare hittades med användarnamn: {} och email: {}", username, email);
                return null;
            }

            // Kontrollera att användaren är aktiv
            if (!user.isActive()) {
                logger.warn("⚠️ Användare {} är inaktiv", username);
                return null;
            }

            // Generera säker token
            String resetToken = UUID.randomUUID().toString();

            // Sätt token och utgångstid (1 timme)
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

            // Spara användaren
            userRepository.save(user);

            logger.info("✅ Reset-token genererad för användare: {} med email: {}", username, email);
            return resetToken;

        } catch (Exception e) {
            logger.error("❌ Fel vid generering av reset-token för {}/{}: {}", username, email, e.getMessage());
            return null;
        }
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