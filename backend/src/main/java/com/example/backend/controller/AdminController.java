package com.example.backend.controller;

import com.example.backend.entity.UserEntity;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // 1. Get all pending provider applicants
    @GetMapping("/applicants")
    public ResponseEntity<List<UserEntity>> getPendingApplicants() {
        return ResponseEntity.ok(userRepository.findByProviderStatus("PENDING"));
    }

    // 2. Accept or Reject a provider applicant
    @PutMapping("/applicants/{id}/status")
    public ResponseEntity<String> updateApplicantStatus(
            @PathVariable Integer id,
            @RequestParam String status) { // pass "APPROVED" or "REJECTED"

        return userRepository.findById(id).map(user -> {
            String upperStatus = status.toUpperCase();
            if (upperStatus.equals("APPROVED")) {
                user.setIsProvider(true);
                user.setProviderStatus("APPROVED");
            } else if (upperStatus.equals("REJECTED")) {
                user.setIsProvider(false);
                user.setProviderStatus("REJECTED");
            } else {
                return ResponseEntity.badRequest().body("Invalid status format.");
            }
            userRepository.save(user);
            return ResponseEntity.ok("Provider status updated to: " + upperStatus);
        }).orElse(ResponseEntity.notFound().build());
    }
}
