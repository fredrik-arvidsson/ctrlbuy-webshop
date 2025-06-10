package com.ctrlbuy.webshop.security.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // NYA FÄLT: Tidsstämplar för skapande och uppdatering
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private List<String> roles = new ArrayList<>(List.of("USER"));

    // NYA METODER: Automatisk tidsstämpling
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active && emailVerified;
    }

    // ✅ KRITISKA KOMPATIBILITETSMETODER för UserService
    /**
     * Kompatibilitetsmetod för UserService - mappar active till enabled
     */
    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    // TILLAGDA: Manuella is-metoder för kompatibilitet med Boolean wrapper types
    public boolean isActive() {
        return active != null && active;
    }

    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }

    // Utility methods - FÖRBÄTTRAD VERSION
    public String getFullName() {
        if (firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty()) {
            return "%s %s".formatted(firstName.trim(), lastName.trim());
        }
        return username;
    }

    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public boolean isVerificationTokenValid() {
        return verificationToken != null &&
                verificationTokenExpiry != null &&
                LocalDateTime.now().isBefore(verificationTokenExpiry);
    }

    public boolean isResetTokenValid() {
        return resetToken != null &&
                resetTokenExpiry != null &&
                LocalDateTime.now().isBefore(resetTokenExpiry);
    }

    // NYA HJÄLPMETODER för datum - FÖRBÄTTRADE VERSIONER
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return createdAt.format(formatter);
        }
        return "Okänt datum";
    }

    public String getFormattedUpdatedAt() {
        if (updatedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            return updatedAt.format(formatter);
        }
        return "Aldrig uppdaterad";
    }

    // NY METOD: Kontrollerar om profilen är komplett
    public boolean hasCompleteProfile() {
        return firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty() &&
                emailVerified != null && emailVerified;
    }
}