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

    // 🚀 PRODUCTION READY - Alla advanced fields som @Transient för Railway/AWS compatibility
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

    // UserDetails implementation - PRODUCTION READY
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        return roles.stream()
                .map(role -> {
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
        // ✅ Production: Email verification disabled, only check active status
        return active != null && active;
    }

    // 🔧 COMPATIBILITY METHODS for Railway/AWS deployment
    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    public boolean isActive() {
        return active != null && active;
    }

    public boolean isEmailVerified() {
        // ✅ Always return true for production deployment
        return true;
    }

    // 🎯 UTILITY METHODS - Production optimized
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
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        if (!roles.contains(normalizedRole)) {
            roles.add(normalizedRole);
        }
    }

    public boolean isVerificationTokenValid() {
        // ✅ Production: Always return false to disable token verification
        return false;
    }

    public boolean isResetTokenValid() {
        // ✅ Production: Always return false to disable reset tokens
        return false;
    }

    // 📅 PRODUCTION DATE METHODS - Use current time as fallback
    public String getFormattedCreatedAt() {
        LocalDateTime dateToUse = createdAt != null ? createdAt : LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return dateToUse.format(formatter);
    }

    public String getFormattedUpdatedAt() {
        LocalDateTime dateToUse = updatedAt != null ? updatedAt : LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return dateToUse.format(formatter);
    }

    public boolean hasCompleteProfile() {
        // ✅ Production: Only check name fields, ignore email verification
        return firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty();
    }
}