package com.ctrlbuy.webshop;

import com.ctrlbuy.webshop.config.TestConfig;
import com.ctrlbuy.webshop.config.SecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Huvudtestklass för Ctrlbuy Webshop-applikationen.
 *
 * Denna klass använder Spring Boot Test med en separat testprofil för att säkerställa
 * att alla tester körs i en isolerad miljö med egen databas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import({TestConfig.class, SecurityTestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
// @Disabled-annoteringen har tagits bort
class CtrlbuyWebshopApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(CtrlbuyWebshopApplicationTests.class);

    /**
     * Grundläggande test som verifierar att applikationskontexten kan laddas korrekt.
     * Detta test är användbart för att validera att alla beroenden och konfigurationer
     * är korrekt inställda.
     */
    @Test
    void contextLoads() {
        log.info("✅ Testet startade – contextLoads körs!");
    }
}