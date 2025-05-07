package com.ctrlbuy.webshop.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET_KEY = "your_secret_key";  // Använd en stark hemlig nyckel
    private final long EXPIRATION_TIME = 86400000L;  // 24 timmar i millisekunder

    // Generera JWT-token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // Extrahera användarnamn från JWT-token
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Validera JWT-token
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);  // Försök att parsa och validera token
            return true;
        } catch (Exception e) {
            return false;  // Om något går fel är token ogiltig
        }
    }
}
