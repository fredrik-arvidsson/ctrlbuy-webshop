package com.ctrlbuy.webshop;

import com.ctrlbuy.webshop.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Minimalt test för att verifiera grundläggande funktionalitet.
 * Används främst för att säkerställa att testkonfigurationen fungerar korrekt.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MinimalTest {

    private static final Logger log = LoggerFactory.getLogger(MinimalTest.class);

    @Test
    public void simpleTest() {
        log.info("Kör minimalt test");
        assertTrue(true, "This test should always pass");
        log.info("Minimalt test slutfört framgångsrikt");
    }
}