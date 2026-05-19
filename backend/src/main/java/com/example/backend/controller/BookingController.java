package com.example.backend.controller;

import com.example.backend.entity.BookingEntity;
import com.example.backend.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    // 1. Book a service
    @PostMapping
    public ResponseEntity<BookingEntity> createBooking(@RequestBody BookingEntity booking) {
        booking.setStatus("PENDING");
        return ResponseEntity.ok(bookingRepository.save(booking));
    }

    // 2. View booking history as a Client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<BookingEntity>> getClientBookings(@PathVariable Integer clientId) {
        return ResponseEntity.ok(bookingRepository.findByClient_UserID(clientId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingEntity> getBooking(@PathVariable Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. View incoming jobs as a Provider
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<BookingEntity>> getProviderJobs(@PathVariable Integer providerId) {
        return ResponseEntity.ok(bookingRepository.findByGig_Provider_UserID(providerId));
    }

    // 4. Provider updates booking/job status
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam String status) {

        return bookingRepository.findById(bookingId).map(booking -> {
            String upperStatus = status.toUpperCase();
            if (!List.of("PENDING", "ACCEPTED", "IN_PROGRESS", "REJECTED", "COMPLETED", "CANCELLED").contains(upperStatus)) {
                return ResponseEntity.badRequest().body("Invalid booking status.");
            }

            booking.setStatus(upperStatus);
            return ResponseEntity.ok(bookingRepository.save(booking));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{bookingId}/receipt")
    public ResponseEntity<?> submitReceipt(
            @PathVariable Integer bookingId,
            @RequestBody BookingEntity input) {

        return bookingRepository.findById(bookingId).map(booking -> {
            if (input.getReceiptUrl() == null || input.getReceiptUrl().isBlank()) {
                return ResponseEntity.badRequest().body("Receipt image is required.");
            }

            booking.setReceiptUrl(input.getReceiptUrl());
            booking.setStatus("COMPLETED");
            return ResponseEntity.ok(bookingRepository.save(booking));
        }).orElse(ResponseEntity.notFound().build());
    }
}
