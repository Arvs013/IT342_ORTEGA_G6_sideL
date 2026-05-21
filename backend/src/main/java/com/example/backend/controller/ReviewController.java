package com.example.backend.controller;

import com.example.backend.entity.GigEntity;
import com.example.backend.entity.ReviewEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.GigRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/gig/{gigId}")
    public ResponseEntity<List<ReviewEntity>> getGigReviews(@PathVariable Integer gigId) {
        return ResponseEntity.ok(reviewRepository.findByGig_GigID(gigId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ReviewEntity>> getProviderReviews(@PathVariable Integer providerId) {
        return ResponseEntity.ok(reviewRepository.findByGig_Provider_UserID(providerId));
    }

    @PostMapping("/gig/{gigId}/client/{clientId}")
    public ResponseEntity<?> createReview(
            @PathVariable Integer gigId,
            @PathVariable Integer clientId,
            @RequestBody ReviewEntity review) {

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
        }

        GigEntity gig = gigRepository.findById(gigId).orElse(null);
        UserEntity client = userRepository.findById(clientId).orElse(null);

        if (gig == null || client == null) {
            return ResponseEntity.notFound().build();
        }

        if (bookingRepository.findByClient_UserIDAndGig_GigIDAndStatus(clientId, gigId, "COMPLETED").isEmpty()) {
            return ResponseEntity.badRequest().body("You can only review a service after a completed booking.");
        }

        review.setGig(gig);
        review.setClient(client);
        return ResponseEntity.ok(reviewRepository.save(review));
    }
}
