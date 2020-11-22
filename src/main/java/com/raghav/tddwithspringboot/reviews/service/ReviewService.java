package com.raghav.tddwithspringboot.reviews.service;

import com.raghav.tddwithspringboot.reviews.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Optional<Review> findById(String id);

    Optional<Review> findByProductId(Integer productId);

    List<Review> findAll();

    Review save(Review review);

    Review update(Review review);

    void delete(String id);
}
