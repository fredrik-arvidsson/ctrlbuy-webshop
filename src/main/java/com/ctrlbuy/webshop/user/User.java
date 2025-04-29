package com.ctrlbuy.webshop.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webshop")
@Data
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(unique = true)
    private String username;

    @Column(columnDefinition = "VARCHAR(255)")
    private String role;

    @Column(columnDefinition = "VARCHAR(50)")
    private String firstName;

    @Column(columnDefinition = "VARCHAR(50)")
    private String lastName;

    // Behåll denna!
    protected User() {}

    public User(String email, String password, String username,
                String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = "USER"; // Standardroll
    }

    // Getters och setters + toString() skapas av @Data
}
