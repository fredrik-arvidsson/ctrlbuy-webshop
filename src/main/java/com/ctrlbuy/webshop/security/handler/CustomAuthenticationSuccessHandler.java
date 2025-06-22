package com.ctrlbuy.webshop.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        log.info("🔑 Användare '{}' loggade in framgångsrikt", username);

        // Kolla om användaren är admin
        boolean isAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        String redirectUrl;

        if (isAdmin) {
            redirectUrl = "/admin/dashboard";
            log.info("🛡️ Admin-användare '{}' omdirigeras till: {}", username, redirectUrl);
        } else {
            redirectUrl = "/produkter";  // Eller "/" för startsidan
            log.info("👤 Vanlig användare '{}' omdirigeras till: {}", username, redirectUrl);
        }

        // Omdirigera till rätt sida
        response.sendRedirect(redirectUrl);
    }
}