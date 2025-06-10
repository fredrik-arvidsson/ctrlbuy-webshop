package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Review;
import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import com.ctrlbuy.webshop.service.ProductService;
import com.ctrlbuy.webshop.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController Unit Tests")
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private User testUser;
    private Product testProduct;
    private Review testReview;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);

        // Setup test review
        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(5);
        testReview.setTitle("Great product!");
        testReview.setReviewText("This is an excellent product.");
        testReview.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should show product reviews successfully")
    void shouldShowProductReviewsSuccessfully() {
        // Given
        Long productId = 1L;
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);
        Map<String, Object> stats = Map.of("reviewCount", 1L, "averageRating", 5.0, "hasReviews", true);

        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(reviewService.getReviewsByProduct(eq(testProduct), any())).thenReturn(reviewPage);
        when(reviewService.getReviewStatistics(testProduct)).thenReturn(stats);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.showProductReviews(productId, 0, model, authentication);

        // Then
        assertThat(result).isEqualTo("reviews/product-reviews");
        verify(model).addAttribute("product", testProduct);
        verify(model).addAttribute("reviews", reviewPage);
        verify(model).addAttribute("stats", stats);
        verify(model).addAttribute("hasUserReviewed", false);
        verify(model).addAttribute("currentPage", 0);
    }

    @Test
    @DisplayName("Should redirect when product not found")
    void shouldRedirectWhenProductNotFound() {
        // Given
        Long productId = 999L;
        when(productService.findById(productId)).thenReturn(Optional.empty());

        // When
        String result = reviewController.showProductReviews(productId, 0, model, authentication);

        // Then
        assertThat(result).isEqualTo("redirect:/products");
        verify(productService).findById(productId);
        verifyNoInteractions(reviewService);
    }

    @Test
    @DisplayName("Should handle anonymous user when showing product reviews")
    void shouldHandleAnonymousUserWhenShowingProductReviews() {
        // Given
        Long productId = 1L;
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);
        Map<String, Object> stats = Map.of("reviewCount", 1L, "averageRating", 5.0, "hasReviews", true);

        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(reviewService.getReviewsByProduct(eq(testProduct), any())).thenReturn(reviewPage);
        when(reviewService.getReviewStatistics(testProduct)).thenReturn(stats);
        when(authentication.getName()).thenReturn("anonymousUser");

        // When
        String result = reviewController.showProductReviews(productId, 0, model, authentication);

        // Then
        assertThat(result).isEqualTo("reviews/product-reviews");
        verify(model).addAttribute("hasUserReviewed", false);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should show write review form for authenticated user")
    void shouldShowWriteReviewFormForAuthenticatedUser() {
        // Given
        Long productId = 1L;
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.showWriteReviewForm(productId, model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("reviews/write-review");
        verify(model).addAttribute("product", testProduct);
    }

    @Test
    @DisplayName("Should redirect to login for unauthenticated user")
    void shouldRedirectToLoginForUnauthenticatedUser() {
        // Given
        Long productId = 1L;
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String result = reviewController.showWriteReviewForm(productId, model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/login");
        verify(redirectAttributes).addAttribute("returnUrl", "/reviews/write/" + productId);
    }

    @Test
    @DisplayName("Should redirect when user already reviewed product")
    void shouldRedirectWhenUserAlreadyReviewedProduct() {
        // Given
        Long productId = 1L;
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(true);

        // When
        String result = reviewController.showWriteReviewForm(productId, model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/product/" + productId + "?error=already-reviewed");
    }

    @Test
    @DisplayName("Should submit review successfully")
    void shouldSubmitReviewSuccessfully() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = "Great product!";
        String reviewText = "This is an excellent product.";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);
        when(reviewService.saveReview(any(Review.class))).thenReturn(testReview);

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/product/" + productId);
        verify(reviewService).saveReview(any(Review.class));
        verify(redirectAttributes).addFlashAttribute("success", "Tack för din recension! Den har publicerats.");
    }

    @Test
    @DisplayName("Should reject invalid rating")
    void shouldRejectInvalidRating() {
        // Given
        Long productId = 1L;
        Integer rating = 6; // Invalid rating
        String title = "Great product!";
        String reviewText = "This is an excellent product.";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/write/" + productId);
        verify(redirectAttributes).addFlashAttribute("error", "Ogiltigt betyg. Välj mellan 1-5 stjärnor.");
        verifyNoInteractions(reviewService);
    }

    @Test
    @DisplayName("Should reject empty title")
    void shouldRejectEmptyTitle() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = ""; // Empty title
        String reviewText = "This is an excellent product.";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/write/" + productId);
        verify(redirectAttributes).addFlashAttribute("error", "Titel är obligatorisk och får vara högst 100 tecken.");
    }

    @Test
    @DisplayName("Should reject too long title")
    void shouldRejectTooLongTitle() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = "A".repeat(101); // Too long title
        String reviewText = "This is an excellent product.";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/write/" + productId);
        verify(redirectAttributes).addFlashAttribute("error", "Titel är obligatorisk och får vara högst 100 tecken.");
    }

    @Test
    @DisplayName("Should reject empty review text")
    void shouldRejectEmptyReviewText() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = "Great product!";
        String reviewText = ""; // Empty review text

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/write/" + productId);
        verify(redirectAttributes).addFlashAttribute("error", "Recensionstext är obligatorisk och får vara högst 1000 tecken.");
    }

    @Test
    @DisplayName("Should show user's reviews")
    void shouldShowUsersReviews() {
        // Given
        List<Review> userReviews = Arrays.asList(testReview);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.getReviewsByUser(testUser)).thenReturn(userReviews);

        // When
        String result = reviewController.showMyReviews(model, authentication);

        // Then
        assertThat(result).isEqualTo("reviews/my-reviews");
        verify(model).addAttribute("reviews", userReviews);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should redirect to login when viewing my reviews unauthenticated")
    void shouldRedirectToLoginWhenViewingMyReviewsUnauthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String result = reviewController.showMyReviews(model, authentication);

        // Then
        assertThat(result).isEqualTo("redirect:/login");
        verifyNoInteractions(userRepository, reviewService);
    }

    @Test
    @DisplayName("Should delete review successfully")
    void shouldDeleteReviewSuccessfully() {
        // Given
        Long reviewId = 1L;
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(reviewService.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        String result = reviewController.deleteReview(reviewId, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/product/" + testProduct.getId());
        verify(reviewService).deleteReview(reviewId);
        verify(redirectAttributes).addFlashAttribute("success", "Recensionen har tagits bort.");
    }

    @Test
    @DisplayName("Should not delete review when user is not owner")
    void shouldNotDeleteReviewWhenUserIsNotOwner() {
        // Given
        Long reviewId = 1L;
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("other@example.com");
        when(reviewService.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        // When
        String result = reviewController.deleteReview(reviewId, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/my-reviews");
        verify(redirectAttributes).addFlashAttribute("error", "Du kan bara ta bort dina egna recensioner.");
        verify(reviewService, never()).deleteReview(reviewId);
    }

    @Test
    @DisplayName("Should handle review not found when deleting")
    void shouldHandleReviewNotFoundWhenDeleting() {
        // Given
        Long reviewId = 999L;
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(reviewService.findById(reviewId)).thenReturn(Optional.empty());

        // When
        String result = reviewController.deleteReview(reviewId, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/my-reviews");
        verify(redirectAttributes).addFlashAttribute("error", "Recensionen hittades inte.");
        verify(reviewService, never()).deleteReview(reviewId);
    }

    @Test
    @DisplayName("Should handle save review exception")
    void shouldHandleSaveReviewException() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = "Great product!";
        String reviewText = "This is an excellent product.";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);
        when(reviewService.saveReview(any(Review.class))).thenThrow(new RuntimeException("Database error"));

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/write/" + productId);
        verify(redirectAttributes).addFlashAttribute("error", "Ett fel uppstod när recensionen skulle sparas. Försök igen.");
    }

    @Test
    @DisplayName("Should find user by username when email not found")
    void shouldFindUserByUsernameWhenEmailNotFound() {
        // Given
        Long productId = 1L;
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);

        // When
        String result = reviewController.showWriteReviewForm(productId, model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("reviews/write-review");
        verify(userRepository).findByEmail("testuser");
        verify(userRepository).findByUsername("testuser");
        verify(model).addAttribute("product", testProduct);
    }

    @Test
    @DisplayName("Should handle null authentication gracefully")
    void shouldHandleNullAuthenticationGracefully() {
        // Given
        Long productId = 1L;
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);
        Map<String, Object> stats = Map.of("reviewCount", 1L, "averageRating", 5.0, "hasReviews", true);

        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(reviewService.getReviewsByProduct(eq(testProduct), any())).thenReturn(reviewPage);
        when(reviewService.getReviewStatistics(testProduct)).thenReturn(stats);

        // When
        String result = reviewController.showProductReviews(productId, 0, model, null);

        // Then
        assertThat(result).isEqualTo("reviews/product-reviews");
        verify(model).addAttribute("hasUserReviewed", false);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should trim whitespace from title and review text")
    void shouldTrimWhitespaceFromTitleAndReviewText() {
        // Given
        Long productId = 1L;
        Integer rating = 5;
        String title = "  Great product!  "; // With whitespace
        String reviewText = "  This is an excellent product.  "; // With whitespace

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(productService.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(reviewService.hasUserReviewedProduct(testUser, testProduct)).thenReturn(false);
        when(reviewService.saveReview(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            assertThat(review.getTitle()).isEqualTo("Great product!");
            assertThat(review.getReviewText()).isEqualTo("This is an excellent product.");
            return review;
        });

        // When
        String result = reviewController.submitReview(productId, rating, title, reviewText,
                model, authentication, redirectAttributes);

        // Then
        assertThat(result).isEqualTo("redirect:/reviews/product/" + productId);
        verify(reviewService).saveReview(any(Review.class));
    }
}