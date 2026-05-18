package com.example.backend.controller;

import com.example.backend.entity.GigEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.GigRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gigs")
public class GigController {

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Client applies to become a Provider
    @PostMapping("/apply/{userId}")
    public ResponseEntity<String> applyToBeProvider(@PathVariable Integer userId) {
        return userRepository.findById(userId).map(user -> {
            user.setProviderStatus("PENDING");
            userRepository.save(user);
            return ResponseEntity.ok("Application submitted to Admin.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 2. Approved Provider posts a new Gig
    @PostMapping("/create/{providerId}")
    public ResponseEntity<?> createGig(@PathVariable Integer providerId, @RequestBody GigEntity gig) {
        return userRepository.findById(providerId).map(user -> {
            if (!user.getIsProvider()) {
                return ResponseEntity.badRequest().body("User is not an approved provider.");
            }
            gig.setProvider(user);
            GigEntity savedGig = gigRepository.save(gig);
            return ResponseEntity.ok(savedGig);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Browse all gigs or filter by category (PLUMBING, GADGETS, CAR, etc.)
    @GetMapping
    public ResponseEntity<List<GigEntity>> getAllGigs(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(gigRepository.findByCategory(category.toUpperCase()));
        }
        return ResponseEntity.ok(gigRepository.findAll());
    }
}
