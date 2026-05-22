package com.example.backend.controller;

import com.example.backend.entity.BookingEntity;
import com.example.backend.entity.GigEntity;
import com.example.backend.entity.ProviderApplicationEntity;
import com.example.backend.entity.ReportEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.GigRepository;
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
    private BookingRepository bookingRepository;

    @Autowired
    private GigRepository gigRepository;

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

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingEntity>> getBookings() {
        return ResponseEntity.ok(bookingRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/gigs")
    public ResponseEntity<List<GigEntity>> getGigs() {
        return ResponseEntity.ok(gigRepository.findAll());
    }

    @PutMapping("/reports/{id}/status")
    public ResponseEntity<?> updateReportStatus(@PathVariable Integer id, @RequestParam String status) {
        return reportRepository.findById(id).map(report -> {
            String upperStatus = status.toUpperCase();
            if (!List.of("OPEN", "REVIEWED", "RESOLVED").contains(upperStatus)) {
                return ResponseEntity.badRequest().body("Invalid report status.");
            }

            report.setStatus(upperStatus);
            return ResponseEntity.ok(reportRepository.save(report));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id, @RequestParam String status) {
        return userRepository.findById(id).map(user -> {
            String upperStatus = status.toUpperCase();
            if (!List.of("ACTIVE", "DISABLED").contains(upperStatus)) {
                return ResponseEntity.badRequest().body("Invalid user status.");
            }
            if (Boolean.TRUE.equals(user.getIsAdmin()) && "DISABLED".equals(upperStatus)) {
                return ResponseEntity.badRequest().body("Admin accounts cannot be disabled here.");
            }

            user.setAccountStatus(upperStatus);
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/gigs/{id}/status")
    public ResponseEntity<?> updateGigStatus(@PathVariable Integer id, @RequestParam String status) {
        return gigRepository.findById(id).map(gig -> {
            String upperStatus = status.toUpperCase();
            if (!List.of("ACTIVE", "DISABLED").contains(upperStatus)) {
                return ResponseEntity.badRequest().body("Invalid gig status.");
            }

            gig.setStatus(upperStatus);
            return ResponseEntity.ok(gigRepository.save(gig));
        }).orElse(ResponseEntity.notFound().build());
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
