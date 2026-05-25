package com.example.backend.controller;

import com.example.backend.entity.GigEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.GigLikeRepository;
import com.example.backend.repository.GigRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private GigLikeRepository gigLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/services")
    public ResponseEntity<List<GigEntity>> getServices(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userId) {

        String normalizedCategory = normalize(category);
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

        List<GigEntity> services = gigRepository.findAll().stream()
                .filter(this::isActiveGig)
                .filter(gig -> normalizedCategory.isEmpty() || normalize(gig.getCategory()).equals(normalizedCategory))
                .filter(gig -> normalizedKeyword.isEmpty() || serviceMatchesKeyword(gig, normalizedKeyword))
                .sorted(Comparator.comparing(GigEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        services.forEach(gig -> attachLikeData(gig, userId));
        return ResponseEntity.ok(services);
    }

    @GetMapping("/providers")
    public ResponseEntity<List<Map<String, Object>>> getProviders() {
        List<Map<String, Object>> providers = userRepository.findByIsProviderTrue().stream()
                .map(this::providerResponse)
                .toList();

        return ResponseEntity.ok(providers);
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<?> getProvider(@PathVariable Integer id) {
        return userRepository.findById(id).map(provider -> {
            if (!Boolean.TRUE.equals(provider.getIsProvider())) {
                return ResponseEntity.badRequest().body("User is not an approved provider.");
            }

            Map<String, Object> response = providerResponse(provider);
            List<GigEntity> services = gigRepository.findByProvider_UserID(provider.getUserID());
            services.forEach(gig -> attachLikeData(gig, id));
            response.put("services", services);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    private boolean serviceMatchesKeyword(GigEntity gig, String keyword) {
        String searchable = String.join(" ",
                safe(gig.getTitle()),
                safe(gig.getDescription()),
                safe(gig.getCategory()),
                safe(gig.getProvider() == null ? "" : gig.getProvider().getFirstname()),
                safe(gig.getProvider() == null ? "" : gig.getProvider().getLastname())
        ).toLowerCase();

        return searchable.contains(keyword);
    }

    private Map<String, Object> providerResponse(UserEntity provider) {
        Map<String, Object> response = new HashMap<>();
        response.put("userID", provider.getUserID());
        response.put("firstname", provider.getFirstname());
        response.put("lastname", provider.getLastname());
        response.put("email", provider.getEmail());
        response.put("phoneNumber", provider.getPhoneNumber());
        response.put("address", provider.getAddress());
        response.put("bio", provider.getBio());
        response.put("profileImageUrl", provider.getProfileImageUrl());
        response.put("providerStatus", provider.getProviderStatus());
        return response;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase().replaceAll("[\\s-]+", "_");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private boolean isActiveGig(GigEntity gig) {
        return gig.getStatus() == null || gig.getStatus().isBlank() || "ACTIVE".equalsIgnoreCase(gig.getStatus());
    }

    private void attachLikeData(GigEntity gig, Integer userId) {
        gig.setLikeCount(gigLikeRepository.countByGig_GigID(gig.getGigID()));
        gig.setLikedByCurrentUser(
                userId != null && gigLikeRepository.existsByGig_GigIDAndUser_UserID(gig.getGigID(), userId)
        );
    }
}
