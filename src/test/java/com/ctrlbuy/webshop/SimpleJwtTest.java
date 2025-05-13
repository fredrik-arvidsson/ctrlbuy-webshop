package com.ctrlbuy.webshop;

import com.ctrlbuy.webshop.config.TestConfig;
import com.ctrlbuy.webshop.security.config.JwtTestConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Enkelt test för att verifiera att JWT-properties laddas korrekt.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import({TestConfig.class, JwtTestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class SimpleJwtTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleJwtTest.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private String jwtExpirationMs;

    @Autowired
    private JwtTestConfig jwtTestConfig;

    @Test
    public void testJwtPropertiesAreLoaded() {
        log.info("Kör test av JWT-properties");

        // Kontrollera att properties är inlästa
        assertNotNull(jwtSecret);
        assertEquals("5EF560E876ACBBF7A2CE0EF8B9175448B33BAFA53309C583D09F933C4BEC3CB7", jwtSecret);

        // Verifiera att jwtTestConfig också är korrekt inläst
        assertNotNull(jwtTestConfig);
        assertEquals(jwtSecret, jwtTestConfig.getJwtSecret());

        log.info("JWT-properties test slutfört framgångsrikt");
    }
}