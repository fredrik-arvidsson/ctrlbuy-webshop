package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@TestConfiguration
@Profile("test")
public class ReviewTestConfiguration {

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Snabbare encoding för tester
    }

    @Bean
    public User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password123");
        user.setEmailVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    @Bean
    public User createTestUser2() {
        User user = new User();
        user.setId(2L);
        user.setEmail("test2@example.com");
        user.setUsername("testuser2");
        user.setFirstName("Test");
        user.setLastName("User2");
        user.setPassword("password123");
        user.setEmailVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    @Bean
    public Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("A test product for reviews");
        product.setPrice(99.99);
        product.setStock(10);
        product.setActive(true);
        return product;
    }

    @Bean
    public Product createTestProduct2() {
        Product product = new Product();
        product.setId(2L);
        product.setName("Test Product 2");
        product.setDescription("Another test product");
        product.setPrice(149.99);
        product.setStock(5);
        product.setActive(true);
        return product;
    }
}