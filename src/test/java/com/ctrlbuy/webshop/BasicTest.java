package com.ctrlbuy.webshop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicTest {

    @Test
    void testBasicMath() {
        assertEquals(4, 2 + 2);
        System.out.println("✅ Matematik fungerar!");
    }

    @Test
    void testStringLength() {
        String test = "Hello";
        assertEquals(5, test.length());
        System.out.println("✅ String fungerar!");
    }

    @Test
    void testBoolean() {
        assertTrue(true);
        assertFalse(false);
        System.out.println("✅ Boolean fungerar!");
    }
}
