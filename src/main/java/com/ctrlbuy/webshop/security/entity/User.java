package com.ctrlbuy.webshop.security.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false, name = "enabled")
    private boolean enabled = true;

    @Column(name = "emailVerified")
    private boolean emailVerified = false;

    @Column(name = "verificationToken")
    private String verificationToken;

    @Column(name = "verificationTokenExpiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "resetToken")
    private String resetToken;

    @Column(name = "resetTokenExpiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "lastLoginAt")
    private LocalDateTime lastLoginAt;

    // Default constructor
    public User() {}

    // Constructor with all fields
    public User(Long id, String username, String email, String password, String firstName, String lastName, Role role, boolean enabled, boolean emailVerified, String verificationToken, LocalDateTime verificationTokenExpiry, String resetToken, LocalDateTime resetTokenExpiry, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.enabled = enabled;
        this.emailVerified = emailVerified;
        this.verificationToken = verificationToken;
        this.verificationTokenExpiry = verificationTokenExpiry;
        this.resetToken = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // Builder pattern
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Role role = Role.USER;
        private boolean enabled = true;
        private boolean emailVerified = false;
        private String verificationToken;
        private LocalDateTime verificationTokenExpiry;
        private String resetToken;
        private LocalDateTime resetTokenExpiry;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime lastLoginAt;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder username(String username) { this.username = username; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public UserBuilder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public UserBuilder verificationToken(String verificationToken) { this.verificationToken = verificationToken; return this; }
        public UserBuilder verificationTokenExpiry(LocalDateTime verificationTokenExpiry) { this.verificationTokenExpiry = verificationTokenExpiry; return this; }
        public UserBuilder resetToken(String resetToken) { this.resetToken = resetToken; return this; }
        public UserBuilder resetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder lastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }

        // ADD THIS METHOD for DataInitializer compatibility
        public UserBuilder active(boolean active) {
            this.enabled = active;
            return this;
        }

        public UserBuilder roles(List<String> roles) {
            if (roles != null && !roles.isEmpty()) {
                // Take first role and convert to enum
                String roleStr = roles.get(0).replace("ROLE_", "").toUpperCase();
                try {
                    this.role = Role.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    this.role = Role.USER; // Default fallback
                }
            }
            return this;
        }

        public User build() {
            return new User(id, username, email, password, firstName, lastName, role, enabled, emailVerified, verificationToken, verificationTokenExpiry, resetToken, resetTokenExpiry, createdAt, lastLoginAt);
        }
    }

    // All getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public LocalDateTime getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) { this.verificationTokenExpiry = verificationTokenExpiry; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // KRITISK: active property alias för enabled
    public boolean isActive() {
        return enabled;
    }

    public void setActive(boolean active) {
        this.enabled = active;
    }

    // Helper method för JPA Query kompatibilitet
    public boolean getActive() {
        return enabled;
    }

    public Set<String> getRoles() { return Set.of(role.name()); }
    public void addRole(String roleName) { this.role = Role.valueOf(roleName); }

    // NEW: For DataInitializer compatibility
    public void setRoles(List<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            String roleStr = roles.get(0).replace("ROLE_", "").toUpperCase();
            try {
                this.role = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                this.role = Role.USER;
            }
        }
    }

    public boolean isVerificationTokenValid() {
        return verificationToken != null && verificationTokenExpiry != null
                && LocalDateTime.now().isBefore(verificationTokenExpiry);
    }

    public boolean isResetTokenValid() {
        return resetToken != null && resetTokenExpiry != null
                && LocalDateTime.now().isBefore(resetTokenExpiry);
    }

    public enum Role {
        USER, ADMIN
    }
}