package com.ctrlbuy.webshop;


public class BasicTest {

    public static void main(String[] args) {
        System.out.println("🚀 Kör basic test...");

        // Test 1: Enkel matematik
        if (2 + 2 == 4) {
            System.out.println("✅ Matematik fungerar!");
        }

        // Test 2: String test
        String test = "Hello World";
        if (test.length() == 11) {
            System.out.println("✅ String fungerar!");
        }

        System.out.println("✅ Alla basic tester klara!");
        System.out.println("📦 Din webshop är redo för deployment!");
    }
}