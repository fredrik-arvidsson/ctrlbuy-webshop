package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.config.TestConfig;
import com.ctrlbuy.webshop.config.SecurityTestConfig;
import com.ctrlbuy.webshop.dto.LoginRequest;
import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.security.JwtService;
import com.ctrlbuy.webshop.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tester för AuthController som hanterar inloggning och registrering.
 * Dessa tester fokuserar på API-interaktionen och validering av autentisering.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestConfig.class, SecurityTestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    public void testContextLoads() {
        // Verifierar att testkontexten laddar korrekt
        log.info("AuthController testkontexten laddades framgångsrikt");
    }

    // De övriga testerna är tillfälligt inaktiverade tills vi har rätt teststruktur

    /*
    @Test
    public void testLoginUserSuccessfully() throws Exception {
        // Kommer att implementeras senare
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // Kommer att implementeras senare
    }

    @Test
    public void testRegisterUserSuccessfully() throws Exception {
        // Kommer att implementeras senare
    }

    @Test
    public void testRegisterUserWithExistingUsername() throws Exception {
        // Kommer att implementeras senare
    }

    @Test
    public void testUnauthorizedAccessToProtectedEndpoint() throws Exception {
        // Kommer att implementeras senare
    }
    */
}