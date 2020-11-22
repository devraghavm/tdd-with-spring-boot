package com.raghav.tddwithspringboot.reviews.repository;

import com.raghav.tddwithspringboot.reviews.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Optional<Review> findByProductId(Integer productId);
}
