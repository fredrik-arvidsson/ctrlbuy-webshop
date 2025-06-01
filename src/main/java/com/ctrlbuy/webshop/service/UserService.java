package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // NY METOD: Registrera användare med verifikationstoken
    public String registerNewUserWithToken(com.ctrlbuy.webshop.dto.RegisterRequest registerRequest) {
        // Kontrollera om username eller email redan finns
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Användarnamnet är redan taget");
        }
        if (existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("E-postadressen är redan registrerad");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .active(true)
                .emailVerified(false) // Kräver email-verifiering
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        // TODO: Implementera EmailService för att skicka verifieringsmail
        System.out.println("Verification token for " + registerRequest.getEmail() + ": " + verificationToken);

        return verificationToken;
    }

    // NY METOD: Verifiera email-token
    public boolean verifyEmailToken(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isVerificationTokenValid()) {
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setVerificationTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // NY METOD: Skapa ny verifikationstoken
    public String createNewVerificationToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String newToken = UUID.randomUUID().toString();
            user.setVerificationToken(newToken);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            // TODO: Implementera EmailService för att skicka nytt verifieringsmail
            System.out.println("New verification token for " + email + ": " + newToken);

            return newToken;
        }
        return null;
    }

    // NY METOD: Hämta förnamn via email
    public String getFirstNameByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(User::getFirstName).orElse("Användare");
    }

    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isVerificationTokenValid()) {
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setVerificationTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            // TODO: Implementera EmailService för att skicka email
            System.out.println("Password reset token for " + email + ": " + resetToken);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isResetTokenValid()) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetToken(null);
                user.setResetTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // NY METOD: Hämta alla användare (för admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // NY METOD: Ta bort användare (för admin)
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    // NY METOD: Ta bort användare via email (för admin)
    public boolean deleteUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }

    // NY METOD: Toggla användarens aktiva status (för admin)
    public boolean toggleUserActive(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(!user.getActive()); // Toggla active-status
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // NY METOD: Återställ användarens email-verifiering (för admin)
    public boolean resetUserVerification(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailVerified(false);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}