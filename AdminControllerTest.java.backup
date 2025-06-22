package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private List<User> mockUsers;
    private User verifiedUser;
    private User unverifiedUser;

    @BeforeEach
    void setUp() {
        verifiedUser = new User();
        verifiedUser.setId(1L);
        verifiedUser.setUsername("verified");
        verifiedUser.setEmail("verified@test.com");
        verifiedUser.setEmailVerified(true);

        unverifiedUser = new User();
        unverifiedUser.setId(2L);
        unverifiedUser.setUsername("unverified");
        unverifiedUser.setEmail("unverified@test.com");
        unverifiedUser.setEmailVerified(false);

        mockUsers = Arrays.asList(verifiedUser, unverifiedUser);

        reset(userService, model);
    }

    @Test
    void dashboard_ShouldCalculateStatisticsAndReturnDashboardView() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act
        String result = adminController.dashboard(model);

        // Assert
        assertEquals("admin/dashboard", result);
        verify(userService).getAllUsers();
        verify(model).addAttribute("users", mockUsers);
        verify(model).addAttribute("totalUsers", 2L);
        verify(model).addAttribute("verifiedUsers", 1L);
        verify(model).addAttribute("pendingUsers", 1L);
    }

    @Test
    void dashboard_WithEmptyUserList_ShouldReturnZeroStatistics() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act
        String result = adminController.dashboard(model);

        // Assert
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("users", Collections.emptyList());
        verify(model).addAttribute("totalUsers", 0L);
        verify(model).addAttribute("verifiedUsers", 0L);
        verify(model).addAttribute("pendingUsers", 0L);
    }

    @Test
    void dashboard_WithAllVerifiedUsers_ShouldShowCorrectStatistics() {
        // Arrange
        User user1 = new User();
        user1.setEmailVerified(true);
        User user2 = new User();
        user2.setEmailVerified(true);
        List<User> allVerifiedUsers = Arrays.asList(user1, user2);
        
        when(userService.getAllUsers()).thenReturn(allVerifiedUsers);

        // Act
        String result = adminController.dashboard(model);

        // Assert
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 2L);
        verify(model).addAttribute("verifiedUsers", 2L);
        verify(model).addAttribute("pendingUsers", 0L);
    }

    @Test
    void dashboard_WithAllUnverifiedUsers_ShouldShowCorrectStatistics() {
        // Arrange
        User user1 = new User();
        user1.setEmailVerified(false);
        User user2 = new User();
        user2.setEmailVerified(false);
        List<User> allUnverifiedUsers = Arrays.asList(user1, user2);
        
        when(userService.getAllUsers()).thenReturn(allUnverifiedUsers);

        // Act
        String result = adminController.dashboard(model);

        // Assert
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 2L);
        verify(model).addAttribute("verifiedUsers", 0L);
        verify(model).addAttribute("pendingUsers", 2L);
    }

    @Test
    void adminHome_ShouldRedirectToDashboard() {
        // Act
        String result = adminController.adminHome();

        // Assert
        assertEquals("redirect:/admin/dashboard", result);
    }

    @Test
    void listUsers_ShouldReturnUsersViewWithUserList() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act
        String result = adminController.listUsers(model);

        // Assert
        assertEquals("admin/users", result);
        verify(userService).getAllUsers();
        verify(model).addAttribute("users", mockUsers);
    }

    @Test
    void listUsers_WithEmptyList_ShouldReturnUsersViewWithEmptyList() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act
        String result = adminController.listUsers(model);

        // Assert
        assertEquals("admin/users", result);
        verify(model).addAttribute("users", Collections.emptyList());
    }

    @Test
    void deleteUser_WhenSuccessful_ShouldSetSuccessMessageAndRedirect() {
        // Arrange
        Long userId = 1L;
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.deleteUser(userId, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUser(userId);
        verify(model).addAttribute("message", "Användare borttagen!");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void deleteUser_WhenExceptionThrown_ShouldSetErrorMessageAndRedirect() {
        // Arrange
        Long userId = 1L;
        RuntimeException exception = new RuntimeException("User not found");
        doThrow(exception).when(userService).deleteUser(userId);

        // Act
        String result = adminController.deleteUser(userId, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUser(userId);
        verify(model).addAttribute("message", "Fel vid borttagning: User not found");
        verify(model).addAttribute("messageType", "error");
    }

    @Test
    void deleteUserByEmail_WhenSuccessful_ShouldSetSuccessMessageAndRedirect() {
        // Arrange
        String email = "test@example.com";
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.deleteUserByEmail(email, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUserByEmail(email);
        verify(model).addAttribute("message", "Användare med e-post " + email + " borttagen!");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void deleteUserByEmail_WhenExceptionThrown_ShouldSetErrorMessageAndRedirect() {
        // Arrange
        String email = "nonexistent@example.com";
        RuntimeException exception = new RuntimeException("Email not found");
        doThrow(exception).when(userService).deleteUserByEmail(email);

        // Act
        String result = adminController.deleteUserByEmail(email, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUserByEmail(email);
        verify(model).addAttribute("message", "Fel vid borttagning: Email not found");
        verify(model).addAttribute("messageType", "error");
    }

    @Test
    void toggleUserActive_WhenSuccessful_ShouldRedirectToUsers() {
        // Arrange
        Long userId = 1L;
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.toggleUserActive(userId);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).toggleUserActive(userId);
    }

    @Test
    void toggleUserActive_WhenExceptionThrown_ShouldStillRedirectToUsers() {
        // Arrange
        Long userId = 1L;
        RuntimeException exception = new RuntimeException("Toggle failed");
        doThrow(exception).when(userService).toggleUserActive(userId);

        // Act
        String result = adminController.toggleUserActive(userId);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).toggleUserActive(userId);
        // Exception is ignored, no error handling
    }

    @Test
    void resetUserVerification_WhenSuccessful_ShouldRedirectToUsers() {
        // Arrange
        Long userId = 1L;
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.resetUserVerification(userId);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).resetUserVerification(userId);
    }

    @Test
    void resetUserVerification_WhenExceptionThrown_ShouldStillRedirectToUsers() {
        // Arrange
        Long userId = 1L;
        RuntimeException exception = new RuntimeException("Reset failed");
        doThrow(exception).when(userService).resetUserVerification(userId);

        // Act
        String result = adminController.resetUserVerification(userId);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).resetUserVerification(userId);
        // Exception is ignored, no error handling
    }

    @Test
    void deleteUser_WithNullId_ShouldHandleGracefully() {
        // Arrange
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.deleteUser(null, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUser(null);
        verify(model).addAttribute("message", "Användare borttagen!");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void deleteUserByEmail_WithNullEmail_ShouldHandleGracefully() {
        // Arrange
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.deleteUserByEmail(null, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUserByEmail(null);
        verify(model).addAttribute("message", "Användare med e-post null borttagen!");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void deleteUserByEmail_WithEmptyEmail_ShouldHandleGracefully() {
        // Arrange
        String emptyEmail = "";
        // No stubbing needed for void methods that don't throw

        // Act
        String result = adminController.deleteUserByEmail(emptyEmail, model);

        // Assert
        assertEquals("redirect:/admin/users", result);
        verify(userService).deleteUserByEmail(emptyEmail);
        verify(model).addAttribute("message", "Användare med e-post  borttagen!");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void dashboard_WithLargeUserList_ShouldCalculateCorrectly() {
        // Arrange
        User[] users = new User[100];
        for (int i = 0; i < 100; i++) {
            users[i] = new User();
            users[i].setEmailVerified(i % 3 == 0); // Every 3rd user is verified
        }
        List<User> largeUserList = Arrays.asList(users);
        when(userService.getAllUsers()).thenReturn(largeUserList);

        // Act
        String result = adminController.dashboard(model);

        // Assert
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 100L);
        verify(model).addAttribute("verifiedUsers", 34L); // 0,3,6,9... = 34 users
        verify(model).addAttribute("pendingUsers", 66L);
    }
}
