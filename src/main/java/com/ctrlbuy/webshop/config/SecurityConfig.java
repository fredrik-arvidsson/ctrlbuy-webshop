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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 🛡️ Aktiverar @PreAuthorize annotationer
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

                        // 📁 Statiska resurser - tillgängliga för alla
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                        // 🏠 Publika sidor - ingen inloggning krävs
                        .requestMatchers("/", "/home", "/home/**", "/about", "/om-oss", "/produkter", "/produkter/**", "/kontakt", "/support", "/debug-products").permitAll()

                        // 🛍️ Produktsidor - publikt tillgängliga
                        .requestMatchers("/products", "/products/**").permitAll()

                        // 🔐 Autentisering och registrering - publikt tillgängliga
                        .requestMatchers("/login", "/login/**", "/register", "/register/**").permitAll()

                        // 📧 E-postverifiering och relaterade endpoints
                        .requestMatchers("/verify-email", "/verify-email/**").permitAll()
                        .requestMatchers("/resend-verification", "/resend-verification/**").permitAll()

                        // 🔑 Lösenordsåterställning - publikt tillgängliga
                        .requestMatchers("/forgot-password", "/forgot-password/**").permitAll()
                        .requestMatchers("/reset-password", "/reset-password/**").permitAll()

                        // 🧪 Test endpoints - utveckling
                        .requestMatchers("/test-email", "/test-email/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()

                        // 🛡️ ADMIN ENDPOINTS - KRÄVER ROLE_ADMIN (dubbel säkerhet)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🛒 Varukorg - tillgänglig för alla (session-baserad)
                        .requestMatchers("/cart/**", "/varukorg/**").permitAll()

                        // 📄 Coming Soon sidor - publikt tillgängliga
                        .requestMatchers("/returer", "/spara-bestallning", "/garantivillkor", "/coming-soon").permitAll()

                        // 👤 Profil-sidor - kräver inloggning (vilken roll som helst)
                        .requestMatchers("/min-profil", "/min-profil/**", "/profile/**").authenticated()

                        // ❌ Error endpoint - publikt tillgängligt
                        .requestMatchers("/error").permitAll()

                        // 🌐 Allt annat är publikt (fallback)
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(rememberMeTokenValiditySeconds)
                        .userDetailsService(customUserDetailsService)
                        .rememberMeParameter(rememberMeParameter)
                        .rememberMeCookieName("ctrlbuy-remember-me")
                        .alwaysRemember(false)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "ctrlbuy-remember-me")
                        .clearAuthentication(true)
                        .permitAll()
                )
                // 🛡️ CSRF-SKYDD MED TOKENS FÖR ADMIN-PANEL
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/test-email/**", "/api/test/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler())
                )
                // 🖼️ Headers för H2-konsoll
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