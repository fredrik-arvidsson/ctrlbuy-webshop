package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registrera ny användare med säker transaktionshantering
     */
    @Transactional
    public RegistrationResult registerUser(RegisterRequest registerRequest) {
        try {
            log.info("🚀 Startar registrering för användare: {}", registerRequest.getUsername());

            // Validera input
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return RegistrationResult.failure("Användarnamn är obligatoriskt");
            }

            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                return RegistrationResult.failure("E-post är obligatorisk");
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
                return RegistrationResult.failure("Lösenord måste vara minst 6 tecken");
            }

            // Kontrollera om användare redan finns
            if (existsByUsernameIncludingInactive(registerRequest.getUsername())) {
                return RegistrationResult.failure("Användarnamnet är redan upptaget");
            }

            if (existsByEmailIncludingInactive(registerRequest.getEmail())) {
                return RegistrationResult.failure("E-postadressen är redan registrerad");
            }

            // Skapa ny användare
            User user = new User();
            user.setUsername(registerRequest.getUsername().trim());
            user.setEmail(registerRequest.getEmail().trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName() != null ? registerRequest.getFirstName().trim() : "");
            user.setLastName(registerRequest.getLastName() != null ? registerRequest.getLastName().trim() : "");

            // Sätt rolle - använd USER från User.Role enum
            user.setRole(User.Role.USER);

            user.setEnabled(true);
            user.setEmailVerified(true); // Auto-aktivera för demo
            user.setCreatedAt(LocalDateTime.now());

            // Spara med transaktion
            User savedUser = userRepository.save(user);
            log.info("✅ Användare sparad framgångsrikt: ID={}, Username={}", savedUser.getId(), savedUser.getUsername());

            return RegistrationResult.success("Registrering lyckades! Du kan nu logga in.");

        } catch (Exception e) {
            log.error("❌ Fel vid registrering av användare: {}", registerRequest.getUsername(), e);
            return RegistrationResult.failure("Ett fel uppstod vid registrering. Försök igen.");
        }
    }

    /**
     * Kontrollera om användarnamn finns (inklusive inaktiva användare)
     */
    @Transactional(readOnly = true)
    public boolean existsByUsernameIncludingInactive(String username) {
        if (username == null || username.trim().isEmpty()) {
            return true; // Behandla som upptaget för säkerhets skull
        }
        return userRepository.findByUsername(username.trim()).isPresent();
    }

    /**
     * Kontrollera om e-post finns (inklusive inaktiva användare)
     */
    @Transactional(readOnly = true)
    public boolean existsByEmailIncludingInactive(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Behandla som upptaget för säkerhets skull
        }
        return userRepository.findByEmail(email.trim().toLowerCase()).isPresent();
    }

    /**
     * Hitta användare via användarnamn
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    /**
     * Hitta användare via e-post
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Hitta användare via ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    // ===== SAKNADE METODER FÖR CONTROLLERS =====

    /**
     * Hämta alla användare (för AdminController)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("❌ Fel vid hämtning av alla användare", e);
            return new ArrayList<>();
        }
    }

    /**
     * Spara användare (för AdminVerificationController)
     */
    @Transactional
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("❌ Fel vid sparande av användare: {}", user.getUsername(), e);
            throw new RuntimeException("Kunde inte spara användare");
        }
    }

    /**
     * Verifiera e-post med token (för AuthController)
     */
    @Transactional
    public boolean verifyEmail(String token) {
        try {
            // Hitta användare med verifikationstoken
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> token.equals(u.getVerificationToken()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Kontrollera om token är giltig
                if (user.isVerificationTokenValid()) {
                    user.setEmailVerified(true);
                    user.setVerificationToken(null);
                    user.setVerificationTokenExpiry(null);
                    userRepository.save(user);

                    log.info("✅ E-post verifierad för användare: {}", user.getUsername());
                    return true;
                }
            }

            log.warn("⚠️ Ogiltig eller utgången verifikationstoken: {}", token);
            return false;

        } catch (Exception e) {
            log.error("❌ Fel vid e-postverifiering med token: {}", token, e);
            return false;
        }
    }

    /**
     * Generera återställningstoken (för AuthController)
     * Returnerar token som String istället för boolean
     */
    @Transactional
    public String generateResetToken(String email) {
        try {
            Optional<User> userOpt = findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Generera token och sätt giltighetsperiod
                String token = java.util.UUID.randomUUID().toString();
                user.setResetToken(token);
                user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // 24 timmar

                userRepository.save(user);

                log.info("✅ Återställningstoken genererad för: {}", email);
                return token; // Returnera token som String
            }

            log.warn("⚠️ Användare inte funnen för återställningstoken: {}", email);
            return null;

        } catch (Exception e) {
            log.error("❌ Fel vid generering av återställningstoken för: {}", email, e);
            return null;
        }
    }

    /**
     * Generera återställningstoken med användarnamn och e-post (för PasswordResetController)
     * Returnerar token som String istället för boolean
     */
    @Transactional
    public String generateResetTokenWithUsernameAndEmail(String username, String email) {
        try {
            // Hitta användare med både användarnamn och e-post
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> username.equals(u.getUsername()) && email.equals(u.getEmail()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                String token = java.util.UUID.randomUUID().toString();
                user.setResetToken(token);
                user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

                userRepository.save(user);

                log.info("✅ Återställningstoken genererad för användare: {} ({})", username, email);
                return token; // Returnera token som String
            }

            log.warn("⚠️ Användare inte funnen med användarnamn och e-post: {} / {}", username, email);
            return null;

        } catch (Exception e) {
            log.error("❌ Fel vid generering av återställningstoken för: {} / {}", username, email, e);
            return null;
        }
    }

    /**
     * Kontrollera om återställningstoken är giltig (för PasswordResetController)
     */
    @Transactional(readOnly = true)
    public boolean isValidResetToken(String token) {
        try {
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> token.equals(u.getResetToken()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean isValid = user.isResetTokenValid();

                log.debug("🔍 Återställningstoken {} är {}", token, isValid ? "giltig" : "ogiltig");
                return isValid;
            }

            log.warn("⚠️ Återställningstoken inte funnen: {}", token);
            return false;

        } catch (Exception e) {
            log.error("❌ Fel vid kontroll av återställningstoken: {}", token, e);
            return false;
        }
    }

    /**
     * Återställ lösenord med token (för PasswordResetController)
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        try {
            if (newPassword == null || newPassword.length() < 6) {
                log.warn("⚠️ Nytt lösenord för kort vid återställning");
                return false;
            }

            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> token.equals(u.getResetToken()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (user.isResetTokenValid()) {
                    // Uppdatera lösenord och rensa token
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetToken(null);
                    user.setResetTokenExpiry(null);

                    userRepository.save(user);

                    log.info("✅ Lösenord återställt för användare: {}", user.getUsername());
                    return true;
                } else {
                    log.warn("⚠️ Återställningstoken utgången för användare: {}", user.getUsername());
                }
            } else {
                log.warn("⚠️ Ingen användare funnen med återställningstoken: {}", token);
            }

            return false;

        } catch (Exception e) {
            log.error("❌ Fel vid återställning av lösenord med token: {}", token, e);
            return false;
        }
    }
}