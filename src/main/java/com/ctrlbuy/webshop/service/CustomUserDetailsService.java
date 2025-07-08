package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("DEBUG: Attempting to load user: " + username);

        // Sök efter användare med username ELLER email
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("DEBUG: User found: " + user.getUsername());
        System.out.println("DEBUG: User active: " + user.isActive());
        System.out.println("DEBUG: User email verified: " + user.isEmailVerified());

        // ✅ FIXAT: Kontrollera både role-fält och roles-lista
        System.out.println("DEBUG: User role (single): " + user.getRole());
        if (user.getRoles() != null) {
            System.out.println("DEBUG: User roles (list): " + user.getRoles());
        }

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        // ✅ FIXAT: Skapa authorities manuellt med rätt ROLE_ prefix
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Kontrollera både role-fält och roles-lista
        boolean isAdmin = false;

        // Kolla single role field
        if (user.getRole() != null) {
            String role = user.getRole().toString(); // ADMIN eller USER
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // ROLE_ADMIN eller ROLE_USER
            isAdmin = "ADMIN".equals(role);
        }

        // Kolla roles list (backup)
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (String role : user.getRoles()) {
                if (!role.startsWith("ROLE_")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                } else {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(role)) {
                    isAdmin = true;
                }
            }
        }

        // Fallback: alla användare ska ha åtminstone ROLE_USER
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Special case för admin username
        if ("admin".equals(user.getUsername()) || "superadmin".equals(user.getUsername())) {
            isAdmin = true;
            // Se till att admin har ROLE_ADMIN
            boolean hasAdminRole = authorities.stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
            if (!hasAdminRole) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        System.out.println("DEBUG: Is admin user: " + isAdmin);
        System.out.println("DEBUG: Final authorities: " + authorities);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)  // ✅ ANVÄND de manuellt skapade authorities
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())  // Bara kontrollera active, inte emailVerified
                .build();
    }
}