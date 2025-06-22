package com.ctrlbuy.webshop.service;


import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // Metoder för autentisering (t.ex. login, register)
    public boolean authenticate(String username, String password) {
        // Logik för autentisering, exempelvis jämförelse mot en databas
        return username.equals("admin") && password.equals("admin");
    }
}
