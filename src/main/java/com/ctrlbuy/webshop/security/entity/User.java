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

    // ✅ COMMENTED OUT - dessa kolumner finns inte i din databas än
    /*
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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    */

    // ✅ TEMPORARY - håller email-verifiering i minnet tills databas uppdateras
    @Transient
    @Builder.Default
    private Boolean emailVerified = false;

    @Transient
    private String verificationToken;

    @Transient
    private LocalDateTime verificationTokenExpiry;

    @Transient
    private String resetToken;

    @Transient
    private LocalDateTime resetTokenExpiry;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private List<String> roles = new ArrayList<>(List.of("USER"));

    // ✅ TILLFÄLLIGT INAKTIVERAD - dessa körs bara om kolumnerna finns
    /*
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
    */

    // UserDetails implementation - FIXAD VERSION
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        return roles.stream()
                .map(role -> {
                    // FIX: Om rollen redan börjar med "ROLE_", lägg inte till igen
                    if (role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority(role);
                    } else {
                        return new SimpleGrantedAuthority("ROLE_" + role);
                    }
                })
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
        // ✅ FIX: Email-verifiering är inaktiverad - bara kontrollera active status
        return active != null && active;
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
        if (roles == null) {
            roles = new ArrayList<>();
        }
        // FIX: Normalisera roller utan "ROLE_" prefix för konsistens
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        if (!roles.contains(normalizedRole)) {
            roles.add(normalizedRole);
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

    // NYA HJÄLPMETODER för datum - TILLFÄLLIGA VERSIONER
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

    // ✅ FIX: hasCompleteProfile ignorerar email-verifiering när funktionen är inaktiverad
    public boolean hasCompleteProfile() {
        // Email-verifiering är inaktiverad - bara kontrollera namn
        return firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty();
        // Borttaget: && emailVerified != null && emailVerified;
    }

    // Hjälpmetod för att kontrollera text
    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}