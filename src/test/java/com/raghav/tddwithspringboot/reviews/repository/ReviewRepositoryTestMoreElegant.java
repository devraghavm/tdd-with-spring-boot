package com.raghav.tddwithspringboot.reviews.repository;

import com.raghav.tddwithspringboot.reviews.model.Review;
import com.raghav.tddwithspringboot.reviews.model.ReviewEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@ExtendWith(MongoSpringExtension.class)
public class ReviewRepositoryTestMoreElegant {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewRepository repository;

    /**
     * MongoSpringExtension method that returns the autowired MongoTemplate to use for MongoDB interactions.
     * @return  The autowired MongoTemplate instance.
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Test
    @DisplayName("Test More Elegant Save Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testSave() {
        // Create a test review
        Review review = new Review("3", 10, 1);
        review.getEntries().add(new ReviewEntry("test-user", new Date(), "This is a review"));

        // Persist the review to MongoDB
        Review savedReview = repository.save(review);

        // Retrieve the review
        Optional<Review> loadedReview = repository.findById(savedReview.getId());

        // Validations
        Assertions.assertTrue(loadedReview.isPresent(), "The review cannot be empty");
        loadedReview.ifPresent(r -> {
            Assertions.assertEquals("3", r.getId(), "Review ID should be 3");
            Assertions.assertEquals(10, r.getProductId().intValue(), "Review Product ID should be 10");
            Assertions.assertEquals(1, r.getVersion(), "Review Version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have 1 entry");
        });
    }

    @Test
    @DisplayName("Test More Elegant findAll Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindAll() {
        List<Review> reviews = repository.findAll();
        Assertions.assertEquals(2, reviews.size(), "Should be 2 reviews in the database");
        reviews.stream().forEach(System.out::println);
    }

    @Test
    @DisplayName("Test More Elegant findAll6 Success")
    @MongoDataFile(value = "sample6.json", classType = Review.class, collectionName = "Reviews")
    void testFindAll6() {
        List<Review> reviews = repository.findAll();
        Assertions.assertEquals(6, reviews.size(), "Should be 2 reviews in the database");
        reviews.stream().forEach(System.out::println);
    }

    @Test
    @DisplayName("Test More Elegant findById Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByIdSuccess() {
        Optional<Review> review = repository.findById("1");
        Assertions.assertTrue(review.isPresent(), "We should have found a review with ID 1");
        review.ifPresent(r -> {
            Assertions.assertEquals("1", r.getId(), "Review ID should be 1");
            Assertions.assertEquals(1, r.getProductId().intValue(), "Review Product ID should be 1");
            Assertions.assertEquals(1, r.getVersion(), "Review Version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have 1 entry");
        });
    }

    @Test
    @DisplayName("Test More Elegant findById Failure")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByIdFailure() {
        Optional<Review> review = repository.findById("99");
        Assertions.assertFalse(review.isPresent(), "We should not find a review with ID 99");
    }

    @Test
    @DisplayName("Test More Elegant findByProductId Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByProductIdSuccess() {
        Optional<Review> review = repository.findByProductId(1);
        Assertions.assertTrue(review.isPresent(), "There should be a review for product ID 1");
    }

    @Test
    @DisplayName("Test More Elegant findByProductId Failure")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testFindByProductIdFailure() {
        Optional<Review> review = repository.findByProductId(99);
        Assertions.assertFalse(review.isPresent(), "We should not find a review with Product ID 99");
    }

    @Test
    @DisplayName("Test More Elegant update Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void testUpdate() {
        // Retrieve review 2
        Optional<Review> review = repository.findById("2");
        Assertions.assertTrue(review.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(3, review.get().getEntries().size(), "There should be 3 review items");

        // Add entry to the review and save
        Review reviewToUpdate = review.get();
        reviewToUpdate.getEntries().add(new ReviewEntry("test-user2", new Date(), "This is the review 4"));
        repository.save(reviewToUpdate);

        // Retrieve the review again and validate that it now has 4 entries
        Optional<Review> updatedReview = repository.findById("2");
        Assertions.assertTrue(updatedReview.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(4, updatedReview.get().getEntries().size(), "There should be 4 review entries");

    }

    @Test
    @DisplayName("Test More Elegant delete Success")
    @MongoDataFile(value = "sample.json", classType = Review.class, collectionName = "Reviews")
    void  testDelete() {
        // Delete the review 2
        repository.deleteById("2");

        // Confirm that it no longer in the database
        Optional<Review> review = repository.findById("2");
        Assertions.assertFalse(review.isPresent(), "Review 2 should now be deleted from the database");
    }
}
