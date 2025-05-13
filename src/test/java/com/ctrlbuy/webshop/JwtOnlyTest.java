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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({TestConfig.class, JwtTestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtOnlyTest {

    private static final Logger log = LoggerFactory.getLogger(JwtOnlyTest.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private JwtTestConfig jwtTestConfig;

    @Test
    public void testJwtPropertiesLoaded() {
        log.info("Kör test av JWT-properties");

        // Kontrollera att properties laddats korrekt
        assertNotNull(jwtSecret);

        // Kontrollera att värdet matchar det förväntade
        // (notera att detta kan behöva ändras om du använder ett annat värde i application-test.properties)
        assertEquals("5EF560E876ACBBF7A2CE0EF8B9175448B33BAFA53309C583D09F933C4BEC3CB7", jwtSecret);

        // Verifiera att jwtTestConfig också är korrekt inläst
        assertNotNull(jwtTestConfig);
        assertEquals(jwtSecret, jwtTestConfig.getJwtSecret());

        log.info("JWT-properties test slutfört framgångsrikt");
    }
}