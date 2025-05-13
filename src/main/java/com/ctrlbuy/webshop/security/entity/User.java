package com.ctrlbuy.webshop.security.entity;

import jakarta.persistence.*;
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
        this.active = true;
        this.roles = new ArrayList<>();
        this.roles.add("ROLE_USER"); // Standardroll
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

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void addRole(String role) { this.roles.add(role); }
}