package com.example.demo.services;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Product;
import com.example.demo.models.Review;
import com.example.demo.models.User;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public Review createReview(ReviewDTO reviewDTO) {
        User user = userRepository.findById(reviewDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(reviewDTO.getProductId()).orElse(null);
        if (user == null || product == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        Review newReview=Review
                .builder()
                .user(user)
                .product(product)
                .comment(reviewDTO.getComment()).build();
        return reviewRepository.save(newReview);
    }


    @Override
    public List<Review> getReviewByProduct(Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));
        return reviewRepository.findByProduct(product);
    }

    @Override
    public List<Review> getAllReviews() {
        List<Review> reviews=reviewRepository.findAll();
        return reviews;
    }

    @Override
    @Transactional
    public Review updateReview(Long id, ReviewDTO reviewDTO) throws DataNotFoundException {
        Review existsReview = reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find review with id " + id));
        existsReview.setComment(reviewDTO.getComment());
        return reviewRepository.save(existsReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
