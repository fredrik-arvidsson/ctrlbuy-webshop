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

    // Mock CommandLineRunner för att förhindra exekvering under tester
    @MockBean
    private CommandLineRunner commandLineRunner;

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
}