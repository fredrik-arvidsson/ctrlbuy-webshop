package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Review;
import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.ReviewRepository;
import com.ctrlbuy.webshop.security.entity.User;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Unit Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private Product testProduct;
    private Review testReview;

    @BeforeEach
    void setUp() {
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
        testReview.setTitle("Excellent product!");
        testReview.setReviewText("This is a great product. Highly recommended!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setIsVerifiedPurchase(true);
        testReview.setHelpfulCount(0);
    }

    @Test
    @DisplayName("Should get paginated reviews for product successfully")
    void shouldGetReviewsByProductWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);

        when(reviewRepository.findByProductOrderByCreatedAtDesc(testProduct, pageable))
                .thenReturn(reviewPage);

        // When
        Page<Review> result = reviewService.getReviewsByProduct(testProduct, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testReview);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(reviewRepository).findByProductOrderByCreatedAtDesc(testProduct, pageable);
    }

    @Test
    @DisplayName("Should handle repository exception when getting reviews by product")
    void shouldHandleExceptionWhenGettingReviewsByProduct() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByProductOrderByCreatedAtDesc(testProduct, pageable))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> reviewService.getReviewsByProduct(testProduct, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kunde inte ladda recensioner för produkten");
    }

    @Test
    @DisplayName("Should get all reviews for product successfully")
    void shouldGetAllReviewsByProduct() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 1000), 1);

        when(reviewRepository.findByProductOrderByCreatedAtDesc(eq(testProduct), any(Pageable.class)))
                .thenReturn(reviewPage);

        // When
        List<Review> result = reviewService.getAllReviewsByProduct(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testReview);
    }

    @Test
    @DisplayName("Should return empty list when exception occurs getting all reviews")
    void shouldReturnEmptyListWhenExceptionOccurs() {
        // Given
        when(reviewRepository.findByProductOrderByCreatedAtDesc(eq(testProduct), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        List<Review> result = reviewService.getAllReviewsByProduct(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get reviews by user successfully")
    void shouldGetReviewsByUser() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByUser(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testReview);

        verify(reviewRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should return empty list when user has no reviews")
    void shouldReturnEmptyListWhenUserHasNoReviews() {
        // Given
        when(reviewRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(Collections.emptyList());

        // When
        List<Review> result = reviewService.getReviewsByUser(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return true when user has reviewed product")
    void shouldReturnTrueWhenUserHasReviewedProduct() {
        // Given
        when(reviewRepository.findByUserAndProduct(testUser, testProduct))
                .thenReturn(Optional.of(testReview));

        // When
        boolean result = reviewService.hasUserReviewedProduct(testUser, testProduct);

        // Then
        assertThat(result).isTrue();
        verify(reviewRepository).findByUserAndProduct(testUser, testProduct);
    }

    @Test
    @DisplayName("Should return false when user has not reviewed product")
    void shouldReturnFalseWhenUserHasNotReviewedProduct() {
        // Given
        when(reviewRepository.findByUserAndProduct(testUser, testProduct))
                .thenReturn(Optional.empty());

        // When
        boolean result = reviewService.hasUserReviewedProduct(testUser, testProduct);

        // Then
        assertThat(result).isFalse();
        verify(reviewRepository).findByUserAndProduct(testUser, testProduct);
    }

    @Test
    @DisplayName("Should return false when exception occurs checking user review")
    void shouldReturnFalseWhenExceptionOccursCheckingUserReview() {
        // Given
        when(reviewRepository.findByUserAndProduct(testUser, testProduct))
                .thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = reviewService.hasUserReviewedProduct(testUser, testProduct);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should get review statistics successfully")
    void shouldGetReviewStatistics() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenReturn(5L);
        when(reviewRepository.findAverageRatingByProduct(testProduct)).thenReturn(4.2);

        // When
        Map<String, Object> stats = reviewService.getReviewStatistics(testProduct);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.get("reviewCount")).isEqualTo(5L);
        assertThat(stats.get("hasReviews")).isEqualTo(true);
        assertThat(stats.get("averageRating")).isEqualTo(4.2);
    }

    @Test
    @DisplayName("Should return default statistics when no reviews exist")
    void shouldReturnDefaultStatisticsWhenNoReviewsExist() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenReturn(0L);
        when(reviewRepository.findAverageRatingByProduct(testProduct)).thenReturn(null);

        // When
        Map<String, Object> stats = reviewService.getReviewStatistics(testProduct);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.get("reviewCount")).isEqualTo(0L);
        assertThat(stats.get("hasReviews")).isEqualTo(false);
        assertThat(stats.get("averageRating")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should handle exception when getting statistics")
    void shouldHandleExceptionWhenGettingStatistics() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenThrow(new RuntimeException("Database error"));

        // When
        Map<String, Object> stats = reviewService.getReviewStatistics(testProduct);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.get("reviewCount")).isEqualTo(0L);
        assertThat(stats.get("hasReviews")).isEqualTo(false);
        assertThat(stats.get("averageRating")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should get review count by product")
    void shouldGetReviewCountByProduct() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenReturn(3L);

        // When
        long count = reviewService.getReviewCountByProduct(testProduct);

        // Then
        assertThat(count).isEqualTo(3L);
        verify(reviewRepository).countByProduct(testProduct);
    }

    @Test
    @DisplayName("Should return 0 when exception occurs getting review count")
    void shouldReturn0WhenExceptionOccursGettingReviewCount() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenThrow(new RuntimeException("Database error"));

        // When
        long count = reviewService.getReviewCountByProduct(testProduct);

        // Then
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should get average rating by product")
    void shouldGetAverageRatingByProduct() {
        // Given
        when(reviewRepository.findAverageRatingByProduct(testProduct)).thenReturn(4.5);

        // When
        double rating = reviewService.getAverageRatingByProduct(testProduct);

        // Then
        assertThat(rating).isEqualTo(4.5);
        verify(reviewRepository).findAverageRatingByProduct(testProduct);
    }

    @Test
    @DisplayName("Should return 0.0 when no average rating exists")
    void shouldReturn0WhenNoAverageRatingExists() {
        // Given
        when(reviewRepository.findAverageRatingByProduct(testProduct)).thenReturn(null);

        // When
        double rating = reviewService.getAverageRatingByProduct(testProduct);

        // Then
        assertThat(rating).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should get latest reviews")
    void shouldGetLatestReviews() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findTop6ByOrderByCreatedAtDesc()).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getLatestReviews();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testReview);
    }

    @Test
    @DisplayName("Should save review successfully")
    void shouldSaveReviewSuccessfully() {
        // Given
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        // When
        Review result = reviewService.saveReview(testReview);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testReview);
        verify(reviewRepository).save(testReview);
    }

    @Test
    @DisplayName("Should throw exception when save fails")
    void shouldThrowExceptionWhenSaveFails() {
        // Given
        when(reviewRepository.save(testReview)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> reviewService.saveReview(testReview))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kunde inte spara recensionen");
    }

    @Test
    @DisplayName("Should find review by ID")
    void shouldFindReviewById() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        Optional<Review> result = reviewService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testReview);
    }

    @Test
    @DisplayName("Should return empty when review not found")
    void shouldReturnEmptyWhenReviewNotFound() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Review> result = reviewService.findById(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete review successfully")
    void shouldDeleteReviewSuccessfully() {
        // Given
        doNothing().when(reviewRepository).deleteById(1L);

        // When
        reviewService.deleteReview(1L);

        // Then
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when delete fails")
    void shouldThrowExceptionWhenDeleteFails() {
        // Given
        doThrow(new RuntimeException("Database error")).when(reviewRepository).deleteById(1L);

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kunde inte ta bort recensionen");
    }

    @Test
    @DisplayName("Should get rating distribution by product")
    void shouldGetRatingDistributionByProduct() {
        // Given
        List<Object[]> distribution = Arrays.asList(
                new Object[]{5, 3L},
                new Object[]{4, 2L},
                new Object[]{3, 1L}
        );
        when(reviewRepository.countReviewsByRatingForProduct(testProduct)).thenReturn(distribution);

        // When
        List<Object[]> result = reviewService.getRatingDistributionByProduct(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0)[0]).isEqualTo(5);
        assertThat(result.get(0)[1]).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should return empty list when exception occurs getting rating distribution")
    void shouldReturnEmptyListWhenExceptionOccursGettingRatingDistribution() {
        // Given
        when(reviewRepository.countReviewsByRatingForProduct(testProduct))
                .thenThrow(new RuntimeException("Database error"));

        // When
        List<Object[]> result = reviewService.getRatingDistributionByProduct(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should round average rating to one decimal place")
    void shouldRoundAverageRatingToOneDecimalPlace() {
        // Given
        when(reviewRepository.countByProduct(testProduct)).thenReturn(3L);
        when(reviewRepository.findAverageRatingByProduct(testProduct)).thenReturn(4.267);

        // When
        Map<String, Object> stats = reviewService.getReviewStatistics(testProduct);

        // Then
        assertThat(stats.get("averageRating")).isEqualTo(4.3);
    }
}