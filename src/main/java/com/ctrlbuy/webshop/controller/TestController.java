package com.ctrlbuy.webshop.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Profile("test")
@Order(1) // Högre prioritet än SecurityConfig
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        // I testmiljö, returnera 404 Not Found för denna endpoint
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }
}