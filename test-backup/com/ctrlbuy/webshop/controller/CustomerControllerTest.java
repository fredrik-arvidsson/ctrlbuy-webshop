package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.exception.CustomerNotFoundException;
import com.ctrlbuy.webshop.model.CustomerEntity;
import com.ctrlbuy.webshop.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Customer Controller Unit Tests")
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new CustomerEntity();
        testCustomer.setUsername("testuser");
        testCustomer.setPassword("password123");
    }

    @Test
    @DisplayName("Get customer - Existing customer")
    void testGetCustomerExisting() {
        // Arrange
        when(customerService.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        ResponseEntity<CustomerEntity> response = customerController.getCustomer(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        verify(customerService).findById(1L);
        
        System.out.println("✅ Get customer existing - PASS");
    }

    @Test
    @DisplayName("Get customer - Non-existing customer")
    void testGetCustomerNonExisting() {
        // Arrange
        when(customerService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(
            CustomerNotFoundException.class,
            () -> customerController.getCustomer(999L)
        );
        
        assertEquals("Kunden med ID 999 hittades inte", exception.getMessage());
        verify(customerService).findById(999L);
        
        System.out.println("✅ Get customer non-existing - PASS");
    }
}
