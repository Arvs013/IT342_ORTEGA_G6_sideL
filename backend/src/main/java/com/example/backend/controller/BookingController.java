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

    // 3. View incoming jobs as a Provider
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<BookingEntity>> getProviderJobs(@PathVariable Integer providerId) {
        return ResponseEntity.ok(bookingRepository.findByGig_Provider_UserID(providerId));
    }
}
