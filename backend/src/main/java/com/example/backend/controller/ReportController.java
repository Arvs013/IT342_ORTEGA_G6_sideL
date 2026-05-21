package com.example.backend.controller;

import com.example.backend.entity.ReportEntity;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody ReportRequest request) {
        if (request.reporterId() == null || request.reportedUserId() == null) {
            return ResponseEntity.badRequest().body("Reporter and reported user are required.");
        }
        if (request.reporterId().equals(request.reportedUserId())) {
            return ResponseEntity.badRequest().body("You cannot report yourself.");
        }
        if (request.reason() == null || request.reason().isBlank()) {
            return ResponseEntity.badRequest().body("Report reason is required.");
        }
        if (request.details() == null || request.details().isBlank()) {
            return ResponseEntity.badRequest().body("Report details are required.");
        }

        return userRepository.findById(request.reporterId()).flatMap(reporter ->
                userRepository.findById(request.reportedUserId()).map(reportedUser -> {
                    ReportEntity report = new ReportEntity();
                    report.setReporter(reporter);
                    report.setReportedUser(reportedUser);
                    report.setReason(request.reason().trim());
                    report.setDetails(request.details().trim());
                    if (request.bookingId() != null) {
                        bookingRepository.findById(request.bookingId()).ifPresent(report::setBooking);
                    }
                    return ResponseEntity.ok(reportRepository.save(report));
                })
        ).orElse(ResponseEntity.notFound().build());
    }

    public record ReportRequest(
            Integer reporterId,
            Integer reportedUserId,
            Integer bookingId,
            String reason,
            String details
    ) {}
}
