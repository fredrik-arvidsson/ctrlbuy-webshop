package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.security.remember-me.token-validity-seconds:2592000}")
    private int rememberMeTokenValiditySeconds;

    @Value("${app.security.remember-me.key:uniqueAndSecretKey2025CtrlBuy}")
    private String rememberMeKey;

    @Value("${app.security.remember-me.parameter:remember-me}")
    private String rememberMeParameter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 🔥 H2-KONSOLL - MÅSTE VARA FÖRST!
                        .requestMatchers("/h2-console/**").permitAll()

                        // Statiska resurser
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Publika sidor
                        .requestMatchers("/", "/home", "/home/**", "/about", "/om-oss", "/produkter", "/produkter/**", "/kontakt", "/support", "/debug-products").permitAll()

                        // Produktsidor
                        .requestMatchers("/products", "/products/**").permitAll()

                        // Autentisering och registrering
                        .requestMatchers("/login", "/login/**", "/register", "/register/**").permitAll()

                        // E-postverifiering och relaterade endpoints
                        .requestMatchers("/verify-email", "/verify-email/**").permitAll()
                        .requestMatchers("/resend-verification", "/resend-verification/**").permitAll()

                        // Lösenordsåterställning
                        .requestMatchers("/forgot-password", "/forgot-password/**").permitAll()
                        .requestMatchers("/reset-password", "/reset-password/**").permitAll()

                        // Test endpoints
                        .requestMatchers("/test-email", "/test-email/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // 🔥 FIXAT: Bara admins får tillgång

                        // Cart endpoints
                        .requestMatchers("/cart/**", "/varukorg/**").permitAll()

                        // Coming Soon sidor (publika) - 🆕 UPPDATERAD med alla URLs
                        .requestMatchers("/returer", "/spara-bestallning", "/garantivillkor", "/coming-soon").permitAll()

                        // Profil-sidor kräver inloggning
                        .requestMatchers("/min-profil", "/min-profil/**", "/profile/**").authenticated()

                        // Resten är publikt
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)  // 🔥 FIXAT: Alltid till hemsidan efter login
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(rememberMeTokenValiditySeconds)
                        .userDetailsService(customUserDetailsService)
                        .rememberMeParameter(rememberMeParameter)
                        .rememberMeCookieName("ctrlbuy-remember-me")
                        .alwaysRemember(false)  // 🔥 NYTT: Bara kom ihåg om användaren kryssar i
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "ctrlbuy-remember-me")
                        .clearAuthentication(true)  // 🔥 NYTT: Rensa autentisering
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        // 🔥 VIKTIGT: Lägg till H2-konsoll här också!
                        .ignoringRequestMatchers("/h2-console/**", "/test-email/**", "/api/test/**", "/admin/**", "/cart/**", "/varukorg/**")
                )
                // 🔥 NYTT: Headers för H2-konsoll
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}