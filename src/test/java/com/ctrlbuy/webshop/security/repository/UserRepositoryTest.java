package com.ctrlbuy.webshop.security.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.ctrlbuy.webshop.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
@EntityScan("com.ctrlbuy.webshop")
@EnableJpaRepositories("com.ctrlbuy.webshop.security.repository")
public class UserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        log.debug("Kör testSaveAndFindUser");

        // Skapa ny användare
        User user = new User();
        user.setUsername("testuser123");
        user.setPassword("testpassword");
        user.setEmail("testuser123@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmailVerified(false);
        user.setActive(true);

        // Spara användaren
        User savedUser = userRepository.save(user);

        log.debug("Sparad användare med ID: {}", savedUser.getId());

        // Hämta användaren
        Optional<User> foundUser = userRepository.findByUsername("testuser123");

        // Verifiera att den finns och är korrekt
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser123@example.com");
        log.debug("Test slutfört framgångsrikt");
    }

    @Test
    public void testFindByEmail() {
        log.debug("Kör testFindByEmail");

        // Skapa ny användare
        User user = new User();
        user.setUsername("emailuser456");
        user.setPassword("password123");
        user.setEmail("emailtest456@example.com");
        user.setFirstName("Email");
        user.setLastName("User");
        user.setEmailVerified(true);
        user.setActive(true);

        // Spara användaren
        userRepository.save(user);

        // Hämta användaren med e-post
        Optional<User> foundUser = userRepository.findByEmail("emailtest456@example.com");

        // Verifiera att den finns och är korrekt
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailuser456");
        log.debug("Test slutfört framgångsrikt");
    }
}