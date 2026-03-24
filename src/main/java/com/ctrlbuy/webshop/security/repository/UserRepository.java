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

    // ✅ SPRING DATA JPA DERIVED QUERIES - fungerar automatiskt
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndEnabledTrue(String username);
    Optional<User> findByEmailAndEnabledTrue(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);

    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Optional<User> findByOauthProviderAndOauthProviderId(String oauthProvider, String oauthProviderId);

    long countByEnabledTrue();
    long countByEnabledFalse();

    // ✅ NYA METODER för email-verifiering (används av UserService)
    long countByEmailVerifiedTrue();
    long countByEmailVerifiedFalse();
    List<User> findByEmailVerifiedFalse();

    // 🔐 SÄKER LÖSENORDSÅTERSTÄLLNING - FIXED JPQL QUERIES

    /**
     * Säker metod för lösenordsåterställning
     * Kräver att BÅDE username OCH email matchar samma användare
     * KOMPATIBILITETSVERSION - returnerar User direkt (eller null)
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email")
    User findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    /**
     * Modern version - returnerar Optional<User>
     * Rekommenderas för ny kod
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email")
    Optional<User> findByUsernameAndEmailOptional(@Param("username") String username, @Param("email") String email);

    /**
     * Extra säkerhetsvariant - kräver även att användaren är aktiv
     * FIXED: Använder enabled istället för active
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email AND u.enabled = true")
    Optional<User> findByUsernameAndEmailAndEnabledTrue(@Param("username") String username, @Param("email") String email);

    /**
     * Kompatibilitetsversion för aktiv användare - returnerar User direkt
     * FIXED: Använder enabled istället för active
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email AND u.enabled = true")
    User findByUsernameAndEmailAndEnabledTrueUser(@Param("username") String username, @Param("email") String email);

    // ✅ ENKLA QUERIES som definitivt fungerar
    // FIXED: Alla queries använder enabled istället för active

    /**
     * Hitta användare som inte verifierat sin email på länge
     * FIXED: Använder entity field names
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.enabled = true AND u.verificationTokenExpiry < CURRENT_TIMESTAMP")
    List<User> findUsersWithExpiredVerificationTokens();

    // ✅ YTTERLIGARE SÄKERHETSMETODER

    /**
     * Kontrollera om användarnamn + email-kombination existerar
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.email = :email")
    boolean existsByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    /**
     * Kontrollera om användarnamn + email-kombination existerar för aktiv användare
     * FIXED: Använder enabled istället för active
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.email = :email AND u.enabled = true")
    boolean existsByUsernameAndEmailAndEnabledTrue(@Param("username") String username, @Param("email") String email);
}