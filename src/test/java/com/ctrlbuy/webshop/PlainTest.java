package com.ctrlbuy.webshop;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Extremt enkelt test utan Spring-kontext.
 * Detta test körs betydligt snabbare än tester som behöver ladda Spring-kontexten.
 */
@ActiveProfiles("test")
public class PlainTest {

    private static final Logger log = LoggerFactory.getLogger(PlainTest.class);

    @Test
    public void simpleTest() {
        log.info("Kör extremt enkelt test");
        assertTrue(true, "This test should always pass");
        log.info("Enkelt test slutfört framgångsrikt");
    }
}