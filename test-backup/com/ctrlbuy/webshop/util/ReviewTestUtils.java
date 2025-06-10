package com.ctrlbuy.webshop.util;

import com.ctrlbuy.webshop.entity.Review;
import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReviewTestUtils {

    private static final Random random = new Random();

    private static final String[] TITLES = {
            "Excellent product!",
            "Great quality",
            "Good value for money",
            "Decent product",
            "Not bad",
            "Could be better",
            "Disappointed",
            "Fantastic!",
            "Love it!",
            "Perfect choice"
    };

    private static final String[] REVIEW_TEXTS = {
            "This product exceeded my expectations. Highly recommended!",
            "Good quality product. Fast delivery and great customer service.",
            "Average product. Nothing special but does the job.",
            "Could be improved. Had some issues with quality.",
            "Not worth the price. Expected better quality.",
            "Fantastic product! Will definitely buy again.",
            "Perfect for my needs. Great build quality.",
            "Decent product but shipping was slow.",
            "Excellent value for money. Very satisfied.",
            "Outstanding quality and fast delivery!"
    };

    public static Review createValidReview(User user, Product product) {
        return createValidReview(user, product, 5, "Great product!", "This is an excellent product.");
    }

    public static Review createValidReview(User user, Product product, Integer rating, String title, String reviewText) {
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setTitle(title);
        review.setReviewText(reviewText);
        review.setCreatedAt(LocalDateTime.now());
        review.setIsVerifiedPurchase(false);
        review.setHelpfulCount(0);
        return review;
    }

    public static Review createRandomReview(User user, Product product) {
        int rating = random.nextInt(5) + 1; // 1-5
        String title = TITLES[random.nextInt(TITLES.length)];
        String reviewText = REVIEW_TEXTS[random.nextInt(REVIEW_TEXTS.length)];

        return createValidReview(user, product, rating, title, reviewText);
    }

    public static List<Review> createMultipleReviews(List<User> users, Product product, int count) {
        List<Review> reviews = new ArrayList<>();

        for (int i = 0; i < count && i < users.size(); i++) {
            Review review = createRandomReview(users.get(i), product);
            review.setCreatedAt(LocalDateTime.now().minusDays(i));
            reviews.add(review);
        }

        return reviews;
    }

    public static Review createReviewWithValidation(User user, Product product, Integer rating, String title, String reviewText) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (title == null || title.trim().isEmpty() || title.length() > 100) {
            throw new IllegalArgumentException("Title must be 1-100 characters");
        }
        if (reviewText == null || reviewText.trim().isEmpty() || reviewText.length() > 1000) {
            throw new IllegalArgumentException("Review text must be 1-1000 characters");
        }

        return createValidReview(user, product, rating, title, reviewText);
    }

    public static User createTestUser(Long id, String email, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User" + id);
        user.setPassword("password123");
        user.setEmailVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static Product createTestProduct(Long id, String name, Double price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Test product: " + name);
        product.setPrice(price);
        product.setStock(10);
        product.setActive(true);
        return product;
    }

    public static List<User> createMultipleUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            users.add(createTestUser((long) i, "user" + i + "@example.com", "user" + i));
        }
        return users;
    }

    public static Review createReviewWithDate(User user, Product product, LocalDateTime createdAt) {
        Review review = createValidReview(user, product);
        review.setCreatedAt(createdAt);
        return review;
    }

    public static Review createVerifiedPurchaseReview(User user, Product product) {
        Review review = createValidReview(user, product);
        review.setIsVerifiedPurchase(true);
        return review;
    }

    public static Review createHelpfulReview(User user, Product product, int helpfulCount) {
        Review review = createValidReview(user, product);
        review.setHelpfulCount(helpfulCount);
        return review;
    }
}