package com.raghav.tddwithspringboot.reviews.controller;

import com.raghav.tddwithspringboot.reviews.model.Review;
import com.raghav.tddwithspringboot.reviews.model.ReviewEntry;
import com.raghav.tddwithspringboot.reviews.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RestController
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /**
     * Returns the review with the specified ID
     * @param id    The ID of the review to return
     * @return      The review with the specified ID, or 404 not found
     */
    @GetMapping("/review/{id}")
    public ResponseEntity<?> getReview(@PathVariable String id) {
        return service.findById(id)
                .map(review -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Integer.toString(review.getVersion()))
                                .location(new URI("/review/" + review.getId()))
                                .body(review);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns either all reviews or the review of the specified productId
     * @param productId The productId for the review to return. This request parameter is optional, if it is omitted.
     * @return          A list of reviews
     */
    @GetMapping("/reviews")
    public Iterable<Review> getReviews(@RequestParam(value = "productId", required = false) final Optional<String> productId) {
        return productId.map(pid -> {
            return service.findByProductId(Integer.valueOf(pid))
                    .map(Arrays::asList)
                    .orElseGet(ArrayList::new);
        }).orElse(service.findAll());
    }

    /**
     * Creates a review
     * @param review    The review to create
     * @return          The newly created review
     */
    @PostMapping("/review")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        logger.info("Creating new review for product id: {}, {}", review.getProductId(), review);

        // Set the date for any entries in the review to now since we're creating the review now
        review.getEntries().forEach(entry -> entry.setDate(new Date()));

        // Save the review to the database
        Review newReview = service.save(review);
        logger.info("Saved review: {}", newReview);
        try {
            // Build a created response
            return ResponseEntity
                    .created(new URI("/review/" + newReview.getId()))
                    .eTag(Integer.toString(newReview.getVersion()))
                    .body(newReview);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates new review entry for the review with the specified productId.
     * @param productId The productId of the review to which to add the new review entry.
     * @param entry     The entry to add to the review
     * @return          The complete updated review
     */
    @PostMapping("/review/{productId}/entry")
    public ResponseEntity<Review> addEntryToReview(@PathVariable Integer productId, @RequestBody ReviewEntry entry) {
        logger.info("Add review entry for the product id: {}, {}", productId, entry);

        // Retrieve the review for the specified productId; If there is no review, create a new one
        Review review = service.findByProductId(productId).orElseGet(() -> new Review(productId));

        // Add this new entry to the review
        entry.setDate(new Date());
        review.getEntries().add(entry);

        // Save the review
        Review updatedReview = service.save(review);
        logger.info("Updated review: {}", updatedReview);

        try {
            return ResponseEntity
                    .ok()
                    .location(new URI("/review/" + updatedReview.getId()))
                    .eTag(Integer.toString(updatedReview.getVersion()))
                    .body(updatedReview);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes the review with the specified ID. Note that this is the review ID not the product ID.
     * @param id    The ID of the review to delete
     * @return      A 200 OK on success, a 404 Not Found if the review does not exist
     */
    @DeleteMapping("/review/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        logger.info("Deleting the review with ID: {}", id);

        // Get the existing review
        Optional<Review> existingReview = service.findById(id);

        // Delete the review if it exists in the database
        return existingReview.map(review -> {
            service.delete(review.getId());
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
