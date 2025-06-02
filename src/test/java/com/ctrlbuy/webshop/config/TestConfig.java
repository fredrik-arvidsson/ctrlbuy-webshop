package com.ctrlbuy.webshop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.ctrlbuy.webshop.security.filter.JwtAuthenticationFilter;

@TestConfiguration
public class TestConfig {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Mock BÅDA CommandLineRunner beans för att förhindra exekvering under tester
    @MockBean(name = "commandLineRunner")
    private CommandLineRunner commandLineRunner;

    @MockBean(name = "init")
    private CommandLineRunner initCommandLineRunner;

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Tillhandahåller en HandlerMappingIntrospector för Spring Security MvcRequestMatcher.
     * Detta löser problemet med "No bean named mvcHandlerMappingIntrospector" i testerna.
     */
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    /**
     * Skapar en primär CommandLineRunner som inte gör något för tester.
     * Detta förhindrar konflikter mellan flera CommandLineRunner beans.
     */
    @Bean
    @Primary
    public CommandLineRunner testCommandLineRunner() {
        return args -> {
            // Gör ingenting under tester - förhindrar data-initialisering
            System.out.println("Test CommandLineRunner: Skippar data-initialisering");
        };
    }
}