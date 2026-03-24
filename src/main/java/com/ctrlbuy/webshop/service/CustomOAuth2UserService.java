package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String pictureUrl = oAuth2User.getAttribute("picture");

        logger.info("OAuth2 login attempt: provider={}, email={}", provider, email);

        User user = findOrCreateUser(provider, providerId, email, firstName, lastName, pictureUrl);

        return new DefaultOAuth2User(
                user.getAuthorities(),
                oAuth2User.getAttributes(),
                "sub"
        );
    }

    private User findOrCreateUser(String provider, String providerId, String email,
                                   String firstName, String lastName, String pictureUrl) {
        // 1. Look up by OAuth provider + provider ID
        Optional<User> existingByProvider = userRepository.findByOauthProviderAndOauthProviderId(provider, providerId);
        if (existingByProvider.isPresent()) {
            User user = existingByProvider.get();
            user.setProfilePictureUrl(pictureUrl);
            user.setLastLoginAt(LocalDateTime.now());
            logger.info("Existing OAuth2 user logged in: {}", user.getUsername());
            return userRepository.save(user);
        }

        // 2. Look up by email — link OAuth to existing account
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            User user = existingByEmail.get();
            user.setOauthProvider(provider);
            user.setOauthProviderId(providerId);
            user.setProfilePictureUrl(pictureUrl);
            user.setLastLoginAt(LocalDateTime.now());
            logger.info("Linked OAuth2 provider to existing user: {}", user.getUsername());
            return userRepository.save(user);
        }

        // 3. Create new user
        String username = generateUniqueUsername(email);
        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .firstName(firstName != null ? firstName : "")
                .lastName(lastName != null ? lastName : "")
                .role(User.Role.USER)
                .enabled(true)
                .emailVerified(true)
                .oauthProvider(provider)
                .oauthProviderId(providerId)
                .profilePictureUrl(pictureUrl)
                .createdAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        logger.info("Created new OAuth2 user: {} ({})", username, email);
        return userRepository.save(newUser);
    }

    private String generateUniqueUsername(String email) {
        String base = email.split("@")[0];
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter;
            counter++;
        }
        return username;
    }
}
