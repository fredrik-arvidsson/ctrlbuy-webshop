package com.ctrlbuy.webshop.security.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
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
        System.out.println("DEBUG: User roles: " + user.getRoles());

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        // VIKTIGT: Admin-användare ska alltid kunna logga in
        boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");

        System.out.println("DEBUG: Is admin user: " + isAdmin);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))
                .accountExpired(false)
                .accountLocked(!isAdmin && !user.isEmailVerified())  // Admin aldrig låst, andra låsta om ej verifierade
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}