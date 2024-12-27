package com.example.demo.services;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.models.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewDTO reviewDTO);

    List<Review> getReviewByProduct(Long productId) throws Exception;

    List<Review> getAllReviews();

    Review updateReview(Long id, ReviewDTO reviewDTO) throws Exception;
    void deleteReview(Long id);
}
