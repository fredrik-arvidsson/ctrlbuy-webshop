package com.ctrlbuy.webshop.security.repository;

import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ BEFINTLIGA METODER från er nuvarande struktur
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndActiveTrue(String username);
    Optional<User> findByEmailAndActiveTrue(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);

    List<User> findByActiveTrue();
    List<User> findByActiveFalse();

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    long countByActiveTrue();
    long countByActiveFalse();

    // ✅ NYA METODER för email-verifiering (används av UserService)
    long countByEmailVerifiedTrue();
    long countByEmailVerifiedFalse();
    List<User> findByEmailVerifiedFalse();

    // ✅ KOMPATIBILITETSMETODER för UserService (mappar enabled -> active)

    /**
     * Kompatibilitetsmetod - mappar till findByActiveTrue()
     * UserService förväntar sig denna metod
     */
    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findByEnabledTrue();

    /**
     * Kompatibilitetsmetod - mappar till findByActiveFalse()
     * UserService förväntar sig denna metod
     */
    @Query("SELECT u FROM User u WHERE u.active = false")
    List<User> findByEnabledFalse();

    // ✅ ENKLA QUERIES som definitivt fungerar

    /**
     * Hitta användare som inte verifierat sin email på länge
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.active = true AND u.verificationTokenExpiry < CURRENT_TIMESTAMP")
    List<User> findUsersWithExpiredVerificationTokens();
}