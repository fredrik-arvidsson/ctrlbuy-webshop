package com.ctrlbuy.webshop.security.repository;

import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Befintliga metoder
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Lägg till dessa metoder för att enkelt kontrollera om ett användarnamn eller en e-post redan finns
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Nya metoder för token-hantering
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);

    // Hitta användare med giltiga verifikationstokens
    @Query("SELECT u FROM User u WHERE u.verificationToken = ?1 AND u.verificationTokenExpiry > ?2")
    Optional<User> findByValidVerificationToken(String token, LocalDateTime now);

    // Hitta användare med giltiga reset-tokens
    @Query("SELECT u FROM User u WHERE u.resetToken = ?1 AND u.resetTokenExpiry > ?2")
    Optional<User> findByValidResetToken(String token, LocalDateTime now);

    // Hitta användare med utgångna tokens för cleanup
    @Query("SELECT u FROM User u WHERE u.verificationTokenExpiry < ?1")
    List<User> findUsersWithExpiredVerificationTokens(LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.resetTokenExpiry < ?1")
    List<User> findUsersWithExpiredResetTokens(LocalDateTime now);

    // Hitta ej verifierade användare (för eventuell cleanup)
    List<User> findByEmailVerifiedFalse();

    // Hitta aktiva, verifierade användare
    List<User> findByActiveAndEmailVerified(boolean active, boolean emailVerified);
}