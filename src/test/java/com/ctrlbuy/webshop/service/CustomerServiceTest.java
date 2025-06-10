package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.CustomerEntity;
import com.ctrlbuy.webshop.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity testCustomer;
    private CustomerEntity savedCustomer;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new CustomerEntity();
        testCustomer.setUsername("testuser");
        testCustomer.setPassword("plainpassword");

        // Setup saved customer (with encoded password)
        savedCustomer = new CustomerEntity();
        savedCustomer.setUsername("testuser");
        savedCustomer.setPassword("encodedpassword123");
    }

    // ===== FIND ALL TESTS =====

    @Test
    void findAll_ReturnsAllCustomers() {
        // Given
        CustomerEntity customer1 = new CustomerEntity();
        customer1.setUsername("user1");

        CustomerEntity customer2 = new CustomerEntity();
        customer2.setUsername("user2");

        List<CustomerEntity> expectedCustomers = Arrays.asList(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        // When
        List<CustomerEntity> result = customerService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findAll_ReturnsEmptyList_WhenNoCustomers() {
        // Given
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CustomerEntity> result = customerService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findAll_HandlesRepositoryException() {
        // Given
        when(customerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.findAll());
        verify(customerRepository, times(1)).findAll();
    }

    // ===== FIND BY ID TESTS =====

    @Test
    void findById_ReturnsCustomer_WhenCustomerExists() {
        // Given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // When
        Optional<CustomerEntity> result = customerService.findById(customerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("plainpassword", result.get().getPassword());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void findById_ReturnsEmpty_WhenCustomerNotFound() {
        // Given
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<CustomerEntity> result = customerService.findById(customerId);

        // Then
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void findById_WithNullId() {
        // Given
        when(customerRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<CustomerEntity> result = customerService.findById(null);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(null);
    }

    @Test
    void findById_HandlesRepositoryException() {
        // Given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.findById(customerId));
        verify(customerRepository, times(1)).findById(customerId);
    }

    // ===== SAVE TESTS =====

    @Test
    void save_EncodesPassword_WhenPasswordIsProvided() {
        // Given
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword123");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(savedCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedpassword123", result.getPassword());

        // Verify password was encoded
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, times(1)).save(testCustomer);

        // Verify the customer's password was updated before saving
        assertEquals("encodedpassword123", testCustomer.getPassword());
    }

    @Test
    void save_DoesNotEncodePassword_WhenPasswordIsNull() {
        // Given
        testCustomer.setPassword(null);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword());

        // Verify password encoder was not called
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_DoesNotEncodePassword_WhenPasswordIsEmpty() {
        // Given
        testCustomer.setPassword("");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("", result.getPassword());

        // Verify password encoder was not called
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_EncodesPassword_WhenPasswordIsBlank() {
        // Given - CustomerService actually DOES encode blank passwords (whitespace only)
        testCustomer.setPassword("   ");
        when(passwordEncoder.encode("   ")).thenReturn("encodedblankpassword");

        CustomerEntity expectedCustomer = new CustomerEntity();
        expectedCustomer.setUsername("testuser");
        expectedCustomer.setPassword("encodedblankpassword");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(expectedCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedblankpassword", result.getPassword());

        // Verify password encoder WAS called (because isEmpty() is false for "   ")
        verify(passwordEncoder, times(1)).encode("   ");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_WithNullCustomer() {
        // When & Then
        assertThrows(NullPointerException.class, () -> customerService.save(null));

        // Verify no interactions with dependencies
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void save_WithEmptyUsername() {
        // Given
        testCustomer.setUsername("");
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword123");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("", result.getUsername());
        assertEquals("encodedpassword123", result.getPassword());

        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_WithNullUsername() {
        // Given
        testCustomer.setUsername(null);
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword123");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertNull(result.getUsername());
        assertEquals("encodedpassword123", result.getPassword());

        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_HandlePasswordEncoderException() {
        // Given
        when(passwordEncoder.encode("plainpassword")).thenThrow(new RuntimeException("Encoding error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.save(testCustomer));

        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void save_HandleRepositoryException() {
        // Given
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword123");
        when(customerRepository.save(any(CustomerEntity.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.save(testCustomer));

        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_WithAlreadyEncodedPassword() {
        // Given
        testCustomer.setPassword("$2a$10$encodedpasswordhash"); // BCrypt format
        when(passwordEncoder.encode(anyString())).thenReturn("newencodedpassword");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("newencodedpassword", result.getPassword());

        // Password should still be encoded (service doesn't distinguish between plain and encoded)
        verify(passwordEncoder, times(1)).encode("$2a$10$encodedpasswordhash");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_WithSpecialCharactersInPassword() {
        // Given
        String specialPassword = "p@ssw0rd!#$%^&*()";
        testCustomer.setPassword(specialPassword);
        when(passwordEncoder.encode(specialPassword)).thenReturn("encodedspecialpassword");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("encodedspecialpassword", result.getPassword());

        verify(passwordEncoder, times(1)).encode(specialPassword);
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void save_WithLongPassword() {
        // Given
        String longPassword = "a".repeat(1000); // Very long password
        testCustomer.setPassword(longPassword);
        when(passwordEncoder.encode(longPassword)).thenReturn("encodedlongpassword");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomer);

        // When
        CustomerEntity result = customerService.save(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals("encodedlongpassword", result.getPassword());

        verify(passwordEncoder, times(1)).encode(longPassword);
        verify(customerRepository, times(1)).save(testCustomer);
    }

    // ===== DELETE BY ID TESTS =====

    @Test
    void deleteById_CallsRepositoryDelete() {
        // Given
        Long customerId = 1L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When
        customerService.deleteById(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void deleteById_WithNullId() {
        // Given
        doNothing().when(customerRepository).deleteById(null);

        // When
        customerService.deleteById(null);

        // Then
        verify(customerRepository, times(1)).deleteById(null);
    }

    @Test
    void deleteById_HandleRepositoryException() {
        // Given
        Long customerId = 1L;
        doThrow(new RuntimeException("Delete failed")).when(customerRepository).deleteById(customerId);

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.deleteById(customerId));
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void deleteById_WithNonExistentId() {
        // Given
        Long nonExistentId = 999L;
        doThrow(new RuntimeException("Customer not found")).when(customerRepository).deleteById(nonExistentId);

        // When & Then
        assertThrows(RuntimeException.class, () -> customerService.deleteById(nonExistentId));
        verify(customerRepository, times(1)).deleteById(nonExistentId);
    }

    @Test
    void deleteById_WithZeroId() {
        // Given
        Long zeroId = 0L;
        doNothing().when(customerRepository).deleteById(zeroId);

        // When
        customerService.deleteById(zeroId);

        // Then
        verify(customerRepository, times(1)).deleteById(zeroId);
    }

    @Test
    void deleteById_WithNegativeId() {
        // Given
        Long negativeId = -1L;
        doNothing().when(customerRepository).deleteById(negativeId);

        // When
        customerService.deleteById(negativeId);

        // Then
        verify(customerRepository, times(1)).deleteById(negativeId);
    }

    // ===== INTEGRATION TESTS =====

    @Test
    void fullCustomerLifecycle_CreateFindDelete() {
        // Given
        Long customerId = 1L;

        // Setup for save
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword123");
        savedCustomer.setUsername("testuser");
        savedCustomer.setPassword("encodedpassword123");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(savedCustomer);

        // Setup for findById
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(savedCustomer));

        // Setup for delete
        doNothing().when(customerRepository).deleteById(customerId);

        // When - Save
        CustomerEntity saved = customerService.save(testCustomer);

        // Then - Verify save
        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
        assertEquals("encodedpassword123", saved.getPassword());

        // When - Find
        Optional<CustomerEntity> found = customerService.findById(customerId);

        // Then - Verify find
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());

        // When - Delete
        customerService.deleteById(customerId);

        // Then - Verify all interactions
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(customerRepository, times(1)).save(testCustomer);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void multipleCustomers_SaveAndRetrieve() {
        // Given
        CustomerEntity customer1 = new CustomerEntity();
        customer1.setUsername("user1");
        customer1.setPassword("pass1");

        CustomerEntity customer2 = new CustomerEntity();
        customer2.setUsername("user2");
        customer2.setPassword("pass2");

        CustomerEntity savedCustomer1 = new CustomerEntity();
        savedCustomer1.setUsername("user1");
        savedCustomer1.setPassword("encodedpass1");

        CustomerEntity savedCustomer2 = new CustomerEntity();
        savedCustomer2.setUsername("user2");
        savedCustomer2.setPassword("encodedpass2");

        when(passwordEncoder.encode("pass1")).thenReturn("encodedpass1");
        when(passwordEncoder.encode("pass2")).thenReturn("encodedpass2");
        when(customerRepository.save(customer1)).thenReturn(savedCustomer1);
        when(customerRepository.save(customer2)).thenReturn(savedCustomer2);
        when(customerRepository.findAll()).thenReturn(Arrays.asList(savedCustomer1, savedCustomer2));

        // When
        CustomerEntity result1 = customerService.save(customer1);
        CustomerEntity result2 = customerService.save(customer2);
        List<CustomerEntity> allCustomers = customerService.findAll();

        // Then
        assertEquals("user1", result1.getUsername());
        assertEquals("encodedpass1", result1.getPassword());
        assertEquals("user2", result2.getUsername());
        assertEquals("encodedpass2", result2.getPassword());

        assertEquals(2, allCustomers.size());
        assertEquals("user1", allCustomers.get(0).getUsername());
        assertEquals("user2", allCustomers.get(1).getUsername());

        verify(passwordEncoder, times(1)).encode("pass1");
        verify(passwordEncoder, times(1)).encode("pass2");
        verify(customerRepository, times(1)).save(customer1);
        verify(customerRepository, times(1)).save(customer2);
        verify(customerRepository, times(1)).findAll();
    }
}