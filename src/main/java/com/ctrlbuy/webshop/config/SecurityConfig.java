package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.service.CustomUserDetailsService;
// import com.ctrlbuy.webshop.security.handler.CustomAuthenticationSuccessHandler;  // ← KOMMENTERAD BORT
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

    // @Autowired
    // private CustomAuthenticationSuccessHandler successHandler;  // ← KOMMENTERAD BORT

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Statiska resurser
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Publika sidor - VIKTIGT: Lägg till /** för att matcha alla home-varianter
                        .requestMatchers("/", "/home", "/home/**", "/about", "/om-oss", "/produkter", "/produkter/**", "/kontakt", "/support", "/debug-products").permitAll()

                        // Produktsidor - LÄGG TILL /products också
                        .requestMatchers("/products", "/products/**").permitAll()

                        // Autentisering och registrering
                        .requestMatchers("/login", "/login/**", "/register", "/register/**").permitAll()

                        // E-postverifiering och relaterade endpoints
                        .requestMatchers("/verify-email", "/verify-email/**").permitAll()
                        .requestMatchers("/resend-verification", "/resend-verification/**").permitAll()

                        // LÖSENORDSÅTERSTÄLLNING - NYA ENDPOINTS
                        .requestMatchers("/forgot-password", "/forgot-password/**").permitAll()
                        .requestMatchers("/reset-password", "/reset-password/**").permitAll()

                        // TEST ENDPOINTS - VIKTIGT FÖR DEBUGGING
                        .requestMatchers("/test-email", "/test-email/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()

                        // ADMIN ENDPOINTS - TILLÅT ADMIN-PANEL
                        .requestMatchers("/admin/**").permitAll()

                        // CART ENDPOINTS - LÄGG TILL CART
                        .requestMatchers("/cart/**", "/varukorg/**").permitAll()

                        // 🔥 ÄNDRING: Inloggade användare behöver tillgång till profil
                        .requestMatchers("/min-profil", "/profile/**").authenticated()

                        // 🔥 ÄNDRING: Andra skyddade sidor kan kräva inloggning - men de flesta sidor ska vara publika
                        .anyRequest().permitAll()  // ÄNDRAT från .authenticated() till .permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)  // 🔥 ÄNDRAT: Använd standard redirect till hemsidan
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")  // 🔥 ÄNDRAT: Gå till hemsidan efter logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        // Inaktivera CSRF för test-endpoints (kan vara behövligt för debugging)
                        .ignoringRequestMatchers("/test-email/**", "/api/test/**", "/admin/**", "/cart/**", "/varukorg/**")
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