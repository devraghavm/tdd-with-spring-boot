package com.raghav.tddwithspringboot.reviews.service;

import com.raghav.tddwithspringboot.reviews.model.Review;
import com.raghav.tddwithspringboot.reviews.repository.ReviewRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    ReviewService service;

    @MockBean
    ReviewRepository repository;

    @Test
    @DisplayName("Test findById Success")
    void testFindById() {
        // Setup Mock Repository
        Review mockReview = new Review("1", 1, 1);
        doReturn(Optional.of(mockReview)).when(repository).findById("1");

        // Execute the service call
        Optional<Review> returnedReview = repository.findById("1");

        // Assert the response
        Assertions.assertTrue(returnedReview.isPresent(), "Review was not found");
        Assertions.assertSame(returnedReview.get(), mockReview, "Review should be same");
    }

    @Test
    @DisplayName("Test findById Not Found")
    void testFindByIdNotFound() {
        // Setup Mock Repository
        doReturn(Optional.empty()).when(repository).findById("1");

        // Execute service call
        Optional<Review> returnedReview = repository.findById("1");

        // Assert the response
        Assertions.assertFalse(returnedReview.isPresent(), "Review should not be found");
    }

    @Test
    @DisplayName("Test findAll Success")
    void testFindAllSuccess() {
        // Setup Mock Repository
        Review mockReview = new Review("1", 1, 1);
        Review mockReview2 = new Review("2", 1, 2);
        doReturn(List.of(mockReview, mockReview2)).when(repository).findAll();

        // Execute service call
        List<Review> returnedReviews = repository.findAll();

        // Assert the response
        Assertions.assertEquals(2, returnedReviews.size(), "findAll should return 2 reviews");
    }

    @Test
    @DisplayName("Test save review")
    void testSaveReview() {
        // Setup Mock Repository
        Review mockReview = new Review("1", 1, 1);
        doReturn(mockReview).when(repository).save(any());

        // Execute service call
        Review savedReview = repository.save(mockReview);

        // Assert the response
        Assertions.assertNotNull(savedReview, "The saved review should not be null");
        Assertions.assertEquals(1, savedReview.getVersion().intValue(), "The version for a new review should be 1");
    }
}
