package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.entity.Review;
import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ReviewRepository Tests")
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setEmail("user1@example.com");
        testUser1.setUsername("user1");
        testUser1.setFirstName("User");
        testUser1.setLastName("One");
        testUser1.setPassword("password123");
        testUser1.setEmailVerified(true);
        testUser1.setActive(true);
        testUser1.setCreatedAt(LocalDateTime.now());
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setUsername("user2");
        testUser2.setFirstName("User");
        testUser2.setLastName("Two");
        testUser2.setPassword("password123");
        testUser2.setEmailVerified(true);
        testUser2.setActive(true);
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Create test products
        testProduct1 = new Product();
        testProduct1.setName("Test Product 1");
        testProduct1.setDescription("Description for product 1");
        testProduct1.setPrice(99.99);
        testProduct1.setStock(10);
        testProduct1.setActive(true);
        testProduct1 = entityManager.persistAndFlush(testProduct1);

        testProduct2 = new Product();
        testProduct2.setName("Test Product 2");
        testProduct2.setDescription("Description for product 2");
        testProduct2.setPrice(149.99);
        testProduct2.setStock(5);
        testProduct2.setActive(true);
        testProduct2 = entityManager.persistAndFlush(testProduct2);
    }

    @Test
    @DisplayName("Should find reviews by product ordered by created date desc")
    void shouldFindReviewsByProductOrderByCreatedAtDesc() {
        // Given
        Review review1 = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now().minusDays(2));
        Review review2 = createReview(testUser2, testProduct1, 4, "Good", "Good product", LocalDateTime.now().minusDays(1));
        Review review3 = createReview(testUser1, testProduct1, 3, "OK", "Average product", LocalDateTime.now());

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Review> result = reviewRepository.findByProductOrderByCreatedAtDesc(testProduct1, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0)).isEqualTo(review3); // Most recent first
        assertThat(result.getContent().get(1)).isEqualTo(review2);
        assertThat(result.getContent().get(2)).isEqualTo(review1); // Oldest last
    }

    @Test
    @DisplayName("Should find reviews by user ordered by created date desc")
    void shouldFindReviewsByUserOrderByCreatedAtDesc() {
        // Given
        Review review1 = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now().minusDays(2));
        Review review2 = createReview(testUser1, testProduct2, 4, "Good", "Good product", LocalDateTime.now().minusDays(1));
        Review review3 = createReview(testUser2, testProduct1, 3, "OK", "Average product", LocalDateTime.now());

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        // When
        List<Review> result = reviewRepository.findByUserOrderByCreatedAtDesc(testUser1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(review2); // Most recent first
        assertThat(result.get(1)).isEqualTo(review1); // Older second
    }

    @Test
    @DisplayName("Should find review by user and product")
    void shouldFindReviewByUserAndProduct() {
        // Given
        Review review = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now());
        entityManager.persistAndFlush(review);

        // When
        Optional<Review> result = reviewRepository.findByUserAndProduct(testUser1, testProduct1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(review);
    }

    @Test
    @DisplayName("Should return empty when no review exists for user and product")
    void shouldReturnEmptyWhenNoReviewExists() {
        // When
        Optional<Review> result = reviewRepository.findByUserAndProduct(testUser1, testProduct1);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should count reviews by product")
    void shouldCountReviewsByProduct() {
        // Given
        Review review1 = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now());
        Review review2 = createReview(testUser2, testProduct1, 4, "Good", "Good product", LocalDateTime.now());
        Review review3 = createReview(testUser1, testProduct2, 3, "OK", "Average product", LocalDateTime.now());

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        // When
        long count = reviewRepository.countByProduct(testProduct1);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find average rating by product")
    void shouldFindAverageRatingByProduct() {
        // Given
        Review review1 = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now());
        Review review2 = createReview(testUser2, testProduct1, 3, "OK", "Average product", LocalDateTime.now());

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);

        // When
        Double averageRating = reviewRepository.findAverageRatingByProduct(testProduct1);

        // Then
        assertThat(averageRating).isEqualTo(4.0); // (5 + 3) / 2 = 4.0
    }

    @Test
    @DisplayName("Should return null when no reviews exist for average rating")
    void shouldReturnNullWhenNoReviewsExistForAverageRating() {
        // When
        Double averageRating = reviewRepository.findAverageRatingByProduct(testProduct1);

        // Then
        assertThat(averageRating).isNull();
    }

    @Test
    @DisplayName("Should find top 6 reviews ordered by created date desc")
    void shouldFindTop6ReviewsOrderByCreatedAtDesc() {
        // Given - create 8 reviews
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setUsername("user" + i);
            user.setFirstName("User");
            user.setLastName("" + i);
            user.setPassword("password123");
            user.setEmailVerified(true);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user = entityManager.persistAndFlush(user);

            Review review = createReview(user, testProduct1, 5, "Review " + i, "Content " + i, LocalDateTime.now().minusDays(i));
            entityManager.persistAndFlush(review);
        }

        // When
        List<Review> result = reviewRepository.findTop6ByOrderByCreatedAtDesc();

        // Then
        assertThat(result).hasSize(6); // Only top 6 should be returned
        // Verify they are ordered by created date desc (most recent first)
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getCreatedAt()).isAfterOrEqualTo(result.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("Should count reviews by rating for product")
    void shouldCountReviewsByRatingForProduct() {
        // Given - create reviews with different ratings
        createAndPersistReview(testUser1, testProduct1, 5, "Excellent", "Great");
        createAndPersistReview(testUser2, testProduct1, 5, "Excellent", "Great");
        createAndPersistReview(testUser1, testProduct1, 4, "Good", "Good");
        createAndPersistReview(testUser2, testProduct1, 3, "OK", "Average");

        // When
        List<Object[]> result = reviewRepository.countReviewsByRatingForProduct(testProduct1);

        // Then
        assertThat(result).hasSize(3); // 3 different ratings (5, 4, 3)

        // Should be ordered by rating DESC
        assertThat((Integer) result.get(0)[0]).isEqualTo(5); // Rating 5
        assertThat((Long) result.get(0)[1]).isEqualTo(2L);   // Count 2

        assertThat((Integer) result.get(1)[0]).isEqualTo(4); // Rating 4
        assertThat((Long) result.get(1)[1]).isEqualTo(1L);   // Count 1

        assertThat((Integer) result.get(2)[0]).isEqualTo(3); // Rating 3
        assertThat((Long) result.get(2)[1]).isEqualTo(1L);   // Count 1
    }

    @Test
    @DisplayName("Should return empty list when no reviews exist for rating distribution")
    void shouldReturnEmptyListWhenNoReviewsExistForRatingDistribution() {
        // When
        List<Object[]> result = reviewRepository.countReviewsByRatingForProduct(testProduct1);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
        // Given - create 15 reviews
        for (int i = 0; i < 15; i++) {
            User user = new User();
            user.setEmail("paguser" + i + "@example.com");
            user.setUsername("paguser" + i);
            user.setFirstName("User");
            user.setLastName("" + i);
            user.setPassword("password123");
            user.setEmailVerified(true);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user = entityManager.persistAndFlush(user);

            Review review = createReview(user, testProduct1, 5, "Review " + i, "Content " + i, LocalDateTime.now().minusMinutes(i));
            entityManager.persistAndFlush(review);
        }

        // When - Get first page (10 items)
        Pageable firstPage = PageRequest.of(0, 10);
        Page<Review> firstResult = reviewRepository.findByProductOrderByCreatedAtDesc(testProduct1, firstPage);

        // Then
        assertThat(firstResult.getContent()).hasSize(10);
        assertThat(firstResult.getTotalElements()).isEqualTo(15);
        assertThat(firstResult.getTotalPages()).isEqualTo(2);
        assertThat(firstResult.isFirst()).isTrue();
        assertThat(firstResult.isLast()).isFalse();

        // When - Get second page (5 remaining items)
        Pageable secondPage = PageRequest.of(1, 10);
        Page<Review> secondResult = reviewRepository.findByProductOrderByCreatedAtDesc(testProduct1, secondPage);

        // Then
        assertThat(secondResult.getContent()).hasSize(5);
        assertThat(secondResult.getTotalElements()).isEqualTo(15);
        assertThat(secondResult.getTotalPages()).isEqualTo(2);
        assertThat(secondResult.isFirst()).isFalse();
        assertThat(secondResult.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should not return reviews from different products")
    void shouldNotReturnReviewsFromDifferentProducts() {
        // Given
        Review review1 = createReview(testUser1, testProduct1, 5, "Great!", "Excellent product", LocalDateTime.now());
        Review review2 = createReview(testUser2, testProduct2, 4, "Good", "Good product", LocalDateTime.now());

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);

        // When
        Page<Review> result = reviewRepository.findByProductOrderByCreatedAtDesc(testProduct1, PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(review1);
        assertThat(result.getContent().get(0).getProduct()).isEqualTo(testProduct1);
    }

    @Test
    @DisplayName("Should handle large datasets efficiently")
    void shouldHandleLargeDatasetsEfficiently() {
        // Given - create many reviews
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("bulk" + i + "@example.com");
            user.setUsername("bulk" + i);
            user.setFirstName("Bulk");
            user.setLastName("User" + i);
            user.setPassword("password123");
            user.setEmailVerified(true);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user = entityManager.persistAndFlush(user);

            Review review = createReview(user, testProduct1, (i % 5) + 1, "Review " + i, "Content " + i, LocalDateTime.now().minusSeconds(i));
            entityManager.persistAndFlush(review);
        }

        // When - measure performance (basic test)
        long startTime = System.currentTimeMillis();

        Page<Review> result = reviewRepository.findByProductOrderByCreatedAtDesc(testProduct1, PageRequest.of(0, 20));
        Double averageRating = reviewRepository.findAverageRatingByProduct(testProduct1);
        long count = reviewRepository.countByProduct(testProduct1);
        List<Object[]> distribution = reviewRepository.countReviewsByRatingForProduct(testProduct1);

        long endTime = System.currentTimeMillis();

        // Then
        assertThat(result.getContent()).hasSize(20);
        assertThat(result.getTotalElements()).isEqualTo(100);
        assertThat(count).isEqualTo(100);
        assertThat(averageRating).isNotNull();
        assertThat(distribution).hasSize(5); // Ratings 1-5

        // Performance should be reasonable (less than 1 second for this test)
        assertThat(endTime - startTime).isLessThan(1000);
    }

    // Helper methods
    private Review createReview(User user, Product product, Integer rating, String title, String reviewText, LocalDateTime createdAt) {
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setTitle(title);
        review.setReviewText(reviewText);
        review.setCreatedAt(createdAt);
        review.setIsVerifiedPurchase(false);
        review.setHelpfulCount(0);
        return review;
    }

    private void createAndPersistReview(User user, Product product, Integer rating, String title, String reviewText) {
        Review review = createReview(user, product, rating, title, reviewText, LocalDateTime.now());
        entityManager.persistAndFlush(review);
    }
}