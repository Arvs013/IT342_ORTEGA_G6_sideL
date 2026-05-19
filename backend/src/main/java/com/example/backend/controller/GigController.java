package com.example.backend.controller;

import com.example.backend.entity.GigEntity;
import com.example.backend.entity.ProviderApplicationEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.GigRepository;
import com.example.backend.repository.ProviderApplicationRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gigs")
public class GigController {

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderApplicationRepository providerApplicationRepository;

    // 1. Client applies to become a Provider
    @PostMapping("/apply/{userId}")
    public ResponseEntity<?> applyToBeProvider(
            @PathVariable Integer userId,
            @RequestBody ProviderApplicationEntity application) {
        return userRepository.findById(userId).map(user -> {
            if (user.getIsProvider()) {
                return ResponseEntity.badRequest().body("User is already an approved provider.");
            }
            if (!providerApplicationRepository.findByUser_UserIDAndStatus(userId, "PENDING").isEmpty()) {
                return ResponseEntity.badRequest().body("You already have a pending provider application.");
            }

            application.setUser(user);
            application.setStatus("PENDING");
            application.setCategory(application.getCategory().toUpperCase());
            user.setProviderStatus("PENDING");
            userRepository.save(user);
            return ResponseEntity.ok(providerApplicationRepository.save(application));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 2. Approved Provider posts a new Gig
    @PostMapping("/create/{providerId}")
    public ResponseEntity<?> createGig(@PathVariable Integer providerId, @RequestBody GigEntity gig) {
        return userRepository.findById(providerId).map(user -> {
            if (!user.getIsProvider()) {
                return ResponseEntity.badRequest().body("User is not an approved provider.");
            }
            List<String> images = parseImageUrls(gig.getImageUrls());
            if (images.size() > 5) {
                return ResponseEntity.badRequest().body("A gig can only have up to 5 images.");
            }
            gig.setImageUrls(String.join("\n", images));
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

    // 4. Provider views their own posted gigs
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<GigEntity>> getProviderGigs(@PathVariable Integer providerId) {
        return ResponseEntity.ok(gigRepository.findByProvider_UserID(providerId));
    }

    @PutMapping("/{gigId}/provider/{providerId}")
    public ResponseEntity<?> updateGig(
            @PathVariable Integer gigId,
            @PathVariable Integer providerId,
            @RequestBody GigEntity input) {

        return gigRepository.findById(gigId).map(gig -> {
            if (gig.getProvider() == null || !gig.getProvider().getUserID().equals(providerId)) {
                return ResponseEntity.badRequest().body("You can only edit your own gigs.");
            }

            List<String> images = parseImageUrls(input.getImageUrls());
            if (images.size() > 5) {
                return ResponseEntity.badRequest().body("A gig can only have up to 5 images.");
            }

            gig.setTitle(input.getTitle());
            gig.setDescription(input.getDescription());
            gig.setPrice(input.getPrice());
            gig.setCategory(input.getCategory());
            gig.setImageUrls(String.join("\n", images));
            return ResponseEntity.ok(gigRepository.save(gig));
        }).orElse(ResponseEntity.notFound().build());
    }

    private List<String> parseImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.isBlank()) {
            return List.of();
        }

        return Arrays.stream(imageUrls.split("[\\n,;]+"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }
}
