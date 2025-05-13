package com.ctrlbuy.webshop.security.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.ctrlbuy.webshop.config.TestConfig;
import com.ctrlbuy.webshop.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        log.debug("Kör testSaveAndFindUser");

        // Skapa ny användare
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@example.com");

        // Spara användaren
        User savedUser = userRepository.save(user);
        log.debug("Sparad användare med ID: {}", savedUser.getId());

        // Hämta användaren
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Verifiera att den finns och är korrekt
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
        log.debug("Test slutfört framgångsrikt");
    }

    @Test
    public void testFindByEmail() {
        log.debug("Kör testFindByEmail");

        // Skapa ny användare
        User user = new User();
        user.setUsername("emailuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        // Spara användaren
        userRepository.save(user);

        // Hämta användaren med e-post (förutsätter att du har en sådan metod)
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Verifiera att den finns och är korrekt
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailuser");
        log.debug("Test slutfört framgångsrikt");
    }
}