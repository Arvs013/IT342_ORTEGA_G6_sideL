package com.example.backend.controller;

import com.example.backend.entity.ProviderApplicationEntity;
import com.example.backend.entity.ReportEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.ProviderApplicationRepository;
import com.example.backend.repository.ReportRepository;
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

    @Autowired
    private ProviderApplicationRepository providerApplicationRepository;

    @Autowired
    private ReportRepository reportRepository;

    // 1. Get all pending provider applicants
    @GetMapping("/applicants")
    public ResponseEntity<List<ProviderApplicationEntity>> getPendingApplicants() {
        return ResponseEntity.ok(providerApplicationRepository.findByStatus("PENDING"));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportEntity>> getReports() {
        return ResponseEntity.ok(reportRepository.findAllByOrderByCreatedAtDesc());
    }

    // 2. Accept or Reject a provider applicant
    @PutMapping("/applicants/{id}/status")
    public ResponseEntity<String> updateApplicantStatus(
            @PathVariable Integer id,
            @RequestParam String status) { // pass "APPROVED" or "REJECTED"

        return providerApplicationRepository.findById(id).map(application -> {
            UserEntity user = application.getUser();
            String upperStatus = status.toUpperCase();
            if (upperStatus.equals("APPROVED")) {
                user.setIsProvider(true);
                user.setProviderStatus("APPROVED");
                application.setStatus("APPROVED");
            } else if (upperStatus.equals("REJECTED")) {
                user.setIsProvider(false);
                user.setProviderStatus("REJECTED");
                application.setStatus("REJECTED");
            } else {
                return ResponseEntity.badRequest().body("Invalid status format.");
            }
            userRepository.save(user);
            providerApplicationRepository.save(application);
            return ResponseEntity.ok("Provider status updated to: " + upperStatus);
        }).orElse(ResponseEntity.notFound().build());
    }
}
