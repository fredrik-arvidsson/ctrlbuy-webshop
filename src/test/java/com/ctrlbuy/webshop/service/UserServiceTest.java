package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest testRegisterRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .emailVerified(true)
                .verificationToken(null)
                .verificationTokenExpiry(null)
                .build();

        // Setup test register request
        testRegisterRequest = new RegisterRequest();
        testRegisterRequest.setUsername("newuser");
        testRegisterRequest.setEmail("newuser@example.com");
        testRegisterRequest.setPassword("password123");
        testRegisterRequest.setConfirmPassword("password123");
        testRegisterRequest.setFirstName("New");
        testRegisterRequest.setLastName("User");
    }

    // ========================================
    // AUTHENTICATION TESTS
    // ========================================

    @Test
    void loadUserByUsername_WithActiveUser_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsernameAndActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsernameAndActiveTrue("testuser");
    }

    @Test
    void loadUserByUsername_WithInactiveUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsernameAndActiveTrue("inactiveuser"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("inactiveuser"));
        verify(userRepository).findByUsernameAndActiveTrue("inactiveuser");
    }

    @Test
    void loadUserByUsername_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsernameAndActiveTrue("nonexistent"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));
    }

    // ========================================
    // REGISTRATION TESTS
    // ========================================

    @Test
    void registerNewUserWithToken_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        RegistrationResult result = userService.registerNewUserWithToken(testRegisterRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUser());
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isEmpty());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerNewUserWithToken_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerNewUserWithToken(testRegisterRequest));
        assertEquals("Användarnamnet är redan taget", exception.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerNewUserWithToken_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerNewUserWithToken(testRegisterRequest));
        assertEquals("E-postadressen är redan registrerad", exception.getMessage());
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================================
    // EMAIL VERIFICATION TESTS
    // ========================================

    @Test
    void verifyEmail_WithValidToken_ShouldVerifyUser() {
        // Arrange
        String token = "valid-token";
        User unverifiedUser = User.builder()
                .id(2L)
                .email("test@example.com")
                .emailVerified(false)
                .verificationToken(token)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(1))
                .build();

        when(userRepository.findByVerificationToken(token))
                .thenReturn(Optional.of(unverifiedUser));
        when(userRepository.save(any(User.class))).thenReturn(unverifiedUser);

        // Act
        boolean result = userService.verifyEmail(token);

        // Assert
        assertTrue(result);
        verify(userRepository).findByVerificationToken(token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyEmail_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String token = "invalid-token";
        when(userRepository.findByVerificationToken(token))
                .thenReturn(Optional.empty());

        // Act
        boolean result = userService.verifyEmail(token);

        // Assert
        assertFalse(result);
        verify(userRepository).findByVerificationToken(token);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createNewVerificationToken_WithValidEmail_ShouldCreateToken() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        String result = userService.createNewVerificationToken("test@example.com");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createNewVerificationToken_WithInvalidEmail_ShouldReturnNull() {
        // Arrange
        when(userRepository.findByEmail("invalid@example.com"))
                .thenReturn(Optional.empty());

        // Act
        String result = userService.createNewVerificationToken("invalid@example.com");

        // Assert
        assertNull(result);
        verify(userRepository).findByEmail("invalid@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================================
    // PASSWORD RESET TESTS
    // ========================================

    @Test
    void initiatePasswordReset_WithValidEmail_ShouldCreateResetToken() {
        // Arrange
        when(userRepository.findByEmailAndActiveTrue("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.initiatePasswordReset("test@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).findByEmailAndActiveTrue("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void initiatePasswordReset_WithInvalidEmail_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByEmailAndActiveTrue("invalid@example.com"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = userService.initiatePasswordReset("invalid@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository).findByEmailAndActiveTrue("invalid@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_WithValidToken_ShouldResetPassword() {
        // Arrange
        String resetToken = "valid-reset-token";
        User userWithResetToken = User.builder()
                .id(3L)
                .active(true)
                .resetToken(resetToken)
                .resetTokenExpiry(LocalDateTime.now().plusHours(1))
                .build();

        when(userRepository.findByResetToken(resetToken))
                .thenReturn(Optional.of(userWithResetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(userWithResetToken);

        // Act
        boolean result = userService.resetPassword(resetToken, "newPassword");

        // Assert
        assertTrue(result);
        verify(userRepository).findByResetToken(resetToken);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String resetToken = "invalid-reset-token";
        when(userRepository.findByResetToken(resetToken))
                .thenReturn(Optional.empty());

        // Act
        boolean result = userService.resetPassword(resetToken, "newPassword");

        // Assert
        assertFalse(result);
        verify(userRepository).findByResetToken(resetToken);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================================
    // USER LOOKUP TESTS
    // ========================================

    @Test
    void findByUsername_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByEmail_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void existsByUsername_WithExistingActiveUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByUsernameAndActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository).findByUsernameAndActiveTrue("testuser");
    }

    @Test
    void existsByEmail_WithExistingActiveUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByEmailAndActiveTrue("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).findByEmailAndActiveTrue("test@example.com");
    }

    @Test
    void existsByUsernameIncludingInactive_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsernameIncludingInactive("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByEmailIncludingInactive_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmailIncludingInactive("test@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    // ========================================
    // USER MANAGEMENT TESTS
    // ========================================

    @Test
    void toggleUserActive_WithExistingUser_ShouldToggleStatus() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.toggleUserActive(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserActive_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.toggleUserActive(999L));
        assertTrue(exception.getMessage().contains("finns inte"));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deactivateUser_WithRegularUser_ShouldDeactivate() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.deactivateUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateUser_WithAdminUser_ShouldReturnFalse() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .active(true)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // Act
        boolean result = userService.deactivateUser(2L);

        // Assert
        assertFalse(result);
        verify(userRepository).findById(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================================
    // STATISTICS TESTS
    // ========================================

    @Test
    void getUserStats_ShouldReturnCorrectStats() {
        // Arrange
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByActiveTrue()).thenReturn(85L);
        when(userRepository.countByActiveFalse()).thenReturn(15L);
        when(userRepository.countByEmailVerifiedTrue()).thenReturn(80L);
        when(userRepository.countByEmailVerifiedFalse()).thenReturn(20L);

        // Act
        UserService.UserStats result = userService.getUserStats();

        // Assert
        assertEquals(100L, result.totalUsers());
        assertEquals(85L, result.activeUsers());
        assertEquals(15L, result.inactiveUsers());
        assertEquals(80L, result.verifiedUsers());
        assertEquals(20L, result.unverifiedUsers());
    }

    @Test
    void getActiveUsers_ShouldReturnActiveUsers() {
        // Arrange
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        // Act
        List<User> result = userService.getActiveUsers();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void getFirstNameByEmail_WithExistingUser_ShouldReturnFirstName() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        String result = userService.getFirstNameByEmail("test@example.com");

        // Assert
        assertEquals("Test", result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getFirstNameByEmail_WithNonExistentUser_ShouldReturnDefault() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act
        String result = userService.getFirstNameByEmail("nonexistent@example.com");

        // Assert
        assertEquals("Användare", result);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void isUserAdmin_WithAdminUser_ShouldReturnTrue() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // Act
        boolean result = userService.isUserAdmin(2L);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(2L);
    }

    @Test
    void isUserAdmin_WithRegularUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.isUserAdmin(1L);

        // Assert
        assertFalse(result);
        verify(userRepository).findById(1L);
    }
}