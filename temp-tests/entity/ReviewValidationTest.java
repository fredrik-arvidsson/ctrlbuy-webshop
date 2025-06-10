package com.ctrlbuy.webshop.entity;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Review Entity Validation Tests")
class ReviewValidationTest {

    private Validator validator;
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
    }

    @Test
    @DisplayName("Should validate valid review successfully")
    void shouldValidateValidReviewSuccessfully() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText("This is an excellent product. Highly recommended!");
        review.setCreatedAt(LocalDateTime.now());

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject null rating")
    void shouldRejectNullRating() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(null); // Invalid
        review.setTitle("Great product!");
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("null");
    }

    @Test
    @DisplayName("Should reject rating less than 1")
    void shouldRejectRatingLessThan1() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(0); // Invalid
        review.setTitle("Great product!");
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Betyg måste vara minst 1 stjärna");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("rating");
    }

    @Test
    @DisplayName("Should reject rating greater than 5")
    void shouldRejectRatingGreaterThan5() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(6); // Invalid
        review.setTitle("Great product!");
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Betyg kan vara högst 5 stjärnor");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("rating");
    }

    @Test
    @DisplayName("Should accept valid ratings 1-5")
    void shouldAcceptValidRatings() {
        for (int rating = 1; rating <= 5; rating++) {
            // Given
            Review review = new Review();
            review.setUser(testUser);
            review.setProduct(testProduct);
            review.setRating(rating);
            review.setTitle("Rating " + rating);
            review.setReviewText("Review with rating " + rating);

            // When
            Set<ConstraintViolation<Review>> violations = validator.validate(review);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("Should reject blank title")
    void shouldRejectBlankTitle() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle(""); // Invalid
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Titel är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("Should reject null title")
    void shouldRejectNullTitle() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle(null); // Invalid
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Titel är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("Should reject title longer than 100 characters")
    void shouldRejectTitleLongerThan100Characters() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("A".repeat(101)); // Invalid - 101 characters
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Titel får vara högst 100 tecken");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("Should accept title with exactly 100 characters")
    void shouldAcceptTitleWith100Characters() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("A".repeat(100)); // Valid - exactly 100 characters
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank review text")
    void shouldRejectBlankReviewText() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText(""); // Invalid

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Recensionstext är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("reviewText");
    }

    @Test
    @DisplayName("Should reject null review text")
    void shouldRejectNullReviewText() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText(null); // Invalid

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Recensionstext är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("reviewText");
    }

    @Test
    @DisplayName("Should reject review text longer than 1000 characters")
    void shouldRejectReviewTextLongerThan1000Characters() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText("A".repeat(1001)); // Invalid - 1001 characters

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Recensionstext får vara högst 1000 tecken");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("reviewText");
    }

    @Test
    @DisplayName("Should accept review text with exactly 1000 characters")
    void shouldAcceptReviewTextWith1000Characters() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText("A".repeat(1000)); // Valid - exactly 1000 characters

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject whitespace-only title")
    void shouldRejectWhitespaceOnlyTitle() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("   "); // Invalid - only whitespace
        review.setReviewText("This is an excellent product.");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Titel är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("Should reject whitespace-only review text")
    void shouldRejectWhitespaceOnlyReviewText() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Great product!");
        review.setReviewText("   "); // Invalid - only whitespace

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Review> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Recensionstext är obligatorisk");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("reviewText");
    }

    @Test
    @DisplayName("Should have multiple validation errors")
    void shouldHaveMultipleValidationErrors() {
        // Given
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(0); // Invalid
        review.setTitle(""); // Invalid
        review.setReviewText(null); // Invalid

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).hasSize(3);

        // Verify each violation
        boolean hasRatingViolation = false;
        boolean hasTitleViolation = false;
        boolean hasReviewTextViolation = false;

        for (ConstraintViolation<Review> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            switch (propertyPath) {
                case "rating":
                    hasRatingViolation = true;
                    assertThat(violation.getMessage()).isEqualTo("Betyg måste vara minst 1 stjärna");
                    break;
                case "title":
                    hasTitleViolation = true;
                    assertThat(violation.getMessage()).isEqualTo("Titel är obligatorisk");
                    break;
                case "reviewText":
                    hasReviewTextViolation = true;
                    assertThat(violation.getMessage()).isEqualTo("Recensionstext är obligatorisk");
                    break;
            }
        }

        assertThat(hasRatingViolation).isTrue();
        assertThat(hasTitleViolation).isTrue();
        assertThat(hasReviewTextViolation).isTrue();
    }

    @Test
    @DisplayName("Should validate default values correctly")
    void shouldValidateDefaultValuesCorrectly() {
        // Given
        Review review = new Review(testUser, testProduct, 4, "Good product", "Nice quality product");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
        assertThat(review.getCreatedAt()).isNotNull();
        assertThat(review.getIsVerifiedPurchase()).isFalse();
        assertThat(review.getHelpfulCount()).isEqualTo(0);
        assertThat(review.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should validate edge case inputs")
    void shouldValidateEdgeCaseInputs() {
        // Given - Test with special characters and unicode
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(5);
        review.setTitle("Bra produkt! ÅÄÖ 😊");
        review.setReviewText("Excellent product with special chars: €£¥ and emojis 🎉🔥");

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate minimum length inputs")
    void shouldValidateMinimumLengthInputs() {
        // Given - Test with minimal valid inputs
        Review review = new Review();
        review.setUser(testUser);
        review.setProduct(testProduct);
        review.setRating(1); // Minimum valid rating
        review.setTitle("A"); // Minimum valid title (1 character)
        review.setReviewText("B"); // Minimum valid review text (1 character)

        // When
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate boundary values")
    void shouldValidateBoundaryValues() {
        // Test rating boundaries
        for (int rating : new int[]{1, 5}) {
            Review review = new Review();
            review.setUser(testUser);
            review.setProduct(testProduct);
            review.setRating(rating);
            review.setTitle("Boundary test");
            review.setReviewText("Testing boundary rating: " + rating);

            Set<ConstraintViolation<Review>> violations = validator.validate(review);
            assertThat(violations).isEmpty();
        }

        // Test invalid boundaries
        for (int rating : new int[]{0, 6}) {
            Review review = new Review();
            review.setUser(testUser);
            review.setProduct(testProduct);
            review.setRating(rating);
            review.setTitle("Boundary test");
            review.setReviewText("Testing invalid boundary rating: " + rating);

            Set<ConstraintViolation<Review>> violations = validator.validate(review);
            assertThat(violations).hasSize(1);
        }
    }
}