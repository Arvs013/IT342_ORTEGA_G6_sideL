package com.example.backend.controller;

import com.example.backend.entity.ReportEntity;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReports(@PathVariable Integer userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<ReportSummary> reports = reportRepository
                .findByReporter_UserIDOrReportedUser_UserIDOrderByCreatedAtDesc(userId, userId)
                .stream()
                .map(report -> new ReportSummary(
                        report.getReportID(),
                        new UserSummary(
                                report.getReporter().getUserID(),
                                report.getReporter().getFirstname(),
                                report.getReporter().getLastname(),
                                report.getReporter().getEmail()
                        ),
                        new UserSummary(
                                report.getReportedUser().getUserID(),
                                report.getReportedUser().getFirstname(),
                                report.getReportedUser().getLastname(),
                                report.getReportedUser().getEmail()
                        ),
                        report.getBooking() == null ? null : report.getBooking().getBookingID(),
                        report.getReason(),
                        report.getDetails(),
                        report.getStatus(),
                        report.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(reports);
    }

    public record ReportRequest(
            Integer reporterId,
            Integer reportedUserId,
            Integer bookingId,
            String reason,
            String details
    ) {}

    public record UserSummary(
            Integer userID,
            String firstname,
            String lastname,
            String email
    ) {}

    public record ReportSummary(
            Integer reportID,
            UserSummary reporter,
            UserSummary reportedUser,
            Integer bookingId,
            String reason,
            String details,
            String status,
            LocalDateTime createdAt
    ) {}
}
