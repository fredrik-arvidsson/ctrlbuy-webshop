package com.ctrlbuy.webshop.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT-konfiguration specifik för testmiljön
 */
@Configuration
@Profile("test")
public class JwtTestConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtTestConfig.class);

    @Value("${jwt.secret:TEST_SECRET_KEY_FOR_TESTING_PURPOSES_ONLY}")
    private String jwtSecret;

    // Lägg till defaultvärde som fallback ifall propertyn inte kan läsas korrekt
    @Value("${jwt.expirationMs:86400000}")
    private int jwtExpirationMs;

    // Definiera konstanta värden för de egenskaper som inte kan hittas
    private static final String JWT_HEADER = "Authorization";
    private static final String JWT_PREFIX = "Bearer ";

    public JwtTestConfig() {
        log.info("Initierar JwtTestConfig för testmiljön");
    }

    @Bean
    public String jwtHeader() {
        log.debug("Tillhandahåller jwtHeader bean: {}", JWT_HEADER);
        return JWT_HEADER;
    }

    @Bean
    public String jwtPrefix() {
        log.debug("Tillhandahåller jwtPrefix bean: {}", JWT_PREFIX);
        return JWT_PREFIX;
    }

    // Getter-metoder för att komma åt konfigurationsparametrar
    @Bean
    public String jwtSecret() {
        log.debug("Tillhandahåller jwtSecret bean: [HIDDEN]");
        return jwtSecret;
    }

    @Bean
    public int jwtExpirationMs() {
        log.debug("Tillhandahåller jwtExpirationMs bean: {}", jwtExpirationMs);
        return jwtExpirationMs;
    }

    // Getter-metoder utan Bean-annotation
    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}