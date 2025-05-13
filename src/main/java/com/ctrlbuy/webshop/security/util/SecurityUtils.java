package com.ctrlbuy.webshop.security.util;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static final String SECRET = "your-super-secure-secret-key"; // Lägg detta i en konfiguration!

    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
