package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.security.service.CustomUserDetailsService;
import com.ctrlbuy.webshop.security.handler.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Statiska resurser
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Publika sidor
                        .requestMatchers("/", "/home", "/about", "/produkter", "/kontakt", "/support").permitAll()

                        // Autentisering och registrering
                        .requestMatchers("/login", "/register", "/register/**").permitAll()

                        // E-postverifiering och relaterade endpoints
                        .requestMatchers("/verify-email", "/verify-email/**").permitAll()
                        .requestMatchers("/resend-verification", "/resend-verification/**").permitAll()

                        // TEST ENDPOINTS - VIKTIGT FÖR DEBUGGING
                        .requestMatchers("/test-email", "/test-email/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()

                        // ADMIN ENDPOINTS - TILLÅT ADMIN-PANEL
                        .requestMatchers("/admin/**").permitAll()

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)  // 🔥 Använd vår smarta routing
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        // Inaktivera CSRF för test-endpoints (kan vara behövligt för debugging)
                        .ignoringRequestMatchers("/test-email/**", "/api/test/**", "/admin/**")
                )
                // VIKTIGT: Använd vår CustomUserDetailsService för verifieringskontroll
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}