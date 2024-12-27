package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Product;
import com.example.demo.models.Review;
import com.example.demo.models.User;
import com.example.demo.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @GetMapping("")
    public ResponseEntity<List<Review>> getReviews(
    ) {
        List<Review> reviews=reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
    @PostMapping("")
    public ResponseEntity<?> createReview (@Valid @RequestBody
                                            ReviewDTO reviewDTO
//
    ) throws IOException, DataNotFoundException {
        User loginUser=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != reviewDTO.getUserId()) {
            return ResponseEntity.badRequest().body("không thể commet với id khác");
        }
        Review newReview=reviewService.createReview(reviewDTO);
        return ResponseEntity.ok(newReview);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable("id") Long id,
            @Valid @RequestBody ReviewDTO reviewDTO,
            Authentication authentication
    ) throws Exception {
        try {
            Review review = reviewService.updateReview(id, reviewDTO);
            return ResponseEntity.ok(review);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
