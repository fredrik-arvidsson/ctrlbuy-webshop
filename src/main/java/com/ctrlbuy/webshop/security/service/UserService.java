package com.ctrlbuy.webshop.security.service;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(RegisterRequest registerRequest) {
        // Kontrollera om användaren redan finns
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Användare med e-post " + registerRequest.getEmail() + " finns redan");
        }

        // Skapa ny användare med konstruktorn
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        // Spara användaren i databasen
        return userRepository.save(user);
    }

    /**
     * Registrerar ny användare och returnerar verifikationstoken
     */
    @Transactional
    public String registerNewUserWithToken(RegisterRequest request) {
        // Kontrollera om email redan finns
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("En användare med denna e-postadress finns redan");
        }

        // Kontrollera om användarnamn redan finns
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Användarnamnet är redan taget");
        }

        // Skapa ny användare
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false); // Inte aktiverad förrän e-post bekräftas
        user.setEmailVerified(false);

        // Generera verifikationstoken
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        // Lägg till standardroll
        user.addRole("ROLE_USER");

        userRepository.save(user);

        return verificationToken;
    }

    /**
     * Verifierar e-posttoken - MED DEBUG
     */
    @Transactional
    public boolean verifyEmailToken(String token) {
        System.out.println("🔧 UserService: Startar token-verifiering");
        System.out.println("🔧 UserService: Söker token: " + token);

        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        System.out.println("🔧 UserService: Användare hittad: " + userOpt.isPresent());

        if (userOpt.isEmpty()) {
            System.out.println("❌ UserService: Ingen användare hittades med token");
            return false;
        }

        User user = userOpt.get();
        System.out.println("🔧 UserService: Hittad användare: " + user.getEmail());
        System.out.println("🔧 UserService: Token expiry: " + user.getVerificationTokenExpiry());
        System.out.println("🔧 UserService: Nuvarande tid: " + LocalDateTime.now());

        // Kontrollera om token har gått ut
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            System.out.println("❌ UserService: Token har gått ut");
            return false;
        }

        System.out.println("✅ UserService: Token är giltig, aktiverar användare");

        // Aktivera användaren
        user.setActive(true);
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);
        System.out.println("✅ UserService: Användare sparad som aktiverad");

        return true;
    }

    /**
     * Skapar ny verifikationstoken för befintlig användare
     */
    @Transactional
    public String createNewVerificationToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Ingen användare hittades med e-postadressen: " + email);
        }

        User user = userOpt.get();

        if (user.isEmailVerified()) {
            throw new RuntimeException("E-postadressen är redan verifierad");
        }

        // Generera ny token
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        return newToken;
    }

    /**
     * Hämtar förnamn baserat på e-postadress
     */
    public String getFirstNameByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Ingen användare hittades med e-postadressen: " + email);
        }

        return userOpt.get().getFirstName();
    }

    /**
     * Hitta användare baserat på e-post
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Hitta användare baserat på användarnamn
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Kontrollera om e-post finns
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kontrollera om användarnamn finns
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // =====================================
    // ADMIN METODER - NYA FUNKTIONER
    // =====================================

    /**
     * Hämta alla användare
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Ta bort användare via ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Användare med ID " + userId + " finns inte");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Ta bort användare via e-post
     */
    @Transactional
    public void deleteUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Ingen användare hittades med e-postadressen: " + email);
        }
        userRepository.delete(userOpt.get());
    }

    /**
     * Aktivera/inaktivera användare
     */
    @Transactional
    public void toggleUserActive(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
    }

    /**
     * Återställ e-postverifiering
     */
    @Transactional
    public void resetUserVerification(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailVerified(false);
            user.setActive(false);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);
        }
    }
}