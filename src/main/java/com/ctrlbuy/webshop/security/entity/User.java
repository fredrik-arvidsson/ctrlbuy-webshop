package com.ctrlbuy.webshop.security.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Användarklass som representerar en användare i systemet med
 * grundläggande information och säkerhetsattribut.
 */
@Entity
@Table(name = "users") // Bra att specificera tabellnamn för att undvika konflikter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean active;

    // Nya fält för namn (behövs för RegisterRequest)
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // Fält för e-postbekräftelse
    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;  // Boolean istället för boolean för att hantera null

    // Fält för lösenordsåterställning
    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    // Konstruktörer
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = false; // Inaktiv tills e-post bekräftas
        this.emailVerified = false;
        this.roles = new ArrayList<>();
        this.roles.add("ROLE_USER"); // Standardroll
    }

    public User(String username, String email, String password, String firstName, String lastName) {
        this(username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters och setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // För att stödja befintliga anrop i koden
    public void setEnabled(boolean enabled) { this.active = enabled; }
    public boolean isEnabled() { return active; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // Verifikationsfält
    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public LocalDateTime getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {
        this.verificationTokenExpiry = verificationTokenExpiry;
    }

    public Boolean isEmailVerified() { return emailVerified != null ? emailVerified : false; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    // Lösenordsåterställningsfält
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void addRole(String role) { this.roles.add(role); }

    // Hjälpmetoder
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isVerificationTokenValid() {
        return verificationToken != null &&
                verificationTokenExpiry != null &&
                verificationTokenExpiry.isAfter(LocalDateTime.now());
    }

    public boolean isResetTokenValid() {
        return resetToken != null &&
                resetTokenExpiry != null &&
                resetTokenExpiry.isAfter(LocalDateTime.now());
    }
}