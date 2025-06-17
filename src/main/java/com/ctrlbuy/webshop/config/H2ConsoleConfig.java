package com.ctrlbuy.webshop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class H2ConsoleConfig {

    /**
     * Separat säkerhetskonfiguration för H2-konsollen
     * Denna körs med högre prioritet (@Order(1)) än huvud-säkerhetskonfigurationen
     */
    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Matcha endast H2-konsoll URL:er
                .securityMatcher("/h2-console/**")

                // Tillåt alla requests till H2-konsollen
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Inaktivera CSRF för H2-konsollen (behövs för att den ska fungera)
                .csrf(csrf -> csrf.disable())

                // Tillåt H2-konsollen att köras i frames (behövs för UI:t)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                        .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable())
                )

                // Inaktivera HTTP Basic Auth för H2-konsollen
                .httpBasic(httpBasic -> httpBasic.disable())

                // Inaktivera form login för H2-konsollen
                .formLogin(formLogin -> formLogin.disable())

                .build();
    }
}